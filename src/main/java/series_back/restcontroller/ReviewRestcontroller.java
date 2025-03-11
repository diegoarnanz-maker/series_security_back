package series_back.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import series_back.modelo.dto.ReviewDto;
import series_back.modelo.entities.Review;
import series_back.modelo.entities.Serie;
import series_back.modelo.entities.User;
import series_back.modelo.services.IReviewService;
import series_back.modelo.services.ISerieService;
import series_back.modelo.services.IUserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/reviews")
@CrossOrigin(origins = "*")
public class ReviewRestcontroller {

    @Autowired
    private IReviewService reviewService;

    @Autowired
    private ISerieService serieService;

    @Autowired
    private IUserService userService;

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
    public ResponseEntity<List<ReviewDto>> getReviewsByRatingRange(@PathVariable Double minRating,
            @PathVariable Double maxRating) {
        return ResponseEntity.ok(reviewService.findByRatingRange(minRating, maxRating));
    }

    // RUTAS USER
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> createReview(@RequestBody ReviewDto reviewDto) {
        try {
            Serie series = serieService.findByTitle(reviewDto.getSeriesTitle())
                    .orElseThrow(() -> new RuntimeException(
                            "Serie no encontrada con título: " + reviewDto.getSeriesTitle()));

            User user = userService.findByUsername(reviewDto.getUsername())
                    .orElseThrow(() -> new RuntimeException(
                            "Usuario no encontrado con username: " + reviewDto.getUsername()));

            // 🔹 Verificar si el usuario ya tiene una reseña para esta serie
            boolean reviewExists = reviewService.existsByUserAndSeries(user.getId(), series.getId());
            if (reviewExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya has realizado una reseña para esta serie.");
            }

            Review review = new Review();
            review.setSeries(series);
            review.setUser(user);
            review.setRating(reviewDto.getRating());
            review.setComment(reviewDto.getComment());
            review.setCreatedAt(LocalDateTime.now());

            Review newReview = reviewService.create(review);

            ReviewDto responseDto = ReviewDto.builder()
                    .id(newReview.getId())
                    .username(newReview.getUser().getUsername())
                    .seriesTitle(newReview.getSeries().getTitle())
                    .rating(newReview.getRating())
                    .comment(newReview.getComment())
                    .createdAt(newReview.getCreatedAt())
                    .build();

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la reseña: " + e.getMessage());
        }
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
