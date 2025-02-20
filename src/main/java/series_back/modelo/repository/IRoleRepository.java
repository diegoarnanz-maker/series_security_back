package series_back.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import series_back.modelo.entities.Role;
import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
}
