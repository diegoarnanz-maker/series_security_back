package series_back.modelo.dto;

import lombok.*;
import series_back.modelo.entities.Favorite;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDto {
    private Long id;
    private String username;
    private String seriesTitle;

    // 🔹 Método estático correcto
    public static FavoriteDto convertToDto(Favorite favorite) {
        if (favorite == null) {
            throw new IllegalArgumentException("El objeto Favorite no puede ser nulo");
        }
        return FavoriteDto.builder()
                .id(favorite.getId())
                .username(favorite.getUser().getUsername())
                .seriesTitle(favorite.getSeries().getTitle())
                .build();
    }
}
