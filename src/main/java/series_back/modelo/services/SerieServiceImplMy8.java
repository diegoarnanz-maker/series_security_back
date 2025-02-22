package series_back.modelo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import series_back.modelo.dto.SerieDto;
import series_back.modelo.entities.Serie;
import series_back.modelo.repository.ISerieRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieServiceImplMy8 extends GenericoCRUDServiceImplMy8<Serie, Long> implements ISerieService {

    @Autowired
    private ISerieRepository serieRepository;

    @Override
    protected ISerieRepository getRepository() {
        return serieRepository;
    }

    @Override
    public List<SerieDto> findAllWithDto() {
        List<Serie> series = serieRepository.findAll();
        return series.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private SerieDto convertToDto(Serie serie) {
        return SerieDto.builder()
                .id(serie.getId())
                .title(serie.getTitle())
                .description(serie.getDescription())
                .genre(serie.getGenre())
                .releaseYear(serie.getReleaseYear())
                .rating(serie.getRating())
                .imageUrl(serie.getImageUrl())
                .trailerUrl(serie.getTrailerUrl())
                .build();
    }

    @Override
    public Optional<Serie> findByName(String title) {
        try {
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("El nombre de la serie no puede estar vacío.");
            }
            return serieRepository.findByTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar la serie por nombre.");
        }
    }

    @Override
    public List<Serie> findByGenre(String genre) {
        try {
            if (genre == null || genre.isEmpty()) {
                throw new IllegalArgumentException("El género no puede estar vacío.");
            }
            return serieRepository.findByGenreContainingIgnoreCase(genre);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar series por género.");
        }
    }

    @Override
    public List<Serie> findByRating(Double rating) {
        try {
            if (rating == null || rating < 0 || rating > 10) {
                throw new IllegalArgumentException("El rating debe estar entre 0 y 10.");
            }
            return serieRepository.findByRatingGreaterThanEqual(rating);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar series por rating.");
        }
    }

    @Override
    public Optional<Serie> findByTitle(String title) {
        try {
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("El título de la serie no puede estar vacío.");
            }
            return serieRepository.findByTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar la serie por título.");
        }
    }
}
