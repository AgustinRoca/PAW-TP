package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.PatientDao;
import ar.edu.itba.paw.models.Office;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.Patient_;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.generics.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PatientDaoImpl extends GenericDaoImpl<Patient, Integer> implements PatientDao {
    public PatientDaoImpl() {
        super(Patient.class, Patient_.id);
    }

    @Override
    public Optional<Patient> findByUserAndOffice(User user, Office office) {
        Map<SingularAttribute<? super Patient, ?>, Object> parameters = new HashMap<>();
        parameters.put(Patient_.user, user);
        parameters.put(Patient_.office, office);
        return this.findBy(parameters).stream().findFirst();
    }

    @Override
    public List<Patient> findByUser(User user) {
        return this.findBy(Patient_.user, user);
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<Patient> query, Root<Patient> root) {
    }
}
