package com.hotelCare.hostelCare.controller.booking;
import com.hotelCare.hostelCare.config.customResponseMessge.CustomResponseMessage;
import com.hotelCare.hostelCare.dto.bookings.*;
import com.hotelCare.hostelCare.mappers.bookingMapper.BookingMapper;
import com.hotelCare.hostelCare.service.bookings.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/${api.version}/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings Management Endpoints", description = "Endpoints for creating and managing bookings")
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Create a new booking",
            description = "Creates a booking request and returns the created booking details."
    )
    @PostMapping("/create-booking")
    public ResponseEntity<CustomResponseMessage<BookingResponseDto>> createBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        BookingResponseDto saved = bookingService.createBooking(bookingRequestDto);

        String message = "Booking created successfully.";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponseMessage<>(message, HttpStatus.CREATED.value(), saved));
    }

    @Operation(
            summary = "Fetch all bookings",
            description = "Returns a list of all bookings in the system."
    )
    @GetMapping("/all-bookings")
    public ResponseEntity<CustomResponseMessage<List<BookingResponseDto>>> fetchAllBookings() {
        List<BookingResponseDto> bookings = bookingService.fetchAllBookings();

        String message = "Bookings retrieved successfully.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), bookings));
    }

    @Operation(
            summary = "Fetch all approved bookings",
            description = "Returns a list of bookings with status APPROVED."
    )
    @GetMapping("/fetch-approved-bookings")
    public ResponseEntity<CustomResponseMessage<List<BookingResponseDto>>> fetchAllApprovedBookings() {
        List<BookingResponseDto> bookings = bookingService.fetchAllApprovedBookings();

        String message = "Approved bookings retrieved successfully.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), bookings));
    }

    @Operation(
            summary = "Fetch all rejected bookings",
            description = "Returns a list of bookings with status REJECTED."
    )
    @GetMapping("/fetch-rejected-bookings")
    public ResponseEntity<CustomResponseMessage<List<BookingResponseDto>>> fetchAllRejectedBookings() {
        List<BookingResponseDto> bookings = bookingService.fetchAllRejectedBookings();

        String message = "Rejected bookings retrieved successfully.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), bookings));
    }

    @Operation(
            summary = "Fetch a booking by ID",
            description = "Returns booking details for the provided booking ID."
    )
    @GetMapping("/fetch-booking/{bookingId}")
    public ResponseEntity<CustomResponseMessage<BookingResponseDto>> fetchBooking(
            @PathVariable UUID bookingId
    ) {
        BookingResponseDto booking = bookingService.fetchBooking(bookingId);

        String message = "Booking retrieved successfully.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), booking));
    }

    @Operation(
            summary = "Update a booking",
            description = "Updates an existing booking using the provided booking ID and update payload."
    )
    @PatchMapping("/update-booking/{bookingId}")
    public ResponseEntity<CustomResponseMessage<BookingResponseDto>> updateBooking(
            @PathVariable UUID bookingId,
            @Valid @RequestBody BookingUpdateRequestDto bookingUpdateRequestDto
    ) {
        BookingResponseDto updatedBooking = bookingService.updateBooking(bookingId, bookingUpdateRequestDto);

        String message = "Booking updated successfully.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), updatedBooking));
    }

    @Operation(
            summary = "Delete a booking",
            description = "Deletes a booking permanently using the booking ID."
    )
    @DeleteMapping("/delete-booking/{bookingId}")
    public ResponseEntity<CustomResponseMessage<Void>> deleteBooking(
            @PathVariable UUID bookingId
    ) {
        bookingService.deleteBooking(bookingId);

        String message = "Booking deleted successfully.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), null));
    }

    @Operation(
            summary = "Approve a booking",
            description = "Approves a booking with status PENDING. Only pending bookings can be approved."
    )
    @PatchMapping("/approve-booking/{bookingId}")
    public ResponseEntity<CustomResponseMessage<BookingResponseDto>> approveBooking(
            @PathVariable UUID bookingId
    ) {
        BookingResponseDto approvedBooking = bookingService.approveBooking(bookingId);

        String message = "Booking approved successfully.";
        return ResponseEntity.ok(
                new CustomResponseMessage<>(message, HttpStatus.OK.value(), approvedBooking)
        );
    }

    @Operation(
            summary = "Reject a booking",
            description = "Rejects a booking with status PENDING and records the rejection reason."
    )
    @PatchMapping("/reject-booking/{bookingId}")
    public ResponseEntity<CustomResponseMessage<BookingResponseDto>> rejectBooking(
            @PathVariable UUID bookingId,
            @Valid @RequestBody String rejectionReason
    ) {
        BookingResponseDto rejectedBooking =
                bookingService.rejectBooking(bookingId, rejectionReason);

        String message = "Booking rejected successfully.";
        return ResponseEntity.ok(
                new CustomResponseMessage<>(message, HttpStatus.OK.value(), rejectedBooking)
        );
    }

    @Operation(
            summary = "Get total number of bookings",
            description = "Returns the total count of all bookings in the system."
    )
    @GetMapping("/total-bookings")
    public ResponseEntity<CustomResponseMessage<Long>> totalBookings() {
        long count = bookingService.totalBookings();

        String message = "Total bookings count retrieved successfully.";
        return ResponseEntity.ok(
                new CustomResponseMessage<>(message, HttpStatus.OK.value(), count)
        );
    }

    @Operation(
            summary = "Search bookings",
            description = """
                    Searches bookings using filters such as status, date range, customer, and supports
                    pagination and sorting.
                    """
    )
    @GetMapping("/search-bookings")
    public ResponseEntity<CustomResponseMessage<Page<BookingResponseDto>>> searchBookings(
            @Valid @ModelAttribute BookingSearchRequestDto request
    ) {
        Page<BookingResponseDto> bookings = bookingService.searchBookings(request);

        String message = "Bookings search completed successfully.";
        return ResponseEntity.ok(
                new CustomResponseMessage<>(message, HttpStatus.OK.value(), bookings)
        );
    }

    @Operation(
            summary = "Recommend bookings similar to a given booking",
            description = """
                Returns recommended bookings based on similarity to the provided bookingId
                (region/country/guests/price) and includes an AI-generated explanation (Ollama).
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking recommendations retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BookingRecommendationResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/recommend/{bookingId}")
    public ResponseEntity<CustomResponseMessage<BookingRecommendationResponseDto>> recommendBookings(
            @Parameter(description = "Base booking ID to generate recommendations from", required = true)
            @PathVariable UUID bookingId,

            @Parameter(description = "Maximum number of recommendations to return (default: 5)")
            @RequestParam(defaultValue = "5") int limit
    ) {
        BookingRecommendationResponseDto result = bookingService.recommendBookings(bookingId, limit);

        String message = "Booking recommendations generated successfully.";
        return ResponseEntity.ok(new CustomResponseMessage<>(message, HttpStatus.OK.value(), result));
    }

}

