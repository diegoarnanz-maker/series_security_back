package series_back.modelo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import series_back.modelo.entities.Favorite;
import series_back.modelo.entities.User;

public interface IFavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    void deleteByUserIdAndSeriesId(Long userId, Long seriesId);

    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);

    @Query("SELECT f FROM Favorite f WHERE f.user.username = :username")
    List<Favorite> findFavoritesByUsername(@Param("username") String username);
}
