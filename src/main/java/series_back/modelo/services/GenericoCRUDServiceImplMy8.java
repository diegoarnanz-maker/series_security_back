package series_back.modelo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class GenericoCRUDServiceImplMy8<E, ID> implements IGenericoCRUD<E, ID> {

    protected abstract JpaRepository<E, ID> getRepository();

    @Override
    public List<E> findAll() {
        try {
            return getRepository().findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar recuperar todos los registros de la base de datos");
        }
    }

    @Override
    public E create(E entity) {
        try {
            if (entity == null) {
                throw new IllegalArgumentException("La entidad no puede ser nula");
            }

            System.out.println("DEBUG: Entidad recibida para guardar: " + entity);

            return getRepository().save(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar crear la entidad en la base de datos. " + e.getMessage());
        }
    }

    @Override
    public Optional<E> read(ID id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El ID no puede ser nulo");
            }
            return getRepository().findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar recuperar la entidad por ID");
        }
    }

    @Override
    public E update(E entity) {
        try {
            if (entity == null) {
                throw new IllegalArgumentException("La entidad no puede ser nula");
            }
            return getRepository().save(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar actualizar la entidad en la base de datos");
        }
    }

    @Override
    public void delete(ID id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El ID no puede ser nulo");
            }
            if (!getRepository().existsById(id)) {
                throw new IllegalArgumentException("El ID no existe en la base de datos");
            }
            getRepository().deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar eliminar la entidad de la base de datos");
        }
    }
}
