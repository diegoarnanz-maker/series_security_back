package series_back.modelo.services;

import java.util.List;

import series_back.modelo.entities.Favorite;

public interface IFavoriteService extends IGenericoCRUD<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);
    
    void deleteByUserIdAndSeriesId(Long userId, Long seriesId);

    boolean isFavoriteOwner(Long userId, Long seriesId);

    List<Favorite> findByUsername(String username);

}
