package com.hotelCare.hostelCare.utils;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.AccountStatus;
import com.hotelCare.hostelCare.enums.UserRole;
import com.hotelCare.hostelCare.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.app.seed-admin:false}")
    private boolean seedAdmin;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 AdminSeeder starting...");
        log.info("✅ seedAdmin flag: {}", seedAdmin);
        log.info("✅ Admin Email loaded: {}", adminEmail);
        log.info("✅ Admin Password loaded? {}", adminPassword != null && !adminPassword.isBlank());

        if (!seedAdmin) {
            log.info("⚠️ seedAdmin is false, skipping admin creation.");
            return;
        }

        if (adminEmail == null || adminEmail.isBlank()) {
            log.error("❌ Admin email is missing. Check application properties.");
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            log.error("❌ Admin password is missing. Check application properties.");
            return;
        }

        String email = adminEmail.trim().toLowerCase();

        User admin = userRepository.findByEmail(email).orElseGet(User::new);

        admin.setRole(UserRole.SUPER_ADMIN);
        admin.setFirstName("Admin");
        admin.setLastName("SuperAdmin");
        admin.setEmail(email);

        admin.setPassword(passwordEncoder.encode(adminPassword));

        admin.setStatus(AccountStatus.VERIFIED);
        admin.setIsAccountActive(true);
        admin.setIsAccountVerified(true);
        admin.setIsAccountBlocked(false);
        admin.setIsAccountConfirmed(true);

        userRepository.save(admin);

        log.info("✅ Admin upserted successfully: {}", email);
        log.info("DEBUG: raw password matches? {}", passwordEncoder.matches(adminPassword, admin.getPassword()));
    }
}
