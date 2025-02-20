package series_back.modelo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import series_back.modelo.entities.Role;
import series_back.modelo.repository.IRoleRepository;

@Service
public class RoleServiceImplMy8 extends GenericoCRUDServiceImplMy8<Role, Long> implements IRoleService {

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    protected IRoleRepository getRepository() {
        return roleRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        try {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío");
            }
            return roleRepository.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar recuperar el rol por nombre");
        }
    }

}
