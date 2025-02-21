package series_back.modelo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {

    private Long id;
    private String username;
    private Long seriesId;
    private String seriesTitle;
    private double rating;
    private String comment;
    private LocalDateTime createdAt;
}
