package series_back.modelo.services;

import java.util.List;
import java.util.Optional;

//Principio DRY( Dont repeat yourself)
public interface IGenericoCRUD<E, ID> {

    List<E> findAll();

    E create(E entity);

    Optional<E> read(ID id);

    E update(E entity);

    void delete(ID id);
    
}
