package com.hotelCare.hostelCare.controller.payment;
import com.hotelCare.hostelCare.config.customResponseMessge.CustomResponseMessage;
import com.hotelCare.hostelCare.dto.payment.PaymentRequestDto;
import com.hotelCare.hostelCare.dto.payment.PaymentResponseDto;
import com.hotelCare.hostelCare.dto.payment.UpdatePaymentStatusRequest;
import com.hotelCare.hostelCare.enums.PaymentStatus;
import com.hotelCare.hostelCare.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/${api.version}/payment")
@RequiredArgsConstructor
@Tag(name = "Payment Integration Management Endpoints", description = "Payment endpoints (Stripe first)")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Create a payment (Stripe)",
            description = "Creates a payment record and (if provider=STRIPE) creates a Stripe PaymentIntent."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Booking or user not found")
    })
    @PostMapping(value = "/create-payment-intent", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<PaymentResponseDto>> createPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentRequestDto.class))
            )
            @Valid @RequestBody PaymentRequestDto request
    ) {
        PaymentResponseDto created = paymentService.createPayment(request);

        CustomResponseMessage<PaymentResponseDto> body =
                new CustomResponseMessage<>("Payment created successfully", HttpStatus.CREATED.value(), created);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(summary = "Get payment by ID", description = "Fetch a single payment by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping(value = "/fetch-payment/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<PaymentResponseDto>> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable UUID paymentId
    ) {
        PaymentResponseDto data = paymentService.getPaymentById(paymentId);

        CustomResponseMessage<PaymentResponseDto> body =
                new CustomResponseMessage<>("Payment fetched successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get payment by reference", description = "Fetch a single payment by unique reference.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping(value = "/payment-reference/{reference}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<PaymentResponseDto>> getPaymentByReference(
            @Parameter(description = "Unique payment reference") @PathVariable String reference
    ) {
        PaymentResponseDto data = paymentService.getPaymentByReference(reference);

        CustomResponseMessage<PaymentResponseDto> body =
                new CustomResponseMessage<>("Payment fetched successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get payments by user ID", description = "Fetch all payments for a given user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found (if you validate user existence)")
    })
    @GetMapping(value = "/fetch-payment-by-userId/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<List<PaymentResponseDto>>> getPaymentsByUserId(
            @Parameter(description = "User ID") @PathVariable UUID userId
    ) {
        List<PaymentResponseDto> data = paymentService.getPaymentsByUserId(userId);

        CustomResponseMessage<List<PaymentResponseDto>> body =
                new CustomResponseMessage<>("Payments fetched successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get payments by booking ID", description = "Fetch all payments for a given booking.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found (if you validate booking existence)")
    })
    @GetMapping(value = "/fetch-payment-by-bookingId/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<List<PaymentResponseDto>>> getPaymentsByBookingId(
            @Parameter(description = "Booking ID") @PathVariable UUID bookingId
    ) {
        List<PaymentResponseDto> data = paymentService.getPaymentsByBookingId(bookingId);

        CustomResponseMessage<List<PaymentResponseDto>> body =
                new CustomResponseMessage<>("Payments fetched successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "Update payment status",
            description = "Updates payment status and optionally stores provider message."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PatchMapping(value = "/update-payment-status/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<PaymentResponseDto>> updatePaymentStatus(
            @Parameter(description = "Payment ID")
            @PathVariable UUID paymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request
    ) {
        PaymentResponseDto data = paymentService.updatePaymentStatus(
                paymentId,
                request.status(),
                request.providerMessage()
        );

        CustomResponseMessage<PaymentResponseDto> body =
                new CustomResponseMessage<>("Payment status updated successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Cancel payment", description = "Cancels a payment (Stripe cancel logic handled in service).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping(value = "/cancel-payment/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<PaymentResponseDto>> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable UUID paymentId,
            @Parameter(description = "Cancellation reason")
            @Valid @RequestBody String reason
    ) {
        PaymentResponseDto data = paymentService.cancelPayment(paymentId, reason);

        CustomResponseMessage<PaymentResponseDto> body =
                new CustomResponseMessage<>("Payment cancelled successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Refund payment", description = "Refunds a payment (Stripe refund logic handled in service).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment refunded successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping(value = "/refund-payment/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<PaymentResponseDto>> refundPayment(
            @Parameter(description = "Payment ID")
            @PathVariable UUID paymentId,
            @Parameter(description = "Refund reason")
            @Valid @RequestBody String reason
    ) {
        PaymentResponseDto data = paymentService.refundPayment(paymentId, reason);

        CustomResponseMessage<PaymentResponseDto> body =
                new CustomResponseMessage<>("Payment refunded successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get all payments", description = "Fetch all payments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments fetched successfully")
    })
    @GetMapping(value = "/all-payments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<List<PaymentResponseDto>>> getAllPayments() {
        List<PaymentResponseDto> data = paymentService.getAllPayments();

        CustomResponseMessage<List<PaymentResponseDto>> body =
                new CustomResponseMessage<>("Payments fetched successfully", HttpStatus.OK.value(), data);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Count payments", description = "Returns total number of payments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments counted successfully")
    })
    @GetMapping(value = "/total-payments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseMessage<Long>> countPayments() {
        long count = paymentService.countPayments();

        CustomResponseMessage<Long> body =
                new CustomResponseMessage<>("Payments counted successfully", HttpStatus.OK.value(), count);

        return ResponseEntity.ok(body);
    }
}
