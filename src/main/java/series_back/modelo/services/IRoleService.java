package series_back.modelo.services;

import java.util.Optional;
import series_back.modelo.entities.Role;

public interface IRoleService extends IGenericoCRUD<Role, Long> {
    
    Optional<Role> findByName(String name);
}
