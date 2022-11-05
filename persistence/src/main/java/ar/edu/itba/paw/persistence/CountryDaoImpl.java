package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.CountryDao;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Country_;
import ar.edu.itba.paw.persistence.generics.GenericSearchableDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

@Repository
public class CountryDaoImpl extends GenericSearchableDaoImpl<Country, String> implements CountryDao {
    public CountryDaoImpl() {
        super(Country.class, Country_.id);
    }

    @Override
    protected SingularAttribute<? super Country, ?> getNameAttribute() {
        return Country_.name;
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<Country> query, Root<Country> root) {
        query.orderBy(builder.asc(root.get(Country_.name)));
    }

    @Override
    @Transactional
    public Country create(Country c) {
        if (c == null || c.getId() == null || c.getId().length() != 2) {
            throw new IllegalArgumentException();
        }
        c.setId(c.getId().toUpperCase());
        if (!c.getId().matches("[A-Z][A-Z]")) {
            throw new IllegalArgumentException();
        }
        return super.create(c);
    }
}
