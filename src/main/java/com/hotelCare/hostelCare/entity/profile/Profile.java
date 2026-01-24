package com.hotelCare.hostelCare.entity.profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.CountryType;
import com.hotelCare.hostelCare.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
@Table(name = "customer_profile")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @JsonIgnore
    @Column(length = 100)
    private String firstName;

    @JsonIgnore
    @Column(length = 100)
    private String lastName;

    @JsonIgnore
    @Column(unique = true)
    private String email;

    @JsonIgnore
    @Column
    private String profilePic;

    @JsonIgnore
    @Column
    private Integer age;

    @JsonIgnore
    @Column
    private LocalDate dateOfBirth;

    @JsonIgnore
    @Column(length = 100)
    private String region;

    @JsonIgnore
    @Column(length = 100)
    private String city;

    @JsonIgnore
    @Column(length = 100)
    private String address;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private CountryType country = CountryType.UNITED_STATES;

    @JsonIgnore
    @Column(length = 20)
    private String phoneNumber;

    @JsonIgnore
    @Column(length = 20)
    private String emergencyContact;

    @JsonIgnore
    @Column(length = 100)
    private String occupation;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus = MaritalStatus.MARRIED;

    @JsonIgnore
    @Column(length = 500)
    private String specialRequests;

    @JsonIgnore
    @Column
    private Boolean newsletterSubscribed = false;

    @JsonIgnore
    @Column
    private Boolean isProfileCompleted = false;

    @JsonIgnore
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
