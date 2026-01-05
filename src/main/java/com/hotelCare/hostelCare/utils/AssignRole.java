package com.hotelCare.hostelCare.utils;
import com.hotelCare.hostelCare.enums.UserRole;
public class AssignRole {
    private static final String ADMIN_EMAIL = "admin@hostelcare.com";
    public static UserRole assignRole(String email) {
        if (email.equalsIgnoreCase(ADMIN_EMAIL)) {
            return UserRole.ADMIN;
        }
        return UserRole.CUSTOMER;
    }
}
