package series_back.modelo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import series_back.modelo.dto.FavoriteDto;
import series_back.modelo.entities.Favorite;
import series_back.modelo.entities.User;
import series_back.modelo.repository.IFavoriteRepository;
import series_back.modelo.repository.IUserRepository;

@Service
public class FavoriteServiceImplMy8 extends GenericoCRUDServiceImplMy8<Favorite, Long> implements IFavoriteService {

    @Autowired
    private IFavoriteRepository favoriteRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected IFavoriteRepository getRepository() {
        return favoriteRepository;
    }

    @Override
    public List<Favorite> findByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndSeriesId(Long userId, Long seriesId) {
        favoriteRepository.deleteByUserIdAndSeriesId(userId, seriesId);
    }

    @Override
    public boolean isFavoriteOwner(Long userId, Long seriesId) {
        return favoriteRepository.existsByUserIdAndSeriesId(userId, seriesId);
    }

    @Override
public List<Favorite> findByUsername(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

    return favoriteRepository.findFavoritesByUsername(user.getUsername());
}


}
