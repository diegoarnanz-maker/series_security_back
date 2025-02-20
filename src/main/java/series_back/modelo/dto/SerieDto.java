package series_back.modelo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SerieDto {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private int releaseYear;
    private double rating;
    private String imageUrl;
    private String trailerUrl;
}
