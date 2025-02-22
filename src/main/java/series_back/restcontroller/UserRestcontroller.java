package series_back.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import series_back.modelo.dto.UserDto;
import series_back.modelo.dto.UserRequestDto;
import series_back.modelo.entities.Role;
import series_back.modelo.entities.User;
import series_back.modelo.services.IRoleService;
import series_back.modelo.services.IUserService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "*")
public class UserRestcontroller {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // RUTAS ADMIN
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> usersDto = userService.findAll().stream()
                .map(UserDto::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usersDto);
    }

    // Aqui permito enviar el User con toda la informacion
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.read(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto userDto) {
        try {
            if (userDto.getUsername().isEmpty() || userDto.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "El nombre de usuario y la contraseña son obligatorios"));
            }
            if (userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email es obligatorio"));
            }
            if (userDto.getPassword().length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "La contraseña debe tener al menos 8 caracteres"));
            }
            if (userService.findByUsername(userDto.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El nombre de usuario ya está en uso"));
            }
            if (userService.findByEmail(userDto.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email ya está en uso"));
            }

            Set<Role> roles = new HashSet<>();
            if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                for (Role role : userDto.getRoles()) {
                    Optional<Role> foundRole = roleService.findByName(role.getName());
                    if (foundRole.isPresent()) {
                        roles.add(foundRole.get());
                    } else {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Error: Rol no encontrado -> " + role.getName()));
                    }
                }
            } else {
                roles.add(roleService.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USER no encontrado")));
            }

            String encodedPassword = passwordEncoder.encode(userDto.getPassword());

            User user = User.builder()
                    .username(userDto.getUsername())
                    .email(userDto.getEmail())
                    .password(encodedPassword)
                    .roles(roles)
                    .build();
            User newUser = userService.create(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al crear el usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequestDto userDto) {
        try {
            User existingUser = userService.read(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

            if (userDto.getUsername().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El nombre de usuario no puede estar vacío"));
            }
            if (userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email es obligatorio"));
            }
            if (userDto.getPassword() != null && userDto.getPassword().length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "La contraseña debe tener al menos 8 caracteres"));
            }

            if (userService.findByUsername(userDto.getUsername()).isPresent() &&
                    !existingUser.getUsername().equals(userDto.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of("message", "El nombre de usuario ya está en uso"));
            }
            if (userService.findByEmail(userDto.getEmail()).isPresent() &&
                    !existingUser.getEmail().equals(userDto.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email ya está en uso"));
            }

            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            Set<Role> roles = new HashSet<>();
            if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                for (Role role : userDto.getRoles()) {
                    Optional<Role> foundRole = roleService.findByName(role.getName());
                    if (foundRole.isPresent()) {
                        roles.add(foundRole.get());
                    } else {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Error: Rol no encontrado -> " + role.getName()));
                    }
                }
            } else {
                roles.add(roleService.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USER no encontrado")));
            }
            existingUser.setRoles(roles);

            User updatedUser = userService.update(existingUser);
            return ResponseEntity.ok(updatedUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al actualizar el usuario: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // RUTAS USER / ADMIN
    // @GetMapping("/me")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    // public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
    //     System.out.println("Usuario autenticado: " + authentication.getName());

    //     authentication.getAuthorities()
    //             .forEach(auth -> System.out.println("Rol detectado por Spring Security: " + auth.getAuthority()));

    //     return userService.findByUsername(authentication.getName())
    //             .map(ResponseEntity::ok)
    //             .orElse(ResponseEntity.notFound().build());
    // }

}
