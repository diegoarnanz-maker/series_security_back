package series_back.modelo.dto;

import java.util.Set;

import lombok.*;
import series_back.modelo.entities.Role;
import series_back.modelo.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    private String username;
    private String email;
    private String password;
    private Set<Role> roles;

    public User convertToEntity() {
        return User.builder()
                .username(this.username)
                .email(this.email)
                .password(new BCryptPasswordEncoder().encode(this.password))
                .roles(this.roles)
                .build();
    }
}
