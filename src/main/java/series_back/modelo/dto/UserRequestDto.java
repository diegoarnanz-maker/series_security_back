package series_back.modelo.dto;

import java.util.List;

import lombok.*;
import series_back.modelo.entities.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    private String username;
    private String email;
    private String password;
    private List<Role> roles;

    // Para test
    // public UserRequestDto(String username, String email, String password) {
    // this.username = username;
    // this.email = email;
    // this.password = password;
    // }

}
