package com.hotelCare.hostelCare.controller.auth;
import com.hotelCare.hostelCare.config.customResponseMessge.CustomResponseMessage;
import com.hotelCare.hostelCare.dto.user.AuthRequestDto;
import com.hotelCare.hostelCare.dto.user.LoginRequestDto;
import com.hotelCare.hostelCare.dto.user.UserResponseDto;
import com.hotelCare.hostelCare.dto.user.VerifyOTP;
import com.hotelCare.hostelCare.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/${api.version}/auth")
@Tag(name = "Authentication & User Management Endpoints", description = "User and account management endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Register a new user account", description = "Creates a new user account and sends a 2FA OTP to the user's email for verification")
    @PostMapping("/register")
    public ResponseEntity<CustomResponseMessage<UserResponseDto>> createAccount(
          @Valid @RequestBody AuthRequestDto authRequestDto,
            HttpServletResponse response
    ) {
        UserResponseDto savedUser = userService.createAccount(authRequestDto, response);

        // Always notify OTP is sent
        String message = "Account created successfully. An OTP has been sent to your email for verification.";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponseMessage<>(message, HttpStatus.CREATED.value(), savedUser));
    }

    @Operation(summary = "User login", description = "Login using email and password. If OTP verification is required, the user will be notified. JWT tokens are set in HttpOnly cookies after successful login")
    @PostMapping("/login")
    public ResponseEntity<CustomResponseMessage<UserResponseDto>> login(
           @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        UserResponseDto userResponse = userService.login(loginRequestDto, response);

        // Dynamic message: whether account needs OTP verification
        String message = userResponse.accountVerified()
                ? "Login successful"
                : "OTP sent to your email. Please verify your account before logging in.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), userResponse));
    }

    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the user's email to activate the account and generate JWT tokens")
    @PostMapping("/verify-otp")
    public ResponseEntity<CustomResponseMessage<UserResponseDto>> verifyOTP(
           @Valid @RequestBody VerifyOTP verifyOTP,
            HttpServletResponse response
    ) {
        UserResponseDto verifiedUser = userService.verifyOTP(verifyOTP, response);
        return ResponseEntity.ok(new CustomResponseMessage<>(
                "OTP verified successfully. Account activated and ready to login.",
                HttpStatus.OK.value(),
                verifiedUser
        ));
    }

    @Operation(summary = "Logout user", description = "Logs out the user by clearing JWT tokens from cookies")
    @PostMapping("/logout")
    public ResponseEntity<CustomResponseMessage<Void>> logoutUser(HttpServletResponse response) {
        userService.logoutUser(response);

        return ResponseEntity.ok(new CustomResponseMessage<>(
                "Logout successful. JWT cookies cleared.",
                HttpStatus.OK.value(),
                null
        ));
    }
}
