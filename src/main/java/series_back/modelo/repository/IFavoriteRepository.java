package series_back.modelo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import series_back.modelo.entities.Favorite;

public interface IFavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);
    
    void deleteByUserIdAndSeriesId(Long userId, Long seriesId);

    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);

}
