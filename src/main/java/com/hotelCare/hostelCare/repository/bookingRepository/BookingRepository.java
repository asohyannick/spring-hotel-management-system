package com.hotelCare.hostelCare.repository.bookingRepository;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.UUID;
public interface BookingRepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {
  List<Booking> findByStatus(BookingStatus status);
}

