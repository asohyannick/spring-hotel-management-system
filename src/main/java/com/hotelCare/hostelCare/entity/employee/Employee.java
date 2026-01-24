package com.hotelCare.hostelCare.entity.employee;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name = "employees")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(unique = true, length = 150, nullable = false)
    private String email;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 500)
    private String profilePic;

    @Column(nullable = false, length = 100)
    private String jobTitle;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false)
    private LocalDate hireDate;

    private LocalDate terminationDate;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    private String emergencyContactName;

    @Column(length = 20)
    private String emergencyContactPhone;

    @Column(nullable = false)
    private Double salary;

    @Column(length = 50)
    private String salaryType;

    @Column(length = 100)
    private String bankName;

    @Column(length = 100)
    private String bankAccountNumber;

    @Column(length = 50)
    private String taxIdentificationNumber;

    @Column(nullable = false)
    private Boolean canAccessSystem = false;

    @Column(nullable = false)
    private Boolean isOnDuty = false;

    @Column(nullable = false)
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

