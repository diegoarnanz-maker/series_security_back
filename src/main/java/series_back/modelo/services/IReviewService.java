package series_back.modelo.services;

import series_back.modelo.dto.ReviewDto;
import series_back.modelo.entities.Review;

import java.util.List;

public interface IReviewService extends IGenericoCRUD<Review, Long> {

    List<ReviewDto> findAllWithDto();

    List<ReviewDto> findByUserId(Long userId);

    // List<ReviewDto> findByUserIdOrderedByDate(Long userId);

    List<ReviewDto> findBySeriesId(Long seriesId);

    // List<ReviewDto> findBySeriesIdOrderedByDate(Long seriesId);

    List<ReviewDto> findByMinRating(Double minRating);

    List<ReviewDto> findByRatingRange(Double minRating, Double maxRating);

    boolean isReviewOwner(Long reviewId, Long userId);

    boolean existsByUserAndSeries(Long userId, Long seriesId);

}
