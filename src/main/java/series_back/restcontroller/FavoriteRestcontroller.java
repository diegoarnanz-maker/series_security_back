package series_back.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import series_back.modelo.dto.FavoriteDto;
import series_back.modelo.entities.Favorite;
import series_back.modelo.entities.Serie;
import series_back.modelo.entities.User;
import series_back.modelo.services.IFavoriteService;
import series_back.modelo.services.ISerieService;
import series_back.modelo.services.IUserService;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteRestcontroller {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteRestcontroller.class);

    @Autowired
    private IFavoriteService favoriteService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISerieService serieService;

    // RUTAS USER(owner) / ADMIN
    @GetMapping("/user/id/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<FavoriteDto>> getUserFavorites(@PathVariable Long userId,
            Authentication authentication) {
        List<FavoriteDto> favorites = favoriteService.findByUserId(userId)
                .stream()
                .map(FavoriteDto::convertToDto) // Ahora incluirá seriesId
                .collect(Collectors.toList());
        return ResponseEntity.ok(favorites);
    }

    // @GetMapping("/user/{username}")
    // public ResponseEntity<List<Favorite>> getFavoritesByUsername(
    // @PathVariable String username,
    // Authentication authentication) {

    // // Debug logs
    // logger.info("🔍 Autenticado como: {}", authentication.getName());
    // logger.info("📌 Nombre en la URL: {}", username);
    // logger.info("🔑 Roles: {}", authentication.getAuthorities());

    // List<Favorite> favorites = favoriteService.findByUsername(username);
    // List<FavoriteDto> favoriteDtos = favorites.stream()
    // .map(FavoriteDto::convertToDto)
    // .collect(Collectors.toList());

    // return ResponseEntity.ok(favoriteDtos);
    // }

    @DeleteMapping("/{seriesId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @favoriteService.isFavoriteOwner(authentication.principal.id, #seriesId)")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long seriesId, Authentication authentication) {
        Long authenticatedUserId = userService.findByUsername(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        favoriteService.deleteByUserIdAndSeriesId(authenticatedUserId, seriesId);
        return ResponseEntity.noContent().build();
    }

    // RUTAS USER
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteDto favoriteDto, Authentication authentication) {
        try {
            String authenticatedUsername = authentication.getName();

            User user = userService.findByUsername(authenticatedUsername)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + authenticatedUsername));

            Serie series = serieService.findByTitle(favoriteDto.getSeriesTitle())
                    .orElseThrow(() -> new RuntimeException("Serie no encontrada: " + favoriteDto.getSeriesTitle()));

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setSeries(series);

            Favorite savedFavorite = favoriteService.create(favorite);

            return ResponseEntity.ok(FavoriteDto.convertToDto(savedFavorite));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agregar a favoritos: " + e.getMessage());
        }
    }

}
