package series_back.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import series_back.modelo.dto.SerieDto;
import series_back.modelo.entities.Serie;
import series_back.modelo.services.ISerieService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/series")
@CrossOrigin(origins = "*")
public class SerieRestcontroller {

    @Autowired
    private ISerieService serieService;

    // RUTAS PÚBLICAS
    @GetMapping
    public ResponseEntity<List<SerieDto>> getAllSeries() {
        return ResponseEntity.ok(serieService.findAllWithDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SerieDto> getSerieById(@PathVariable Long id) {
        return serieService.read(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<SerieDto>> getSeriesByGenre(@PathVariable String genre) {
        List<SerieDto> seriesDtoList = serieService.findByGenre(genre)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(seriesDtoList);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<String>> getGenres() {
        List<String> genres = serieService.findAll().stream()
                .map(Serie::getGenre)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(genres);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<SerieDto>> getSeriesByRating(@PathVariable Double rating) {
        List<SerieDto> seriesDtoList = serieService.findByRating(rating)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(seriesDtoList);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<SerieDto>> getLatestSeries() {
        List<SerieDto> latestSeries = serieService.findLatestSeries()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(latestSeries);
    }

    // RUTAS ADMIN
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SerieDto> createSerie(@RequestBody Serie serie) {
        Serie savedSerie = serieService.create(serie);
        return ResponseEntity.ok(convertToDto(savedSerie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SerieDto> updateSerie(@PathVariable Long id, @RequestBody Serie serie) {
        serie.setId(id);
        Serie updatedSerie = serieService.update(serie);
        return ResponseEntity.ok(convertToDto(updatedSerie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSerie(@PathVariable Long id) {
        serieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Método para convertir `Serie` a `SerieDto`, podria ir en el service
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
}
