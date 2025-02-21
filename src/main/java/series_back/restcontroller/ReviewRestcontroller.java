package series_back.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import series_back.modelo.dto.ReviewDto;
import series_back.modelo.entities.Review;
import series_back.modelo.services.IReviewService;

import java.util.List;

@RestController
@RequestMapping("api/reviews")
@CrossOrigin(origins = "*")
public class ReviewRestcontroller {

    @Autowired
    private IReviewService reviewService;

    // RUTAS PÚBLICAS
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.findAllWithDto());
    }

    @GetMapping("/series/{seriesId}")
    public ResponseEntity<List<ReviewDto>> getReviewsBySeries(@PathVariable Long seriesId) {
        return ResponseEntity.ok(reviewService.findBySeriesId(seriesId));
    }

    @GetMapping("/rating/min/{minRating}")
    public ResponseEntity<List<ReviewDto>> getReviewsByMinRating(@PathVariable Double minRating) {
        return ResponseEntity.ok(reviewService.findByMinRating(minRating));
    }

    @GetMapping("/rating/range/{minRating}/{maxRating}")
    public ResponseEntity<List<ReviewDto>> getReviewsByRatingRange(@PathVariable Double minRating, @PathVariable Double maxRating) {
        return ResponseEntity.ok(reviewService.findByRatingRange(minRating, maxRating));
    }

    // RUTAS USER
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Review> createReview(@RequestBody Review review, Authentication authentication) {
        return ResponseEntity.ok(reviewService.create(review));
    }

    // RUTAS USER / ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @reviewService.isReviewOwner(#id, authentication.principal.id)")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, Authentication authentication) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<ReviewDto>> getReviewsByUser(@PathVariable Long userId, Authentication authentication) {
        return ResponseEntity.ok(reviewService.findByUserId(userId));
    }
}
