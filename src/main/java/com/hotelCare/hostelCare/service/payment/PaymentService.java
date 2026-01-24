package com.hotelCare.hostelCare.service.payment;
import com.hotelCare.hostelCare.dto.payment.PaymentRequestDto;
import com.hotelCare.hostelCare.dto.payment.PaymentResponseDto;
import com.hotelCare.hostelCare.enums.PaymentStatus;
import com.hotelCare.hostelCare.enums.ProviderType;
import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto request);


    PaymentResponseDto getPaymentById(UUID paymentId);

    PaymentResponseDto getPaymentByReference(String reference);

    List<PaymentResponseDto> getPaymentsByUserId(UUID userId);

    List<PaymentResponseDto> getPaymentsByBookingId(UUID bookingId);

    PaymentResponseDto updatePaymentStatus(
            UUID paymentId,
            PaymentStatus status,
            String providerMessage
    );

    void attachProviderReference(
            UUID paymentId,
            ProviderType provider,
            String providerReference
    );

    PaymentResponseDto cancelPayment(UUID paymentId, String reason);

    PaymentResponseDto refundPayment(UUID paymentId, String reason);

    List<PaymentResponseDto> getAllPayments();

    long countPayments();
}
