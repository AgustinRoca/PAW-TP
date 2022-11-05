package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.OfficeDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.generics.GenericSearchableDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Repository
public class OfficeDaoImpl extends GenericSearchableDaoImpl<Office, Integer> implements OfficeDao {
    public OfficeDaoImpl() {
        super(Office.class, Office_.id);
    }

    @Override
    public List<Office> findByCountry(Country country) {
        if (country == null) {
            throw new IllegalArgumentException();
        }
        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Office> query = builder.createQuery(Office.class);
        Root<Office> root = query.from(Office.class);
        Join<Office, Locality> localityJoin = root.join(Office_.locality);
        Join<Locality, Province> provinceJoin = localityJoin.join(Locality_.province);
        Join<Province, Country> countryJoin = provinceJoin.join(Province_.country);

        query.select(root);
        query.where(builder.equal(countryJoin.get(Country_.id), country.getId()));

        return this.getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public List<Office> findByProvince(Province province) {
        if (province == null) {
            throw new IllegalArgumentException();
        }
        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Office> query = builder.createQuery(Office.class);
        Root<Office> root = query.from(Office.class);
        Join<Office, Locality> localityJoin = root.join(Office_.locality);
        Join<Locality, Province> provinceJoin = localityJoin.join(Locality_.province);

        query.select(root);
        query.where(builder.equal(provinceJoin.get(Province_.id), province.getId()));

        return this.getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public List<Office> findByLocality(Locality locality) {
        if (locality == null) {
            throw new IllegalArgumentException();
        }
        return this.findBy(Office_.locality, locality);
    }

    @Override
    protected SingularAttribute<? super Office, ?> getNameAttribute() {
        return Office_.name;
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<Office> query, Root<Office> root) {
        query.orderBy(builder.asc(root.get(Office_.name)));
    }
}
