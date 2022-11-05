package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.WorkdayDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.generics.GenericDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WorkdayDaoImpl extends GenericDaoImpl<Workday, Integer> implements WorkdayDao {
    private static final int BATCH_SIZE = 5;

    public WorkdayDaoImpl() {
        super(Workday.class, Workday_.id);
    }

    @Override
    public List<Workday> findByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Workday> query = builder.createQuery(Workday.class);
        Root<Workday> root = query.from(Workday.class);
        Join<Workday, Doctor> doctorJoin = root.join(Workday_.doctor);
        Join<Doctor, User> userJoin = doctorJoin.join(Doctor_.user);

        query.select(root);
        query.where(builder.equal(userJoin.get(User_.id), user.getId()));

        return this.getEntityManager().createQuery(query).getResultList();
    }

    @Override
    public List<Workday> findByDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException();
        }
        return this.findBy(Workday_.doctor, doctor);
    }

    @Override
    public List<Workday> findByDoctor(Doctor doctor, WorkdayDay day) {
        if (doctor == null || day == null) {
            throw new IllegalArgumentException();
        }
        Map<SingularAttribute<? super Workday, ?>, Object> parametersValues = new HashMap<>();
        parametersValues.put(Workday_.doctor, doctor);
        parametersValues.put(Workday_.day, day);
        return this.findBy(parametersValues);
    }

    @Override
    public boolean doctorWorks(Doctor doctor, AppointmentTimeSlot timeSlot) {
        if (doctor == null || timeSlot == null)
            throw new IllegalArgumentException();

        List<Workday> workdays = findByDoctor(doctor, WorkdayDay.from(timeSlot.getDate()));
        for (Workday workday : workdays){
            if((workday.getStartTime().compareTo(timeSlot.getDate().toLocalTime()) <= 0) &&
                    (workday.getEndTime().compareTo(timeSlot.getToDate().toLocalTime()) >= 0)){
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public Collection<Workday> create(Collection<Workday> workdays) {
        if(workdays == null || workdays.isEmpty()){
            throw new IllegalArgumentException();
        }
        int i=0;
        for (Workday workday : workdays){
            // Memory Optimization
            if (i > 0 && i % BATCH_SIZE == 0) {
                getEntityManager().flush();
                getEntityManager().clear();
            }
            super.create(workday);
            i++;
        }
        return workdays;
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<Workday> query, Root<Workday> root) {
        query.orderBy(
                builder.asc(root.get(Workday_.day)),
                builder.asc(root.get(Workday_.startHour)),
                builder.asc(root.get(Workday_.startMinute))
        );
    }
}
