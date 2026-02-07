package com.hotelCare.hostelCare.service.user;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.hotelCare.hostelCare.config.JWTConfig.JWTTokenGenerationLogic;
import com.hotelCare.hostelCare.config.JavaMailSenderConfig.ElasticEmailService;
import com.hotelCare.hostelCare.dto.user.*;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.AccountStatus;
import com.hotelCare.hostelCare.enums.UserRole;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import com.hotelCare.hostelCare.mappers.authMapper.AuthMapper;
import com.hotelCare.hostelCare.repository.userRepository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenGenerationLogic jwtTokenGenerationLogic;
    private final AuthMapper authMapper;
    private final ElasticEmailService elasticEmailService;

    @Value("${FIREBASE_PRIVATE_KEY}")
    private String firebasePrivateKey;

    @Value("${FIREBASE_CLIENT_EMAIL}")
    private String firebaseClientEmail;

    @Value("${FIREBASE_PROJECT_ID}")
    private String firebaseProjectId;

    private User createUserFromFirebase(FirebaseToken token) {
        User newUser = new User();
        newUser.setEmail(token.getEmail().trim().toLowerCase());
        newUser.setFirstName(token.getName() != null ? token.getName().trim().split(" ")[0] : "CUSTOMER");
        newUser.setLastName(token.getName() != null && token.getName().trim().contains(" ") ? token.getName().split(" ")[1] : "CUSTOMER");
        newUser.setIsAccountVerified(token.isEmailVerified());
        newUser.setRole(UserRole.valueOf("CUSTOMER"));
        newUser.setIsAccountBlocked(false);
        return userRepository.save(newUser);
    }

    public String generate2FACode(User savedUser) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        savedUser.setTwoFactorExpiryTime(LocalDateTime.now().plusMinutes(5)); // Changed to 5 minutes
        savedUser.setTwoFactorAttemptsLeft(3);
        savedUser.setTwoFactorCode(otp);
        userRepository.save(savedUser);
        return otp;
    }

    public void send2FACodeEmail(String toEmail, String code) {
        elasticEmailService.send2FACodeEmail(toEmail, code);
    }

    private void activateUser(User user) {
        user.setTwoFactorCode(null);
        user.setTwoFactorExpiryTime(null);
        user.setTwoFactorAttemptsLeft(0);
        user.setIsAccountActive(true);
        user.setIsAccountVerified(true);
        user.setIsAccountBlocked(false);
        user.setIsAccountConfirmed(true);
        user.setStatus(AccountStatus.VERIFIED);
        userRepository.save(user);
    }

    public UserResponseDto createAccount(AuthRequestDto authRequestDto, HttpServletResponse response) {

        if (userRepository.existsByEmail(authRequestDto.email().trim().toLowerCase())) {
            throw new BadRequestException("Email already exists");
        }

        User newUser = authMapper.toUser(authRequestDto);

        newUser.setRole(UserRole.CUSTOMER);

        newUser.setPassword(passwordEncoder.encode(authRequestDto.password()));

        User savedUser = userRepository.save(newUser);

        String otpCode = generate2FACode(savedUser);
        send2FACodeEmail(savedUser.getEmail(), otpCode);

        return authMapper.toUserResponseDto(savedUser);
    }

    public UserResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() -> new NotFoundException("User with email " + loginRequestDto.email() + " not found"));

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (Boolean.TRUE.equals(user.getIsAccountBlocked())) {
            throw new BadRequestException("Account is blocked. Contact support.");
        }

        if (!user.getIsAccountVerified()) {
            String otpCode = generate2FACode(user);
            send2FACodeEmail(user.getEmail(), otpCode);
            throw new BadRequestException("Account requires OTP verification. Please verify your email.");
        }

        String accessToken = jwtTokenGenerationLogic.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenGenerationLogic.generateRefreshToken(user.getEmail());

        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 6️⃣ Set cookies
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return authMapper.toUserResponseDto(user);
    }

    public LoginResult superAdminLogin(AdminRequestDto dto) {

        User admin = userRepository.findByEmail(dto.email().trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Admin not found"));

        if (!passwordEncoder.matches(dto.password(), admin.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        if (Boolean.TRUE.equals(admin.getIsAccountBlocked())) {
            throw new BadRequestException("Account blocked. Contact support.");
        }

        admin.setRole(UserRole.SUPER_ADMIN);

        String accessToken = jwtTokenGenerationLogic.generateAccessToken(admin.getEmail(), admin.getRole().name());
        String refreshToken = jwtTokenGenerationLogic.generateRefreshToken(admin.getEmail());

        admin.setRefreshToken(refreshToken);
        userRepository.save(admin);

        return new LoginResult(authMapper.toUserResponseDto(admin), accessToken, refreshToken);
    }

    public UserResponseDto verifyOTP(VerifyOTP verifyOTP, HttpServletResponse response) {
        User user = userRepository.findByTwoFactorCode(verifyOTP.twoFactorCode())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getTwoFactorCode() == null ||
                !user.getTwoFactorCode().equals(verifyOTP.twoFactorCode()) ||
                user.getTwoFactorExpiryTime() == null ||
                user.getTwoFactorExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired OTP code");
        }

        activateUser(user);

        String accessToken = jwtTokenGenerationLogic.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenGenerationLogic.generateRefreshToken(user.getEmail());
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        // ✅ Set cookies after OTP verification
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return authMapper.toUserResponseDto(user);
    }

    public void resentOTP(ResendOTPDto resendOTPDto) {
        User user = userRepository.findByEmail(resendOTPDto.email())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (Boolean.TRUE.equals(user.getIsAccountVerified())) {
            throw  new BadRequestException("User is already verified");
        }
        if(Boolean.TRUE.equals(user.getIsAccountBlocked())) {
            throw new BadRequestException("User is already blocked. OTP can't be sent to blocked accounts");
        }
        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() >= 5) {
            throw new BadRequestException(
                    "You've reached the maximum attempts to request an OTP for this account. Please try again later"
            );
        }
        String otp = generate2FACode(user);
        user.setTwoFactorCode(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        send2FACodeEmail(user.getEmail(), otp);
    }

    public void logoutUser(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    public List<UserResponseDto> getAllUsers() {
        return  userRepository.findAll()
                .stream()
                .map(authMapper::toUserResponseDto)
                .toList();
    }

    public  UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        return  authMapper.toUserResponseDto(user);
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found!")
        );
        userRepository.delete(user);
    }

    public UserResponseDto blockCustomer(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        if (Boolean.TRUE.equals(user.getIsAccountBlocked())){
            throw new BadRequestException("User account is already blocked!");
        }
        user.setIsAccountBlocked(true);
        User saved = userRepository.save(user);
        return  authMapper.toUserResponseDto(saved);
    }

    public UserResponseDto unBlockCustomer(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        if (!Boolean.TRUE.equals(user.getIsAccountBlocked())) {
            throw  new BadRequestException("User account is not blocked");
        }
        user.setIsAccountBlocked(false);
        userRepository.save(user);
        return authMapper.toUserResponseDto(user);
    }

    public long countTotalUsers() {
        return userRepository.count();
    }

    public String forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userRepository.findByEmail(forgotPasswordDto.email()).orElseThrow(
                () -> new NotFoundException("User not found.")
        );

        String resetCode = generate2FACode(user);
        user.setTwoFactorCode(resetCode);
        user.setTwoFactorExpiryTime(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        elasticEmailService.sendPasswordResetEmail(forgotPasswordDto.email(), resetCode);

        return user.getEmail();
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Invalid or expired reset token"));

        if (!resetPasswordDto.password().equals(resetPasswordDto.confirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        if (!user.getId().equals(userId)) {
            throw new BadRequestException("Reset token does not match this user");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordDto.password()));
        user.setTwoFactorCode(null);
        user.setTwoFactorExpiryTime(null);

        userRepository.save(user);
    }

    @PostConstruct
    public void init() throws Exception {
        String privateKey = firebasePrivateKey.replace("\\n", "\n"); // replace escaped newlines
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        com.google.auth.oauth2.GoogleCredentials.fromStream(
                                new ByteArrayInputStream((
                                        "{\n" +
                                                "  \"type\": \"service_account\",\n" +
                                                "  \"project_id\": \"" + firebaseProjectId + "\",\n" +
                                                "  \"private_key_id\": \"ignored\",\n" +
                                                "  \"private_key\": \"" + privateKey + "\",\n" +
                                                "  \"client_email\": \"" + firebaseClientEmail + "\",\n" +
                                                "  \"client_id\": \"ignored\",\n" +
                                                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                                                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                                                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                                                "  \"client_x509_cert_url\": \"ignored\"\n" +
                                                "}").getBytes(StandardCharsets.UTF_8))
                        )
                ).build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public User loginWithGoogle(String googleToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(googleToken);
        if (decodedToken == null || decodedToken.getEmail() == null || decodedToken.getEmail().isBlank()) {
            throw new BadRequestException("Invalid Firebase token");
        }
        String email = decodedToken.getEmail().trim().toLowerCase();
        User auth = userRepository.findByEmail(email)
                .orElseGet(() -> createUserFromFirebase(decodedToken));
        String accessToken = jwtTokenGenerationLogic.generateAccessToken(auth.getEmail().trim().toLowerCase(), String.valueOf(auth.getRole()));
        String refreshToken = jwtTokenGenerationLogic.generateRefreshToken(auth.getEmail().trim().toLowerCase());
        auth.setAccessToken(accessToken);
        auth.setRefreshToken(refreshToken);
        return userRepository.save(auth);
    }
}
