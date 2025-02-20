package series_back.modelo.services;

import java.util.Optional;
import series_back.modelo.entities.User;

public interface IUserService extends IGenericoCRUD<User, Long> {
    
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
