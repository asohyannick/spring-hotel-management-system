package com.hotelCare.hostelCare.mappers.paymentMapper;
import com.hotelCare.hostelCare.dto.payment.PaymentRequestDto;
import com.hotelCare.hostelCare.dto.payment.PaymentResponseDto;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.entity.payment.Payment;
import com.hotelCare.hostelCare.entity.user.User;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "bookingId", source = "booking.id")
    @Mapping(target = "userId", source = "user.id")
    PaymentResponseDto toResponseDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "stripePaymentIntentId", ignore = true)
    @Mapping(target = "stripeChargeId", ignore = true)
    @Mapping(target = "paypalOrderId", ignore = true)
    @Mapping(target = "paypalCaptureId", ignore = true)
    @Mapping(target = "providerMessage", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "refundedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Payment toEntity(PaymentRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(PaymentRequestDto dto, @MappingTarget Payment payment);


    @AfterMapping
    default void attachRelations(
            @MappingTarget Payment payment,
            @Context Booking booking,
            @Context User user
    ) {
        payment.setBooking(booking);
        payment.setUser(user);
    }
}
