package com.hotelCare.hostelCare.mappers.bookingMapper;
import com.hotelCare.hostelCare.dto.bookings.BookingRequestDto;
import com.hotelCare.hostelCare.dto.bookings.BookingResponseDto;
import com.hotelCare.hostelCare.dto.bookings.BookingUpdateRequestDto;
import com.hotelCare.hostelCare.entity.booking.Booking;
import org.mapstruct.*;
import java.math.BigDecimal;
@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isPaid", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "paymentReference", ignore = true)
    @Mapping(target = "isCancelled", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(dto))")
    @Mapping(target = "totalAmount", expression = "java(calculateTotal(dto))")
    Booking toEntity(BookingRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateBookingFromDto(
            BookingUpdateRequestDto dto,
            @MappingTarget Booking booking
    );

    BookingResponseDto toResponseDto(Booking booking);


    default BigDecimal calculateSubtotal(BookingRequestDto dto) {
        return dto.pricePerNight()
                .multiply(BigDecimal.valueOf(dto.numberOfNights()));
    }

    default BigDecimal calculateTotal(BookingRequestDto dto) {
        BigDecimal subtotal = calculateSubtotal(dto);

        BigDecimal tax = dto.taxAmount() != null
                ? dto.taxAmount()
                : BigDecimal.ZERO;

        BigDecimal discount = dto.discountAmount() != null
                ? dto.discountAmount()
                : BigDecimal.ZERO;

        return subtotal.add(tax).subtract(discount);
    }
}
