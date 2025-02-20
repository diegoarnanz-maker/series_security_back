package series_back.modelo.services;

import java.util.List;
import java.util.Optional;

import series_back.modelo.dto.SerieDto;
import series_back.modelo.entities.Serie;

public interface ISerieService extends IGenericoCRUD<Serie, Long> {
    List<SerieDto> findAllWithDto();
    Optional<Serie> findByName(String name);
    List<Serie> findByGenre(String genre);
    List<Serie> findByRating(Double rating);

}
