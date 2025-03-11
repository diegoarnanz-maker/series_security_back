package series_back.restcontroller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import series_back.modelo.dto.LoginDto;
import series_back.modelo.dto.UserDto;
import series_back.modelo.dto.UserRequestDto;
import series_back.modelo.entities.Role;
import series_back.modelo.entities.User;
import series_back.modelo.services.IRoleService;
import series_back.modelo.services.IUserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthRestcontroller {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto loginDto) {
        User user = userService.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Credenciales incorrectas"));
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(), user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login exitoso");
        response.put("user", user.getUsername());
        response.put("id", user.getId());
        response.put("roles", user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequestDto userDto) {
        try {
            if (userDto.getUsername().isEmpty() || userDto.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "El nombre de usuario y la contraseña son obligatorios"));
            }
            if (userDto.getEmail().isEmpty() || userDto.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email es obligatorio"));
            }
            if (userDto.getPassword().isEmpty() || userDto.getPassword().length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "La contraseña debe tener al menos 8 caracteres"));
            }
            if (userService.findByUsername(userDto.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El nombre de usuario ya está en uso"));
            }
            if (userService.findByEmail(userDto.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email ya está en uso"));
            }
            Optional<Role> userRole = roleService.findByName("ROLE_USER");
            if (userRole.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Error: Rol no encontrado"));
            }

            String encodedPassword = passwordEncoder.encode(userDto.getPassword());

            User user = User.builder()
                    .username(userDto.getUsername())
                    .email(userDto.getEmail())
                    .password(encodedPassword)
                    .roles(Collections.singleton(userRole.get()))
                    .build();

            User newUser = userService.create(user);

            return ResponseEntity.status(201).body(newUser);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error al registrar el usuario"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return ResponseEntity.ok(UserDto.convertToDto(user));
    }
}
