package ar.edu.itba.paw.services.generics;

import ar.edu.itba.paw.interfaces.daos.generic.GenericSearchableDao;
import ar.edu.itba.paw.interfaces.services.generic.GenericSearchableService;
import ar.edu.itba.paw.models.GenericModel;

import java.util.List;

/**
 * This provides a generic Service abstract class
 * This class should be extended by all Services that
 * support searching data by name
 *
 * @param <M> the Service model type
 * @param <I> the Model's id type
 */
public abstract class GenericSearchableServiceImpl<DAO extends GenericSearchableDao<M, I>, M extends GenericModel<I>, I> extends GenericServiceImpl<DAO, M, I> implements GenericSearchableService<M, I> {
    @Override
    public List<M> findByName(String name) {
        return this.getRepository().findByName(name);
    }
}
