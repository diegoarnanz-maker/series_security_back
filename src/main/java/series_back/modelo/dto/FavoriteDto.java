package series_back.modelo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDto {
    private Long id;
    private String username;
    private String seriesTitle;
}
