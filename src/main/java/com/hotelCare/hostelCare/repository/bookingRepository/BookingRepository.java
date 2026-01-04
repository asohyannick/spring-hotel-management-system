package com.hotelCare.hostelCare.repository.bookingRepository;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /* =========================
       Basic Searches
       ========================= */

    List<Booking> findByUserId(UUID userId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByRegionIgnoreCase(String region);

    /* =========================
       Date-based Searches
       ========================= */

    List<Booking> findByCheckInDate(LocalDateTime checkInDate);

    List<Booking> findByCheckOutDate(LocalDateTime checkOutDate);

    List<Booking> findByCheckInDateBetween(LocalDateTime start, LocalDateTime end);

    /* =========================
       Availability / Conflict Checks
       ========================= */

    @Query("""
        SELECT b FROM Booking b
        WHERE b.region = :region
          AND b.status = 'CONFIRMED'
          AND (
                :checkIn < b.checkOutDate
            AND :checkOut > b.checkInDate
          )
    """)
    List<Booking> findConflictingBookings(
            @Param("location") String region,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    /* =========================
       Advanced Filtering
       ========================= */

    @Query("""
        SELECT b FROM Booking b
        WHERE (:status IS NULL OR b.status = :status)
          AND (:region IS NULL OR LOWER(b.region) LIKE LOWER(CONCAT('%', :region, '%')))
          AND (:minPrice IS NULL OR b.price >= :minPrice)
          AND (:maxPrice IS NULL OR b.price <= :maxPrice)
    """)
    List<Booking> searchBookings(
            @Param("status") BookingStatus status,
            @Param("region") String region,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    /* =========================
       Admin / Reporting
       ========================= */

    long countByStatus(BookingStatus status);

    boolean existsByUserIdAndStatus(UUID userId, BookingStatus status);
}

