package series_back.modelo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import series_back.modelo.entities.User;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

}
