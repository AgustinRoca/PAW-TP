package ar.edu.itba.paw.interfaces.daos.generic;

import ar.edu.itba.paw.models.GenericModel;
import ar.edu.itba.paw.models.ModelMetadata;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This provides a generic DAO interface
 *
 * @param <M> the DAO model type
 * @param <I> the Model's id type
 */
public interface GenericDao<M extends GenericModel<I>, I> {
    Optional<M> findById(I id);

    List<M> findByIds(Collection<I> ids);

    M create(M model);

    void update(M model);

    void remove(M model);

    void remove(I id);

    List<M> list();

    ModelMetadata count();

    ModelMetadata count(Map<SingularAttribute<? super M, ?>, Object> parametersValues);
}
