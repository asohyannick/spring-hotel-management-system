package com.hotelCare.hostelCare.service.bookings;
import com.hotelCare.hostelCare.dto.bookings.*;
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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private  final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ChatClient chatClient;

    private int similarityScore(Booking base, Booking other) {
        int score = 0;

        if (safeEqualsIgnoreCase(base.getRegion(), other.getRegion())) score += 40;
        if (safeEqualsIgnoreCase(base.getCountry(), other.getCountry())) score += 30;

        if (base.getNumberOfGuests() != null && other.getNumberOfGuests() != null) {
            int diff = Math.abs(base.getNumberOfGuests() - other.getNumberOfGuests());
            score += Math.max(0, 20 - diff * 4);
        }

        if (base.getPricePerNight() != null && other.getPricePerNight() != null) {
            double diff = base.getPricePerNight().subtract(other.getPricePerNight()).abs().doubleValue();
            score += Math.max(0, 20 - (int) (diff / 10));
        }

        score += Math.max(0, 10 - Math.abs(base.getNumberOfNights() - other.getNumberOfNights()));

        return score;
    }

    private boolean safeEqualsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }


    private String generateRecommendationExplanation(Booking base, List<Booking> recommendations) {
        try {

            String recSummary = recommendations.stream()
                    .map(b -> "- " + b.getName() + " | " + b.getRegion() + ", " + b.getCountry()
                            + " | guests=" + b.getNumberOfGuests()
                            + " | pricePerNight=" + b.getPricePerNight())
                    .reduce("", (acc, line) -> acc + line + "\n");

            String template = """
                    You are a hotel booking recommendation assistant.
                    
                    Base booking:
                    - name: {name}
                    - region: {region}
                    - country: {country}
                    - guests: {guests}
                    - nights: {nights}
                    - pricePerNight: {price}
                    
                    Candidate recommendations:
                    {recommendations}
                    
                    Task:
                    1) In 2-4 sentences, explain WHY these recommendations fit the base booking.
                    2) Keep it short and user-friendly.
                    """;

            Prompt prompt = new PromptTemplate(template).create(Map.of(
                    "name", base.getName(),
                    "region", base.getRegion(),
                    "country", base.getCountry(),
                    "guests", String.valueOf(base.getNumberOfGuests()),
                    "nights", String.valueOf(base.getNumberOfNights()),
                    "price", String.valueOf(base.getPricePerNight()),
                    "recommendations", recSummary.isBlank() ? "- (none)" : recSummary
            ));
            return chatClient.prompt(prompt).call().content();
        } catch (Exception e) {
            return "Recommendations are based on similar location, guests, nights, and price. (AI explanation unavailable right now.)";
        }
    }

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

    public BookingRecommendationResponseDto recommendBookings(UUID bookingId, int limit) {

        Booking base = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with ID: " + bookingId));

        List<Booking> candidates = bookingRepository.findByStatus(BookingStatus.APPROVED)
                .stream()
                .filter(b -> !b.getId().equals(base.getId()))
                .toList();

        List<Booking> top = candidates.stream()
                .sorted(Comparator.<Booking>comparingInt(b -> similarityScore(base, b)).reversed())
                .limit(Math.max(1, limit))
                .toList();

        List<BookingResponseDto> topDtos = top.stream()
                .map(bookingMapper::toResponseDto)
                .toList();

        String aiExplanation = generateRecommendationExplanation(base, top);

        return new BookingRecommendationResponseDto(
                bookingMapper.toResponseDto(base),
                topDtos,
                aiExplanation
        );
    }
}
