package series_back.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import series_back.modelo.entities.User;
import series_back.modelo.services.IFavoriteService;
import series_back.modelo.services.IReviewService;
import series_back.modelo.services.IUserService;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    private IUserService userService;

    @Autowired
    private IReviewService reviewService;

    @Autowired
    private IFavoriteService favoriteService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(authorize -> authorize
                        // AUTHORIZATION
                        .requestMatchers("/auth/**").permitAll()

                        // SERIES
                        // Rutas públicas
                        .requestMatchers(HttpMethod.GET, "/api/series").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/series/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/series/genre/{genre}")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/series/genre")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/series/rating/{rating}").permitAll()

                        // Rutas ROLE_ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/series").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/series/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/series/{id}").hasAuthority("ROLE_ADMIN")

                        // REVIEWS
                        // Rutas públicas
                        .requestMatchers(HttpMethod.GET, "/api/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/series/{seriesId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/rating/min/{minRating}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/rating/range/{minRating}/{maxRating}")
                        .permitAll()

                        // Rutas ROLE_USER
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasAuthority("ROLE_USER")

                        // Rutas ROLE_ADMIN / ROLE_USER
                        .requestMatchers(HttpMethod.GET, "/api/reviews/user/{userId}")
                        .access((authentication, request) -> {
                            try {
                                String authenticatedUsername = authentication.get().getName();
                                HttpServletRequest httpRequest = request.getRequest();
                                Long requestedUserId = Long.parseLong(httpRequest.getRequestURI().split("/")[4]);

                                User authenticatedUser = userService.findByUsername(authenticatedUsername)
                                        .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado: " + authenticatedUsername));

                                boolean isAdmin = authentication.get().getAuthorities().stream()
                                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                                .equals("ROLE_ADMIN"));

                                return new AuthorizationDecision(
                                        isAdmin || requestedUserId.equals(authenticatedUser.getId()));
                            } catch (Exception e) {
                                return new AuthorizationDecision(false);
                            }
                        })

                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/{id}")
                        .access((authentication, request) -> {
                            try {
                                String authenticatedUsername = authentication.get().getName();
                                HttpServletRequest httpRequest = request.getRequest();
                                Long reviewId = Long.parseLong(httpRequest.getRequestURI().split("/")[3]);

                                User authenticatedUser = userService.findByUsername(authenticatedUsername)
                                        .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado: " + authenticatedUsername));

                                boolean isAdmin = authentication.get().getAuthorities().stream()
                                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                                .equals("ROLE_ADMIN"));

                                boolean isReviewOwner = reviewService.isReviewOwner(reviewId,
                                        authenticatedUser.getId());

                                return new AuthorizationDecision(isAdmin || isReviewOwner);
                            } catch (Exception e) {
                                return new AuthorizationDecision(false);
                            }
                        })

                        // FAVORITES
                        // Rutas ROLE_ADMIN / ROLE_USER
                        .requestMatchers(HttpMethod.GET, "/api/favorites/user/{userId}")
                        .access((authentication, request) -> {
                            try {
                                String authenticatedUsername = authentication.get().getName();
                                HttpServletRequest httpRequest = request.getRequest();
                                Long requestedUserId = Long.parseLong(httpRequest.getRequestURI().split("/")[4]);

                                Long authenticatedUserId = userService.findByUsername(authenticatedUsername)
                                        .map(user -> user.getId())
                                        .orElse(null);

                                return new AuthorizationDecision(
                                        authentication.get().getAuthorities().stream()
                                                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                                        .equals("ROLE_ADMIN"))
                                                || requestedUserId.equals(authenticatedUserId));
                            } catch (Exception e) {
                                return new AuthorizationDecision(false);
                            }
                        })
                        .requestMatchers(HttpMethod.GET, "/api/favorites/user/{username}")
                        .access((authentication, request) -> {
                            try {
                                String authenticatedUsername = authentication.get().getName();
                                HttpServletRequest httpRequest = request.getRequest();
                                Long requestedUserId = Long.parseLong(httpRequest.getRequestURI().split("/")[4]);

                                Long authenticatedUserId = userService.findByUsername(authenticatedUsername)
                                        .map(user -> user.getId())
                                        .orElse(null);

                                return new AuthorizationDecision(
                                        authentication.get().getAuthorities().stream()
                                                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                                        .equals("ROLE_ADMIN"))
                                                || requestedUserId.equals(authenticatedUserId));
                            } catch (Exception e) {
                                return new AuthorizationDecision(false);
                            }
                        })

                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/{seriesId}")
                        .access((authentication, request) -> {
                            try {
                                String authenticatedUsername = authentication.get().getName();
                                HttpServletRequest httpRequest = request.getRequest();
                                Long seriesId = Long.parseLong(httpRequest.getRequestURI().split("/")[3]);

                                Long authenticatedUserId = userService.findByUsername(authenticatedUsername)
                                        .map(user -> user.getId())
                                        .orElse(null);

                                return new AuthorizationDecision(
                                        authentication.get().getAuthorities().stream()
                                                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                                        .equals("ROLE_ADMIN"))
                                                || favoriteService.isFavoriteOwner(authenticatedUserId, seriesId));
                            } catch (Exception e) {
                                return new AuthorizationDecision(false);
                            }
                        })

                        // ROLE_USER(owner)
                        // .requestMatchers(HttpMethod.POST, "/api/favorites")
                        // .hasAuthority("ROLE_USER")

                        // USER
                        // ROLE_ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority("ROLE_ADMIN")

                        // ROLE_ADMIN / ROLE_USER(owner)
                        .requestMatchers(HttpMethod.GET, "/api/users/me").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/users/username/{username}").hasAnyAuthority("ROLE_USER",
                                "ROLE_ADMIN")
                        // OTRAS
                        .anyRequest().authenticated())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
