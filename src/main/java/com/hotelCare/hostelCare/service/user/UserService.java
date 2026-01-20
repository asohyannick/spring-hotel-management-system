package com.hotelCare.hostelCare.service.user;
import com.hotelCare.hostelCare.config.JWTConfig.JWTTokenGenerationLogic;
import com.hotelCare.hostelCare.config.firebaseConfig.FirebaseConfig;
import com.hotelCare.hostelCare.dto.user.*;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.AccountStatus;
import com.hotelCare.hostelCare.enums.UserRole;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import com.hotelCare.hostelCare.mappers.authMapper.AuthMapper;
import com.hotelCare.hostelCare.repository.userRepository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final JWTTokenGenerationLogic jwtTokenGenerationLogic;
    private final FirebaseConfig firebaseConfig;
    private final AuthMapper authMapper;
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender javaMailSender,
            JWTTokenGenerationLogic jwtTokenGenerationLogic,
            FirebaseConfig firebaseConfig,
            AuthMapper authMapper
            ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.jwtTokenGenerationLogic = jwtTokenGenerationLogic;
        this.firebaseConfig = firebaseConfig;
        this.authMapper = authMapper;
    }

    public String generate2FACode(User savedUser) {
         String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
         savedUser.setTwoFactorExpiryTime(LocalDateTime.now().plusHours(5));
         savedUser.setTwoFactorAttemptsLeft(0);
         savedUser.setTwoFactorCode(otp);
         userRepository.save(savedUser);
         return otp;
    }

    private void send2FACodeEmail(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("HostelCare Account Verification: Your 2FA Security Code");

            String htmlContent = """
        <div style="margin:0;padding:0;background-color:#f5f7fb;font-family:Arial,Helvetica,sans-serif;">
          <div style="max-width:640px;margin:30px auto;background:#ffffff;
                      border:1px solid #e5e7eb;border-radius:12px;
                      box-shadow:0 6px 18px rgba(17,24,39,0.06);overflow:hidden;">

            <!-- Header -->
            <div style="padding:22px 24px;
                        background:linear-gradient(135deg,#0f172a,#1d4ed8);
                        color:#ffffff;">
              <h2 style="margin:0;font-size:18px;">HostelCare Security Team</h2>
              <p style="margin:6px 0 0;font-size:13px;opacity:0.9;">
                Two-Factor Authentication (2FA)
              </p>
            </div>

            <!-- Content -->
            <div style="padding:24px;color:#1f2937;font-size:14px;line-height:1.6;">
              <p>Dear customer,</p>

              <p>Thank you for securing your account.</p>

              <p>
                To verify your identity and complete your login or registration,
                please use the following Two-Factor Authentication (2FA) code:
              </p>

              <!-- Code Box -->
              <div style="margin:20px 0;padding:18px;
                          background:#eff6ff;border:1px solid #bfdbfe;
                          border-radius:12px;text-align:center;">
                <p style="margin:0 0 10px;font-size:12px;
                          text-transform:uppercase;letter-spacing:2px;
                          font-weight:bold;color:#1e40af;">
                  Your Verification Code
                </p>
                <div style="display:inline-block;
                            padding:10px 16px;
                            font-size:28px;
                            font-weight:800;
                            letter-spacing:6px;
                            font-family:'Courier New',Courier,monospace;
                            background:#ffffff;
                            border:1px solid #bfdbfe;
                            border-radius:10px;
                            color:#0b1220;">
                  %s
                </div>
              </div>

              <!-- Security Notice -->
              <div style="margin-top:20px;padding:14px;
                          background:#fffbeb;
                          border-left:4px solid #f59e0b;
                          border-radius:10px;">
                <p style="margin:0 0 8px;font-weight:bold;color:#92400e;">
                  ⚠️ Important Security Notice
                </p>
                <ol style="margin:0;padding-left:18px;color:#78350f;">
                  <li>This code is valid for the next <strong>5 minutes</strong>.</li>
                  <li><strong>Do not share this code</strong> with anyone, including Mercado employees.</li>
                </ol>
              </div>

              <p style="margin-top:20px;color:#6b7280;font-size:13px;">
                If you did not attempt to access or register a Mercado account,
                please disregard this email. Your account security remains intact.
              </p>

              <p style="margin-top:18px;">
                If you have any questions, please contact our support team.
              </p>

              <p style="margin-top:20px;">
                Sincerely,<br/>
                <strong>The HostelCare Team</strong> led by Asoh Yannick
              </p>
            </div>

            <!-- Footer -->
            <div style="padding:16px 24px;
                        background:#f9fafb;
                        border-top:1px solid #e5e7eb;
                        font-size:12px;
                        color:#6b7280;">
              This is an automated security message. Please do not reply.
            </div>

          </div>
        </div>
        """.formatted(code);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send 2FA email", e);
        }
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

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= 5) {
            user.setIsAccountBlocked(true);
            user.setStatus(AccountStatus.SUSPENDED);
        }

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

        // 🔑 Generate JWT tokens after verification
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

    public User resentOTP(ResendOTPDto resendOTPDto) {
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

        return user;
    }

    public void logoutUser(HttpServletResponse response) {
        // Clear cookies
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
        User saved = userRepository.save(user);
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
        String html = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <title>Password Reset</title>
        </head>
        <body style="margin:0;padding:0;background:#f6f9fc;font-family:Arial,Helvetica,sans-serif;">
          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f6f9fc;padding:24px 0;">
            <tr>
              <td align="center">
                <table role="presentation" width="600" cellspacing="0" cellpadding="0"
                       style="width:600px;max-width:92%%;background:#ffffff;border-radius:12px;overflow:hidden;
                              box-shadow:0 6px 18px rgba(0,0,0,0.08);">
                  <tr>
                    <td style="background:#0b5ed7;padding:18px 24px;color:#ffffff;">
                      <h2 style="margin:0;font-size:18px;line-height:1.4;">Password Reset Verification</h2>
                    </td>
                  </tr>

                  <tr>
                    <td style="padding:24px;color:#1f2937;">
                      <p style="margin:0 0 14px;font-size:14px;line-height:1.6;">
                        Hi there, 👋
                      </p>

                      <p style="margin:0 0 16px;font-size:14px;line-height:1.6;">
                        We received a request to reset your password. Use the verification code below to continue:
                      </p>

                      <div style="text-align:center;margin:22px 0;">
                        <span style="display:inline-block;background:#f3f4f6;border:1px solid #e5e7eb;
                                     padding:14px 20px;border-radius:10px;font-size:28px;letter-spacing:6px;
                                     font-weight:700;color:#111827;">
                          %s
                        </span>
                      </div>

                      <p style="margin:0 0 10px;font-size:13px;line-height:1.6;color:#374151;">
                        ⏳ This code will expire in <strong>15 minutes</strong>.
                      </p>

                      <p style="margin:0;font-size:13px;line-height:1.6;color:#6b7280;">
                        If you didn’t request this, you can safely ignore this email.
                      </p>
                    </td>
                  </tr>

                  <tr>
                    <td style="padding:16px 24px;background:#f9fafb;border-top:1px solid #e5e7eb;color:#6b7280;">
                      <p style="margin:0;font-size:12px;line-height:1.5;">
                        © %d Your App. All rights reserved.
                      </p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(resetCode, java.time.Year.now().getValue());
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(forgotPasswordDto.email());
            helper.setSubject("Password Reset Verification Code");
            helper.setText(html, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BadRequestException("Failed to send password reset email", e);
        }
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


}
