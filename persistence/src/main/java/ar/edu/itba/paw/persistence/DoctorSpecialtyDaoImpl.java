package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.DoctorSpecialtyDao;
import ar.edu.itba.paw.models.DoctorSpecialty;
import ar.edu.itba.paw.models.DoctorSpecialty_;
import ar.edu.itba.paw.persistence.generics.GenericSearchableDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

@Repository
public class DoctorSpecialtyDaoImpl extends GenericSearchableDaoImpl<DoctorSpecialty, Integer> implements DoctorSpecialtyDao {
    public DoctorSpecialtyDaoImpl() {
        super(DoctorSpecialty.class, DoctorSpecialty_.id);
    }

    @Override
    protected SingularAttribute<? super DoctorSpecialty, ?> getNameAttribute() {
        return DoctorSpecialty_.name;
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<DoctorSpecialty> query, Root<DoctorSpecialty> root) {
        query.orderBy(builder.asc(root.get(DoctorSpecialty_.name)));
    }
}
