package series_back.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import series_back.modelo.entities.Favorite;

public interface IFavoriteRepository extends JpaRepository<Favorite, Long> {

}
