package series_back.modelo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import series_back.modelo.entities.Favorite;
import series_back.modelo.repository.IFavoriteRepository;

@Service
public class FavoriteServiceImplMy8 extends GenericoCRUDServiceImplMy8<Favorite, Long> implements IFavoriteService {

    @Autowired
    private IFavoriteRepository favoriteRepository;

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
}
