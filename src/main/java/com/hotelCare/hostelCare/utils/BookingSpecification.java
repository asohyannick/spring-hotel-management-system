package com.hotelCare.hostelCare.utils;
import com.hotelCare.hostelCare.dto.bookings.BookingSearchRequestDto;
import com.hotelCare.hostelCare.entity.booking.Booking;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class BookingSpecification {

    public static Specification<Booking> search(BookingSearchRequestDto dto) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (dto.status() != null) {
                predicates.add(cb.equal(root.get("status"), dto.status()));
            }

            if (dto.isCancelled() != null) {
                predicates.add(cb.equal(root.get("isCancelled"), dto.isCancelled()));
            }

            if (dto.isPaid() != null) {
                predicates.add(cb.equal(root.get("isPaid"), dto.isPaid()));
            }

            if (dto.paymentMethod() != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), dto.paymentMethod()));
            }

            if (dto.region() != null && !dto.region().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("region")),
                                "%" + dto.region().toLowerCase() + "%")
                );
            }

            if (dto.country() != null && !dto.country().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("country")),
                                "%" + dto.country().toLowerCase() + "%")
                );
            }

            /* ================= PRICE ================= */
            if (dto.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("totalAmount"), dto.minPrice()));
            }

            if (dto.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("totalAmount"), dto.maxPrice()));
            }

            /* ================= DATES ================= */
            if (dto.checkInFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("checkInDate"), dto.checkInFrom()));
            }

            if (dto.checkInTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("checkInDate"), dto.checkInTo()));
            }

            if (dto.checkOutFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("checkOutDate"), dto.checkOutFrom()));
            }

            if (dto.checkOutTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("checkOutDate"), dto.checkOutTo()));
            }

            if (dto.createdFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"), dto.createdFrom()));
            }

            if (dto.createdTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"), dto.createdTo()));
            }

            if (dto.userId() != null) {
                predicates.add(cb.equal(
                        root.get("user").get("id"), dto.userId()));
            }

            if (dto.bookingReference() != null && !dto.bookingReference().isBlank()) {
                predicates.add(cb.equal(
                        root.get("bookingReference"), dto.bookingReference()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
