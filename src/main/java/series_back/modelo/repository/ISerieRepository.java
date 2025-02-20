package series_back.modelo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import series_back.modelo.entities.Serie;

public interface ISerieRepository extends JpaRepository<Serie, Long> {
    
    Optional<Serie> findByTitle(String title);
    List<Serie> findByGenreContainingIgnoreCase(String genre);
    List<Serie> findByRatingGreaterThanEqual(Double rating);

}
