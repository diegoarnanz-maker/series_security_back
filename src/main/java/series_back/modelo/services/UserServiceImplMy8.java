package series_back.modelo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import series_back.modelo.entities.User;
import series_back.modelo.repository.IUserRepository;

import java.util.Optional;

@Service
public class UserServiceImplMy8 extends GenericoCRUDServiceImplMy8<User, Long> implements IUserService, UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected IUserRepository getRepository() {
        return userRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
            }
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar recuperar el usuario por nombre de usuario");
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("El email no puede ser nulo o vacío");
            }
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar recuperar el usuario por email");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getAuthorities())
                        .build()
                ).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
