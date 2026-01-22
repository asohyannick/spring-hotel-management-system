package com.hotelCare.hostelCare.service.bookings;
import com.hotelCare.hostelCare.dto.bookings.BookingRequestDto;
import com.hotelCare.hostelCare.dto.bookings.BookingResponseDto;
import com.hotelCare.hostelCare.dto.bookings.BookingSearchRequestDto;
import com.hotelCare.hostelCare.dto.bookings.BookingUpdateRequestDto;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.BookingStatus;
import com.hotelCare.hostelCare.enums.CancelledBookingStatus;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import com.hotelCare.hostelCare.mappers.bookingMapper.BookingMapper;
import com.hotelCare.hostelCare.repository.bookingRepository.BookingRepository;
import com.hotelCare.hostelCare.repository.userRepository.UserRepository;
import com.hotelCare.hostelCare.utils.BookingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private  final BookingMapper bookingMapper;
    private final UserRepository userRepository;

    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = bookingMapper.toEntity(bookingRequestDto);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadRequestException("Unauthenticated: user must login before creating a booking");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING);
        booking.setIsPaid(false);
        booking.setIsCancelled(CancelledBookingStatus.FALSE);

        Booking savedBooking = bookingRepository.saveAndFlush(booking);
        return bookingMapper.toResponseDto(savedBooking);
    }

    public  List<BookingResponseDto> fetchAllApprovedBookings() {
        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.APPROVED);
        return  bookings
                .stream()
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    public  List<BookingResponseDto> fetchAllRejectedBookings() {
        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.REJECTED);
        return  bookings
                .stream()
                .map(bookingMapper::toResponseDto).toList();
    }

    public List<BookingResponseDto> fetchAllBookings() {
       return  bookingRepository.findAll()
               .stream()
               .map(bookingMapper::toResponseDto)
               .toList();
    }

    public  BookingResponseDto fetchBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking not found with ID:" + bookingId)
        );
        return bookingMapper.toResponseDto(booking);
    }

    public  BookingResponseDto updateBooking(UUID bookingId, BookingUpdateRequestDto bookingUpdateRequestDto) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking not found with ID:" + bookingId)
        );
        bookingMapper.updateBookingFromDto(bookingUpdateRequestDto, booking);
        Booking updatedBooking = bookingRepository.save(booking);
        return  bookingMapper.toResponseDto(updatedBooking);
    }

    public void deleteBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking not found with ID:" + bookingId)
        );
        bookingRepository.delete(booking);
    }

    public BookingResponseDto approveBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only PENDING bookings can be approved. Current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.APPROVED);
        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponseDto(savedBooking);
    }

    public BookingResponseDto rejectBooking(UUID bookingId, String rejectionReason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only PENDING bookings can be rejected. Current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setIsCancelled(CancelledBookingStatus.TRUE);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(rejectionReason);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(savedBooking);
    }

    public  long totalBookings() {
        return bookingRepository.count();
    }

    public Page<BookingResponseDto> searchBookings(BookingSearchRequestDto request) {

        int page = request.page() == null ? 0 : request.page();
        int size = request.size() == null ? 10 : request.size();
        String sortBy = (request.sortBy() == null || request.sortBy().isBlank()) ? "createdAt" : request.sortBy();
        String direction = (request.direction() == null || request.direction().isBlank()) ? "DESC" : request.direction();

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Booking> bookings = bookingRepository.findAll(
                BookingSpecification.search(request),
                pageable
        );

        return bookings.map(bookingMapper::toResponseDto);
    }

}
