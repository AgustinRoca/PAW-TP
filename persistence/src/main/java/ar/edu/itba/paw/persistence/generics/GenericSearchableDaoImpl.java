package ar.edu.itba.paw.persistence.generics;

import ar.edu.itba.paw.interfaces.daos.generic.GenericSearchableDao;
import ar.edu.itba.paw.models.GenericModel;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

/**
 * This provides a generic listable DAO abstract class
 * This class should be extended by all DAOs that
 * support searching data by name
 *
 * @param <M> the DAO model type
 * @param <I> the Model's id type
 */
public abstract class GenericSearchableDaoImpl<M extends GenericModel<I>, I> extends GenericDaoImpl<M, I> implements GenericSearchableDao<M, I> {
    public GenericSearchableDaoImpl(Class<M> mClass, SingularAttribute<? super M, I> idAttribute) {
        super(mClass, idAttribute);
    }

    /**
     * Returns a collection of <M> that have a name similar to the one provided.
     * The search is not case-sensitive nor exact.
     * By default, runs a query against a column name = "name". If it doesn't exist, it will throw an SQLException
     *
     * @param name the model's name
     * @return a collection of matched models
     */
    @Override
    public List<M> findByName(String name) {
        return this.findByIgnoreCase(this.getNameAttribute(), name);
    }

    protected abstract SingularAttribute<? super M, ?> getNameAttribute();
}
