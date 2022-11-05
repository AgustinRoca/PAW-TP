package ar.edu.itba.paw.interfaces.daos.generic;

import ar.edu.itba.paw.models.GenericModel;

import java.util.List;

/**
 * This provides a generic listable DAO interface
 * This interface should be implemented by all DAOs that
 * support searching data by name
 *
 * @param <M> the DAO model type
 * @param <I> the Model's id type
 */
public interface GenericSearchableDao<M extends GenericModel<I>, I> extends GenericDao<M, I> {
    /**
     * Returns a collection of <M> that have a name similar to the one provided.
     * The search is not case-sensitive nor exact
     *
     * @param name the <M>'s name
     * @return a collection of matched <M>
     */
    List<M> findByName(String name);
}
