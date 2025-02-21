package series_back.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import series_back.modelo.entities.Review;
import java.util.List;

public interface IReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findByUserId(Long userId);

    List<Review> findBySeriesId(Long seriesId);

    List<Review> findByRatingGreaterThanEqual(Double minRating);

    List<Review> findByRatingBetween(Double minRating, Double maxRating);
}
