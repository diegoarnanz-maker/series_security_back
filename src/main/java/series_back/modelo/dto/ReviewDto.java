package series_back.modelo.dto;

import lombok.*;
import series_back.modelo.entities.Review;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private String username;
    private String seriesTitle;
    private double rating;
    private String comment;
    private LocalDateTime createdAt;

    // ✅ Método estático para convertir una entidad Review en ReviewDto
    public static ReviewDto convertToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .username(review.getUser().getUsername())
                .seriesTitle(review.getSeries().getTitle())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
