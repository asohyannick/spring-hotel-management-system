package com.hotelCare.hostelCare.controller.review;
import com.hotelCare.hostelCare.config.customResponseMessge.CustomResponseMessage;
import com.hotelCare.hostelCare.dto.review.ReviewRequestDto;
import com.hotelCare.hostelCare.dto.review.ReviewResponseDto;
import com.hotelCare.hostelCare.service.review.ReviewService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@Tag(name = "Reviews Management Endpoints", description = "Review management endpoints (create, moderation, fetch, update, delete)")
@RestController
@RequestMapping("/api/${api.version}/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create a review", description = "Creates a review for a booking by a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created successfully",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Booking/User not found")
    })
    @PostMapping("/create-review")
    public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> createReview(
            @RequestBody @Valid ReviewRequestDto request
    ) {
        ReviewResponseDto created = reviewService.createReview(request);

        CustomResponseMessage<ReviewResponseDto> body =
                new CustomResponseMessage<>("Review created successfully", HttpStatus.CREATED.value(), created);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(summary = "Get all reviews", description = "Fetches all reviews that are not deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews fetched successfully")
    })
    @GetMapping("/all-reviews")
    public ResponseEntity<CustomResponseMessage<List<ReviewResponseDto>>> getAllReviews() {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();

        CustomResponseMessage<List<ReviewResponseDto>> body =
                new CustomResponseMessage<>("Reviews fetched successfully", HttpStatus.OK.value(), reviews);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get review by ID", description = "Fetch a single review by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/fetch-review/{reviewId}")
    public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> getReviewById(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId
    ) {
        ReviewResponseDto review = reviewService.getReviewById(reviewId);

        CustomResponseMessage<ReviewResponseDto> body =
                new CustomResponseMessage<>("Review fetched successfully", HttpStatus.OK.value(), review);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Update a review", description = "Updates a review if it is still pending.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PutMapping("/update-review/{reviewId}")
    public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> updateReview(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId,
            @RequestBody @Valid ReviewRequestDto request
    ) {
        ReviewResponseDto updated = reviewService.updateReview(reviewId, request);

        CustomResponseMessage<ReviewResponseDto> body =
                new CustomResponseMessage<>("Review updated successfully", HttpStatus.OK.value(), updated);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Delete a review", description = "Soft deletes a review by setting deleted=true.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/delete-review/{reviewId}")
    public ResponseEntity<CustomResponseMessage<Object>> deleteReview(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId
    ) {
        reviewService.deleteReview(reviewId);

        CustomResponseMessage<Object> body =
                new CustomResponseMessage<>("Review deleted successfully", HttpStatus.OK.value(), null);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Approve a review", description = "Approves a review and optionally adds a moderation note.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review approved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PostMapping("/approve-review/{reviewId}")
    public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> approveReview(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId,
            @RequestBody @Valid String moderationNote
    ) {
        ReviewResponseDto approved = reviewService.approveReview(reviewId, moderationNote);

        CustomResponseMessage<ReviewResponseDto> body =
                new CustomResponseMessage<>("Review approved successfully", HttpStatus.OK.value(), approved);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Reject a review", description = "Rejects a review and optionally adds a moderation note.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PostMapping("/reject-review/{reviewId}")
    public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> rejectReview(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId,
            @RequestBody @Valid  String moderationNote
    ) {
        ReviewResponseDto rejected = reviewService.rejectReview(reviewId, moderationNote);

        CustomResponseMessage<ReviewResponseDto> body =
                new CustomResponseMessage<>("Review rejected successfully", HttpStatus.OK.value(), rejected);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get approved reviews", description = "Fetches all approved reviews.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Approved reviews fetched successfully")
    })
    @GetMapping("/fetch-approved-reviews")
    public ResponseEntity<CustomResponseMessage<List<ReviewResponseDto>>> getApprovedReviews() {
        List<ReviewResponseDto> reviews = reviewService.getApprovedReviews();

        CustomResponseMessage<List<ReviewResponseDto>> body =
                new CustomResponseMessage<>("Approved reviews fetched successfully", HttpStatus.OK.value(), reviews);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get rejected reviews", description = "Fetches all rejected reviews.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rejected reviews fetched successfully")
    })
    @GetMapping("/fetch-rejected-reviews")
    public ResponseEntity<CustomResponseMessage<List<ReviewResponseDto>>> getRejectedReviews() {
        List<ReviewResponseDto> reviews = reviewService.getRejectedReviews();

        CustomResponseMessage<List<ReviewResponseDto>> body =
                new CustomResponseMessage<>("Rejected reviews fetched successfully", HttpStatus.OK.value(), reviews);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Count total reviews", description = "Counts all reviews that are not deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total reviews counted successfully")
    })
    @GetMapping("/total-reviews")
    public ResponseEntity<CustomResponseMessage<Long>> countTotalReviews() {
        long count = reviewService.countTotalReviews();

        CustomResponseMessage<Long> body =
                new CustomResponseMessage<>("Total reviews counted successfully", HttpStatus.OK.value(), count);

        return ResponseEntity.ok(body);
    }
}
