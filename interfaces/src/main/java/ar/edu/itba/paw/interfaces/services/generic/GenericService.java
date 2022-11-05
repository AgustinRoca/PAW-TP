package ar.edu.itba.paw.interfaces.services.generic;

import ar.edu.itba.paw.models.GenericModel;
import ar.edu.itba.paw.models.ModelMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This provides a generic Service interface
 *
 * @param <M> the Service model type
 * @param <I> the Model's id type
 */
public interface GenericService<M extends GenericModel<I>, I> {
    Optional<M> findById(I id);

    List<M> findByIds(Collection<I> ids);

    M create(M model);

    void update(M model);

    void remove(I id);

    List<M> list();

    ModelMetadata count();
}
