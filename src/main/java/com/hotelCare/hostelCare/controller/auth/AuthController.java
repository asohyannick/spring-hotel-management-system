package com.hotelCare.hostelCare.controller.auth;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("/api/${api.version}/auth")
@Tag(name = "Authentication & User Management Endpoints", description = "User and account management endpoints")
public class AuthController {
    @GetMapping("all-users")
     public List<String> getUsers() {
     return List.of("user1", "user2", "user3");
    };

    @GetMapping("users")
    public List<String> getPeople() {
        return List.of("John", "Peter", "James");
    };
}
