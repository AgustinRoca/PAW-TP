package ar.edu.itba.paw.persistence.generics;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.models.GenericModel;
import ar.edu.itba.paw.models.ModelMetadata;
import ar.edu.itba.paw.models.Paginator;
import ar.edu.itba.paw.persistence.utils.StringSearchType;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.function.Function;

/**
 * This provides a generic DAO implementation with lots of useful methods
 *
 * @param <M> the DAO model type
 * @param <I> the Model's id type
 */
public abstract class GenericDaoImpl<M extends GenericModel<I>, I> implements GenericDao<M, I> {
    private static final Function<Tuple, ModelMetadata> modelMetadataExtractor = tuple -> {
        Long count;
        Object min, max;
        try {
            count = (Long) tuple.get("count");
        } catch (Exception e) {
            count = null;
        }
        try {
            max = tuple.get("max");
        } catch (Exception e) {
            max = null;
        }
        try {
            min = tuple.get("min");
        } catch (Exception e) {
            min = null;
        }

        return new ModelMetadata(count, min, max);
    };

    @PersistenceContext
    private EntityManager entityManager;

    private final Class<M> mClass;
    private final SingularAttribute<? super M, I> idAttribute;

    public GenericDaoImpl(Class<M> mClass, SingularAttribute<? super M, I> idAttribute) {
        this.mClass = mClass;
        this.idAttribute = idAttribute;
    }

    @Override
    public Optional<M> findById(I id) {
        if (id == null)
            throw new IllegalArgumentException();

        M model = this.entityManager.find(this.mClass, id);
        return (model == null) ? Optional.empty() : Optional.of(model);
    }

    @Override
    public List<M> findByIds(Collection<I> ids) {
        if (ids == null)
            throw new IllegalArgumentException();
        if (ids.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(this.mClass);
        Root<M> root = query.from(this.mClass);

        query.select(root);
        Path<I> expression = root.get(this.idAttribute);
        Predicate predicate = expression.in(ids);
        query.where(predicate);

        return this.selectQuery(builder, query, root);
    }

    @Override
    @Transactional
    public M create(M model) {
        if (model == null) {
            throw new IllegalArgumentException();
        }
        this.entityManager.persist(model);
        return model;
    }

    @Override
    @Transactional
    public void update(M model) {
        if (model == null || model.getId() == null) {
            throw new IllegalArgumentException("Model or its id is null");
        }
        if(!findById(model.getId()).isPresent()){
            throw new IllegalArgumentException(model.getClass().getName() + " with id:" + model.getId() + " doesn't exists");
        }
        this.entityManager.unwrap(Session.class).merge(model);
    }

    @Override
    @Transactional
    public void remove(M model) {
        if (model == null) {
            throw new IllegalArgumentException();
        }
        this.remove(model.getId());
    }

    @Override
    @Transactional
    public void remove(I id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        M model = this.entityManager.find(this.mClass, id);
        if (model != null) {
            this.entityManager.remove(model);
        } else {
            throw new IllegalArgumentException("Model is null");
        }
    }

    @Override
    public List<M> list() {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(this.mClass);
        Root<M> root = query.from(this.mClass);

        query.select(root);

        return this.selectQuery(builder, query, root);
    }

    @Override
    public ModelMetadata count() {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);
        Root<M> root = query.from(this.mClass);

        query.multiselect(builder.count(root).alias("count"));
        query.distinct(true);

        return modelMetadataExtractor.apply(this.entityManager.createQuery(query).getSingleResult());
    }

    @Override
    public ModelMetadata count(Map<SingularAttribute<? super M, ?>, Object> parametersValues) {
        if (parametersValues == null)
            throw new IllegalArgumentException();

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);
        Root<M> root = query.from(this.mClass);
        Predicate[] predicates = new Predicate[parametersValues.size()];

        int i = 0;
        for (Map.Entry<SingularAttribute<? super M, ?>, Object> parameter : parametersValues.entrySet()) {
            predicates[i++] = builder.equal(root.get(parameter.getKey()), parameter.getValue());
        }

        query.where(builder.and(predicates));
        return this.count(builder, query, root);
    }

    protected ModelMetadata metadata(SingularAttribute<? super M, ?> countAttribute, SingularAttribute<? super M, Number> minMaxAttribute) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);
        Root<M> root = query.from(this.mClass);

        query.multiselect(
                builder.count(root.get(countAttribute)).alias("count"),
                builder.min(root.get(minMaxAttribute)).alias("min"),
                builder.max(root.get(minMaxAttribute)).alias("max")
        );

        return modelMetadataExtractor.apply(this.entityManager.createQuery(query).getSingleResult());
    }

    protected List<M> findBy(SingularAttribute<? super M, ?> attribute, Object value) {
        if (value == null)
            throw new IllegalArgumentException();

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(this.mClass);
        Root<M> root = query.from(this.mClass);

        query.select(root);
        query.where(builder.equal(root.get(attribute), value));

        return this.selectQuery(builder, query, root);
    }

    protected List<M> findBy(Map<SingularAttribute<? super M, ?>, Object> parametersValues) {
        if (parametersValues == null)
            throw new IllegalArgumentException();
        if (parametersValues.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(this.mClass);
        Root<M> root = query.from(this.mClass);
        Predicate[] predicates = new Predicate[parametersValues.size()];
        int i = 0;
        for (Map.Entry<SingularAttribute<? super M, ?>, ?> parameter : parametersValues.entrySet()) {
            predicates[i++] = builder.equal(root.get(parameter.getKey()), parameter.getValue());
        }

        query.select(root);
        query.where(builder.and(predicates));

        return this.selectQuery(builder, query, root);
    }

    protected List<M> findByIn(SingularAttribute<? super M, ?> attribute, Collection<?> values) {
        if (values == null)
            throw new IllegalArgumentException();
        if (values.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(this.mClass);
        Root<M> root = query.from(this.mClass);

        query.select(root);
        Path<?> expression = root.get(attribute);
        Predicate predicate = expression.in(values);
        query.where(predicate);

        return this.selectQuery(builder, query, root);
    }

    protected List<M> findByIgnoreCase(SingularAttribute<? super M, ?> attribute, String value) {
        return this.findByIgnoreCase(attribute, value, StringSearchType.CONTAINS_NO_ACC);
    }

    protected List<M> findByIgnoreCase(SingularAttribute<? super M, ?> attribute, String value, StringSearchType stringSearchType) {
        if (value == null)
            throw new IllegalArgumentException();

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(this.mClass);
        Root<M> root = query.from(this.mClass);

        query.select(root);
        value = value.replace("%", "\\%");
        value = value.replace("_", "\\%");
        query.where(
                builder.like(
                        builder.lower(root.get(attribute).as(String.class)),
                        stringSearchType.transform(value.toLowerCase())
                )
        );

        return this.selectQuery(builder, query, root);
    }

    protected List<M> selectQuery(CriteriaBuilder builder, CriteriaQuery<M> query, Root<M> root) {
        this.insertOrderBy(builder, query, root);
        return this.entityManager.createQuery(query).getResultList();
    }

    protected Paginator<M> selectQuery(CriteriaBuilder builder, CriteriaQuery<M> query, CriteriaQuery<Tuple> tupleQuery, Root<M> root, int page, int pageSize) {
        this.insertOrderBy(builder, query, root);

        List<M> list = this.entityManager.createQuery(query)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return new Paginator<>(list, page, pageSize, this.selectQueryMetadata(tupleQuery, root).getCount());
    }

    protected Optional<M> selectSingleQuery(CriteriaBuilder builder, CriteriaQuery<M> query, Root<M> root) {
        this.insertOrderBy(builder, query, root);
        return Optional.of(this.entityManager.createQuery(query).getSingleResult());
    }

    protected ModelMetadata selectQueryMetadata(CriteriaQuery<Tuple> query, Root<?> root) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        query.multiselect(builder.count(root).alias("count"));
        if (query.getRoots().isEmpty())
            query.from(this.mClass);

        return modelMetadataExtractor.apply(this.entityManager.createQuery(query).getSingleResult());
    }

    protected boolean exists(Map<SingularAttribute<? super M, ?>, Object> parametersValues) {
        return this.count(parametersValues).getCount() > 0;
    }

    protected <T> ModelMetadata count(CriteriaBuilder builder, CriteriaQuery<Tuple> query, Root<T> root) {
        query.multiselect(builder.count(root).alias("count"));
        query.distinct(true);
        if (query.getRoots().isEmpty())
            query.from(this.mClass);

        return modelMetadataExtractor.apply(this.entityManager.createQuery(query).getSingleResult());
    }

    protected <T> boolean exists(CriteriaBuilder builder, CriteriaQuery<Tuple> query, Root<T> root) {
        return this.count(builder, query, root).getCount() > 0;
    }

    protected int executeUpdate(CriteriaUpdate<M> update) {
        return this.entityManager.createQuery(update).executeUpdate();
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    protected abstract void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<M> query, Root<M> root);
}
