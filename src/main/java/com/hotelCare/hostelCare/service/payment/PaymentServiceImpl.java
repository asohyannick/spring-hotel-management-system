package com.hotelCare.hostelCare.service.payment;
import com.hotelCare.hostelCare.dto.payment.PaymentRequestDto;
import com.hotelCare.hostelCare.dto.payment.PaymentResponseDto;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.entity.payment.Payment;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.PaymentStatus;
import com.hotelCare.hostelCare.enums.ProviderType;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import com.hotelCare.hostelCare.mappers.paymentMapper.PaymentMapper;
import com.hotelCare.hostelCare.repository.bookingRepository.BookingRepository;
import com.hotelCare.hostelCare.repository.paymentRepository.PaymentRepository;
import com.hotelCare.hostelCare.repository.userRepository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    private void ensureStripeInitialized() {
        if (Stripe.apiKey == null || Stripe.apiKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey;
        }
    }

    private long toSmallestUnit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }
        return amount.setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();
    }

    private String generateReference() {
        return "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    private PaymentResponseDto createStripeIntentForPayment(Payment payment) {
        long amountSmallest = toSmallestUnit(payment.getAmount());
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountSmallest)
                    .setCurrency(payment.getCurrency().name().toLowerCase())
                    .setDescription(payment.getPurpose() == null ? "Hotel booking payment" : payment.getPurpose())
                    .putMetadata("paymentId", payment.getId().toString())
                    .putMetadata("bookingId", payment.getBooking().getId().toString())
                    .putMetadata("userId", payment.getUser().getId().toString())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            payment.setStripePaymentIntentId(intent.getId());
            payment.setProviderMessage("Stripe PaymentIntent created");
            Payment saved = paymentRepository.saveAndFlush(payment);
            return paymentMapper.toResponseDto(saved);
        } catch (StripeException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setProviderMessage("Stripe error: " + e.getMessage());
            paymentRepository.save(payment);
            throw new BadRequestException("Stripe error: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto request) {

        if (request == null) throw new BadRequestException("Payment request is required");
        if (request.bookingId() == null) throw new BadRequestException("bookingId is required");
        if (request.userId() == null) throw new BadRequestException("userId is required");
        if (request.amount() == null) throw new BadRequestException("amount is required");
        if (request.currency() == null) throw new BadRequestException("currency is required");

        ProviderType provider = request.provider() == null ? ProviderType.STRIPE : request.provider();

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new NotFoundException("Booking not found: " + request.bookingId()));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.userId()));

        if (paymentRepository.existsByBookingId(request.bookingId())) {
            throw new BadRequestException("Payment already exists for this booking");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .user(user)
                .amount(request.amount())
                .currency(request.currency())
                .provider(provider)
                .status(PaymentStatus.PENDING)
                .purpose(request.purpose())
                .method(request.method())
                .reference(generateReference())
                .metadata(request.method())
                .providerMessage("Payment initialized")
                .build();

        Payment saved = paymentRepository.save(payment);

        if (provider == ProviderType.STRIPE) {
            ensureStripeInitialized();
            return createStripeIntentForPayment(saved);
        }

        if (provider == ProviderType.PAYPAL) {
            saved.setProviderMessage("PayPal integration not implemented yet");
            return paymentMapper.toResponseDto(paymentRepository.save(saved));
        }

        throw new BadRequestException("Unsupported payment provider: " + provider);
    }



    @Override
    public PaymentResponseDto getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    public PaymentResponseDto getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + reference));
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(paymentMapper::toResponseDto)
                .toList();
    }

    @Override
    public PaymentResponseDto updatePaymentStatus(UUID paymentId, PaymentStatus status, String providerMessage) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        if (status == null) throw new BadRequestException("status is required");

        payment.setStatus(status);
        if (providerMessage != null && !providerMessage.isBlank()) {
            payment.setProviderMessage(providerMessage);
        }

        if (status == PaymentStatus.SUCCEEDED && payment.getPaidAt() == null) payment.setPaidAt(Instant.now());
        if (status == PaymentStatus.CANCELLED && payment.getCancelledAt() == null) payment.setCancelledAt(Instant.now());
        if (status == PaymentStatus.REFUNDED && payment.getRefundedAt() == null) payment.setRefundedAt(Instant.now());

        return paymentMapper.toResponseDto(paymentRepository.save(payment));
    }

    @Override
    public void attachProviderReference(UUID paymentId, ProviderType provider, String providerReference) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        if (provider == null) throw new BadRequestException("provider is required");
        if (providerReference == null || providerReference.isBlank()) throw new BadRequestException("providerReference is required");

        payment.setProvider(provider);

        if (provider == ProviderType.STRIPE) {
            payment.setStripePaymentIntentId(providerReference);
        } else if (provider == ProviderType.PAYPAL) {
            payment.setPaypalOrderId(providerReference);
        } else {
            throw new BadRequestException("Unsupported provider: " + provider);
        }

        paymentRepository.save(payment);
    }

    @Override
    public PaymentResponseDto cancelPayment(UUID paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            throw new BadRequestException("Cannot cancel a PAID payment. Refund it instead.");
        }

        if (payment.getProvider() == ProviderType.STRIPE) {
            ensureStripeInitialized();

            if (payment.getStripePaymentIntentId() == null || payment.getStripePaymentIntentId().isBlank()) {
                throw new BadRequestException("Stripe PaymentIntent ID is missing for this payment");
            }

            try {
                PaymentIntent intent = PaymentIntent.retrieve(payment.getStripePaymentIntentId());
                intent.cancel(PaymentIntentCancelParams.builder().build());

                payment.setStatus(PaymentStatus.CANCELLED);
                payment.setCancelledAt(Instant.now());
                payment.setProviderMessage(reason == null ? "Cancelled" : reason);

                return paymentMapper.toResponseDto(paymentRepository.save(payment));
            } catch (StripeException e) {
                throw new BadRequestException("Stripe cancel error: " + e.getMessage());
            }
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setCancelledAt(Instant.now());
        payment.setProviderMessage(reason == null ? "Cancelled" : reason);
        return paymentMapper.toResponseDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponseDto refundPayment(UUID paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new BadRequestException("Cancelled payments cannot be refunded");
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BadRequestException("Payment has already been refunded");
        }

        if(payment.getStatus() == PaymentStatus.PENDING) {
            throw new BadRequestException("Payment has already been refunded");
        }

        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new BadRequestException("Only SUCCEEDED payments can be refunded");
        }

        if (payment.getProvider() == ProviderType.STRIPE) {
            ensureStripeInitialized();

            if (payment.getStripePaymentIntentId() == null || payment.getStripePaymentIntentId().isBlank()) {
                throw new BadRequestException("Stripe PaymentIntent ID is missing for this payment");
            }

            try {
                com.stripe.model.PaymentIntent intent =
                        com.stripe.model.PaymentIntent.retrieve(payment.getStripePaymentIntentId());

                if (!"succeeded".equalsIgnoreCase(intent.getStatus())) {
                    if ("requires_payment_method".equals(intent.getStatus())
                            || "requires_confirmation".equals(intent.getStatus())
                            || "requires_action".equals(intent.getStatus())
                            || "processing".equals(intent.getStatus())) {

                        intent.cancel();

                        payment.setStatus(PaymentStatus.CANCELLED);
                        payment.setCancelledAt(Instant.now());
                        payment.setProviderMessage(
                                (reason == null || reason.isBlank())
                                        ? "Stripe intent cancelled (refund not possible because payment was not completed)"
                                        : ("Cancelled: " + reason)
                        );

                        return paymentMapper.toResponseDto(paymentRepository.save(payment));
                    }

                    throw new BadRequestException(
                            "Cannot refund: Stripe PaymentIntent status is '" + intent.getStatus()
                                    + "'. It has no successful charge."
                    );
                }

                String chargeId = payment.getStripeChargeId();
                if (chargeId == null || chargeId.isBlank()) {
                    chargeId = intent.getLatestCharge();
                }

                if (chargeId == null || chargeId.isBlank()) {
                    throw new BadRequestException("Cannot refund: no Stripe charge found for this PaymentIntent");
                }

                RefundCreateParams params = RefundCreateParams.builder()
                        .setCharge(chargeId)
                        .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                        .build();

                Refund refund = Refund.create(params);

                payment.setStatus(PaymentStatus.REFUNDED);
                payment.setRefundedAt(Instant.now());
                payment.setProviderMessage((reason == null || reason.isBlank())
                        ? ("Refund created: " + refund.getId())
                        : (reason + " | refundId=" + refund.getId()));

                return paymentMapper.toResponseDto(paymentRepository.save(payment));

            } catch (StripeException e) {
                throw new BadRequestException("Stripe refund error: " + e.getMessage());
            }
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(Instant.now());
        payment.setProviderMessage(reason == null ? "Refunded" : reason);
        return paymentMapper.toResponseDto(paymentRepository.save(payment));
    }


    @Override
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponseDto)
                .toList();
    }

    @Override
    public long countPayments() {
        return paymentRepository.count();
    }
}

