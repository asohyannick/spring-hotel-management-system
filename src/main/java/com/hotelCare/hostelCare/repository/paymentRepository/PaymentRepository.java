package com.hotelCare.hostelCare.repository.paymentRepository;
import com.hotelCare.hostelCare.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByReference(String reference);

    List<Payment> findByUserId(UUID userId);

    List<Payment> findByBookingId(UUID bookingId);

    boolean existsByBookingId(UUID bookingId);
}
