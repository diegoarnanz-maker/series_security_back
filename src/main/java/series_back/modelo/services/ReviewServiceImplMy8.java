package series_back.modelo.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import series_back.modelo.dto.ReviewDto;
import series_back.modelo.entities.Review;
import series_back.modelo.repository.IReviewRepository;

@Service
public class ReviewServiceImplMy8 extends GenericoCRUDServiceImplMy8<Review, Long> implements IReviewService {

    @Autowired
    private IReviewRepository reviewRepository;

    @Override
    protected IReviewRepository getRepository() {
        return reviewRepository;
    }

    @Override
    public List<ReviewDto> findAllWithDto() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewDto::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(ReviewDto::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findBySeriesId(Long seriesId) {
        if (seriesId == null) {
            throw new IllegalArgumentException("El ID de la serie no puede ser nulo.");
        }
        List<Review> reviews = reviewRepository.findBySeriesId(seriesId);
        return reviews.isEmpty() ? List.of() : reviews.stream().map(ReviewDto::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findByMinRating(Double minRating) {
        if (minRating == null) {
            throw new IllegalArgumentException("El rating mínimo no puede ser nulo.");
        }
        List<Review> reviews = reviewRepository.findByRatingGreaterThanEqual(minRating);
        return reviews.isEmpty() ? List.of() : reviews.stream().map(ReviewDto::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findByRatingRange(Double minRating, Double maxRating) {
        if (minRating == null || maxRating == null) {
            throw new IllegalArgumentException("Los ratings no pueden ser nulos.");
        }
        if (minRating > maxRating) {
            throw new IllegalArgumentException("El rating mínimo no puede ser mayor al rating máximo.");
        }
        List<Review> reviews = reviewRepository.findByRatingBetween(minRating, maxRating);
        return reviews.isEmpty() ? List.of() : reviews.stream().map(ReviewDto::convertToDto).collect(Collectors.toList());
    }

    @Override
    public boolean isReviewOwner(Long reviewId, Long userId) {
        return reviewRepository.existsByIdAndUserId(reviewId, userId);
    }
}
