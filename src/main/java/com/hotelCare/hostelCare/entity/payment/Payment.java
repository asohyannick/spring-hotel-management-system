package com.hotelCare.hostelCare.entity.payment;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.PaymentCurrency;
import com.hotelCare.hostelCare.enums.PaymentStatus;
import com.hotelCare.hostelCare.enums.ProviderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payments_booking_id", columnList = "booking_id"),
                @Index(name = "idx_payments_user_id", columnList = "user_id"),
                @Index(name = "idx_payments_provider", columnList = "provider"),
                @Index(name = "idx_payments_status", columnList = "status"),
                @Index(name = "idx_payments_stripe_payment_intent", columnList = "stripe_payment_intent_id"),
                @Index(name = "idx_payments_paypal_order", columnList = "paypal_order_id")
        }
)
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderType provider = ProviderType.PAYPAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(length = 120)
    private String purpose;

    @Column(length = 30)
    private String method;

    @Column(name = "reference", unique = true, length = 60)
    private String reference;

    // ---------- Stripe fields ----------
    @Column(name = "stripe_payment_intent_id", unique = true)
    private String stripePaymentIntentId;

    @Column(name = "stripe_charge_id", unique = true)
    private String stripeChargeId;

    // ---------- PayPal fields ----------
    @Column(name = "paypal_order_id", unique = true)
    private String paypalOrderId;

    @Column(name = "paypal_capture_id", unique = true)
    private String paypalCaptureId;

    @Column(length = 600)
    private String providerMessage;

    @Column(name = "metadata", length = 2000)
    private String metadata;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentCurrency currency = PaymentCurrency.USD;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;
}
