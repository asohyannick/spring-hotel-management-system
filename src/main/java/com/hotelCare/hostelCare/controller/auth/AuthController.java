package com.hotelCare.hostelCare.controller.auth;
import com.hotelCare.hostelCare.config.JWTConfig.JWTTokenGenerationLogic;
import com.hotelCare.hostelCare.config.customResponseMessge.CustomResponseMessage;
import com.hotelCare.hostelCare.dto.user.*;
import com.hotelCare.hostelCare.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/auth")
@Tag(name = "Authentication & User Management Endpoints", description = "User and account management endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private  final JWTTokenGenerationLogic jwtTokenGenerationLogic;

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

        String message = userResponse.accountVerified()
                ? "Login successful"
                : "OTP sent to your email. Please verify your account before logging in.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), userResponse));
    }

    @Operation(
            summary = "Admin / Super Admin Login",
            description = "Allows ADMIN or SUPER_ADMIN users to authenticate and receive JWT tokens",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Admin authenticated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Account blocked or insufficient privileges",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    )
            }
    )
    @PostMapping("/admin-login")
    public ResponseEntity<CustomResponseMessage<UserResponseDto>> adminLogin(
            @Valid @RequestBody AdminRequestDto dto,
            HttpServletResponse response
    ) {
        LoginResult result = userService.superAdminLogin(dto);

        Cookie accessCookie = new Cookie("accessToken", result.accessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // true in prod
        accessCookie.setPath("/");
        accessCookie.setMaxAge(jwtTokenGenerationLogic.getAccessTokenExpirationSeconds());
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", result.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Super Admin logged in successfully",
                        HttpStatus.OK.value(),
                        result.user()
                )
        );
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

    @Operation(
            summary = "Fetch all users",
            description = "Retrieve a list of all registered users in the system. "
                    + "Accessible only to ADMIN and SUPER_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges")
    })
    @GetMapping("/all-users")
    public ResponseEntity<CustomResponseMessage<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "All users fetched successfully",
                        HttpStatus.OK.value(),
                        users
                )
        );
    }

    @Operation(
            summary = "Fetch a user by ID",
            description = "Retrieve details of a specific user using their unique ID. "
                    + "Accessible only to ADMIN and SUPER_ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden – insufficient privileges")
    })
    @GetMapping("/fetch-user/{userId}")
    public ResponseEntity<CustomResponseMessage<UserResponseDto>> getUserById(
            @Parameter(
                    description = "Unique identifier of the user",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID userId
    ) {
        UserResponseDto user = userService.getUserById(userId);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "User fetched successfully",
                        HttpStatus.OK.value(),
                        user
                )
        );
    }

    @Operation(
            summary = "Delete a user",
            description = "Permanently delete a user account from the system. "
                    + "This action is restricted to SUPER_ADMIN only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden – SUPER_ADMIN access required")
    })
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<CustomResponseMessage<Void>> deleteUser(
            @Parameter(
                    description = "Unique identifier of the user to be deleted",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID userId
    ) {
        userService.deleteUser(userId);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "User deleted successfully",
                        HttpStatus.OK.value(),
                        null
                )
        );
    }

    @Operation(summary = "Block a customer", description = "Blocks a customer account by userId")
    @PatchMapping("/block-user/{userId}")
    public ResponseEntity<CustomResponseMessage<UserResponseDto>> blockUser(
            @Parameter(
                    description = "UUID of the user to block",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable @NotNull UUID userId
    ) {
        UserResponseDto user = userService.blockCustomer(userId);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "User blocked successfully",
                        HttpStatus.OK.value(),
                        user
                )
        );
    }

    @Operation(summary = "Unblock a customer", description = "Unblocks a customer account by userId")
    @PatchMapping("/unblock-user/{userId}")
    public ResponseEntity<CustomResponseMessage<UserResponseDto>> unblockUser(
            @Parameter(
                    description = "UUID of the user to unblock",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable @NotNull UUID userId
    ) {
        UserResponseDto user = userService.unBlockCustomer(userId);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "User unblocked successfully",
                        HttpStatus.OK.value(),
                        user
                )
        );
    }

    @Operation(
            summary = "Count total users",
            description = "Returns the total number of users in the system"
    )
    @GetMapping("/count-users")
    public ResponseEntity<CustomResponseMessage<Long>> countTotalUsers() {

        long totalUsers = userService.countTotalUsers();

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Total users fetched successfully",
                        HttpStatus.OK.value(),
                        totalUsers
                )
        );
    }

}



