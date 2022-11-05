package ar.edu.itba.paw.services.generics;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.GenericModel;
import ar.edu.itba.paw.models.ModelMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This provides a generic Service abstract class
 *
 * @param <M> the Service model type
 * @param <I> the Model's id type
 */
public abstract class GenericServiceImpl<DAO extends GenericDao<M, I>, M extends GenericModel<I>, I> implements GenericService<M, I> {
    @Override
    public Optional<M> findById(I id) {
        return this.getRepository().findById(id);
    }

    @Override
    public List<M> findByIds(Collection<I> ids) {
        return this.getRepository().findByIds(ids);
    }

    @Override
    public M create(M model) {
        return this.getRepository().create(model);
    }

    @Override
    public void update(M model) {
        this.getRepository().update(model);
    }

    @Override
    public void remove(I id) {
        this.getRepository().remove(id);
    }

    @Override
    public List<M> list() {
        return this.getRepository().list();
    }

    @Override
    public ModelMetadata count() {
        return this.getRepository().count();
    }

    protected abstract DAO getRepository();
}
