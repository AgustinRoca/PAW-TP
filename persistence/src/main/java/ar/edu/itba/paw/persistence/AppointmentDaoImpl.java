package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.AppointmentDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.generics.GenericDaoImpl;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AppointmentDaoImpl extends GenericDaoImpl<Appointment, Integer> implements AppointmentDao {
    public AppointmentDaoImpl() {
        super(Appointment.class, Appointment_.id);
    }

    @Override
    public List<Appointment> find(Patient patient) {
        return this.findBy(Appointment_.patient, patient);
    }

    @Override
    public List<Appointment> findByPatients(Collection<Patient> patients) {
        return this.findByIn(Appointment_.patient, patients);
    }

    @Override
    public List<Appointment> find(Doctor doctor) {
        return this.findBy(Appointment_.doctor, doctor);
    }

    @Override
    public List<Appointment> findByDoctors(Collection<Doctor> doctors) {
        return this.findByIn(Appointment_.doctor, doctors);
    }

    @Override
    public List<Appointment> findPending(Patient patient) {
        if (patient == null)
            throw new IllegalArgumentException();

        Map<SingularAttribute<? super Appointment, ?>, Object> parameters = new HashMap<>();
        parameters.put(Appointment_.patient, patient);
        parameters.put(Appointment_.appointmentStatus, AppointmentStatus.PENDING);
        return this.findBy(parameters);
    }

    @Override
    public List<Appointment> findPending(Doctor doctor) {
        if (doctor == null)
            throw new IllegalArgumentException();

        Map<SingularAttribute<? super Appointment, ?>, Object> parameters = new HashMap<>();
        parameters.put(Appointment_.doctor, doctor);
        parameters.put(Appointment_.appointmentStatus, AppointmentStatus.PENDING);
        return this.findBy(parameters);
    }

    @Override
    public List<Appointment> findPending(Patient patient, Doctor doctor) {
        if (patient == null || doctor == null)
            throw new IllegalArgumentException();

        Map<SingularAttribute<? super Appointment, ?>, Object> parameters = new HashMap<>();
        parameters.put(Appointment_.patient, patient);
        parameters.put(Appointment_.doctor, doctor);
        parameters.put(Appointment_.appointmentStatus, AppointmentStatus.PENDING);
        return this.findBy(parameters);
    }

    @Override
    public List<Appointment> findByDoctorsAndDate(Collection<Doctor> doctors, LocalDateTime date) {
        if (date == null || doctors == null)
            throw new IllegalArgumentException();
        if (doctors.isEmpty())
            return Collections.emptyList();

        LocalDateTime fromDate = new LocalDateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 0, 0);
        LocalDateTime toDate = new LocalDateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 23, 59);
        return this.findByDoctorsAndDate(doctors, fromDate, toDate);
    }

    @Override
    public List<Appointment> findByDoctorsAndDate(Collection<Doctor> doctors, LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || toDate == null || doctors == null)
            throw new IllegalArgumentException();
        if (doctors.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Appointment> query = builder.createQuery(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.select(root);
        Path<?> expression = root.get(Appointment_.doctor);
        Predicate predicate = expression.in(doctors);
        query.where(builder.and(
                predicate,
                builder.between(
                        root.get(Appointment_.fromDate),
                        fromDate,
                        toDate
                )
        ));

        return this.selectQuery(builder, query, root);
    }

    @Override
    public List<Appointment> findPendingByDoctorsAndDate(Collection<Doctor> doctors, LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || toDate == null || doctors == null)
            throw new IllegalArgumentException();
        if (doctors.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Appointment> query = builder.createQuery(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.select(root);
        Path<?> expression = root.get(Appointment_.doctor);
        Predicate predicate = expression.in(doctors);
        query.where(builder.and(
                predicate,
                builder.between(
                        root.get(Appointment_.fromDate),
                        fromDate,
                        toDate
                ),
                builder.equal(root.get(Appointment_.appointmentStatus), AppointmentStatus.PENDING)
        ));

        return this.selectQuery(builder, query, root);
    }

    @Override
    public List<Appointment> findByPatientsAndDate(Collection<Patient> patients, LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || patients == null)
            throw new IllegalArgumentException();
        if (patients.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Appointment> query = builder.createQuery(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.select(root);
        Path<?> expression = root.get(Appointment_.patient);
        Predicate predicate = expression.in(patients);
        query.where(builder.and(
                predicate,
                builder.between(
                        root.get(Appointment_.fromDate),
                        fromDate,
                        toDate
                )
        ));

        return this.selectQuery(builder, query, root);
    }

    @Override
    public List<Appointment> findPendingByPatientsAndDate(Collection<Patient> patients, LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || patients == null)
            throw new IllegalArgumentException();
        if (patients.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Appointment> query = builder.createQuery(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.select(root);
        Path<?> expression = root.get(Appointment_.patient);
        Predicate predicate = expression.in(patients);
        query.where(builder.and(
                predicate,
                builder.between(
                        root.get(Appointment_.fromDate),
                        fromDate,
                        toDate
                ),
                builder.equal(root.get(Appointment_.appointmentStatus), AppointmentStatus.PENDING)
        ));

        return this.selectQuery(builder, query, root);
    }

    @Override
    public List<Appointment> findByDate(Patient patient, LocalDateTime date) {
        if (date == null || patient == null)
            throw new IllegalArgumentException();

        List<Patient> patients = new LinkedList<>();
        patients.add(patient);

        LocalDateTime toDate = new LocalDateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 23, 59);

        return this.findByPatientsAndDate(patients, date, toDate);
    }

    @Override
    public List<Appointment> findByPatientsFromDate(Collection<Patient> patients, LocalDateTime from) {
        if (from == null || patients == null)
            throw new IllegalArgumentException();
        if (patients.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Appointment> query = builder.createQuery(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.select(root);
        Path<?> expression = root.get(Appointment_.patient);
        Predicate predicate = expression.in(patients);
        query.where(builder.and(
                predicate,
                builder.greaterThanOrEqualTo(root.get(Appointment_.fromDate), from)
        ));

        return this.selectQuery(builder, query, root);
    }

    @Override
    public List<Appointment> findPendingByWorkday(Workday workday) {
        if (workday == null || workday.getDoctor() == null || workday.getDay() == null)
            throw new IllegalArgumentException();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Appointment> query = builder.createQuery(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate = new LocalDateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0).plusDays(1);

        query.select(root);
        query.where(builder.and(
                builder.equal(root.get(Appointment_.doctor), workday.getDoctor()),
                builder.equal(root.get(Appointment_.appointmentStatus), AppointmentStatus.PENDING),
                builder.equal(
                        builder.function("DATE_PART", Integer.class, builder.literal("isodow"), root.get(Appointment_.fromDate)),
                        workday.getDay().toInteger()
                ),
                builder.and(
                        builder.or(
                                builder.and(
                                        builder.equal(
                                                builder.function("HOUR", Integer.class, root.get(Appointment_.fromDate)),
                                                workday.getStartHour()
                                        ),
                                        builder.greaterThanOrEqualTo(
                                                builder.function("MINUTE", Integer.class, root.get(Appointment_.fromDate)),
                                                workday.getStartMinute()
                                        )
                                ),
                                builder.greaterThan(
                                        builder.function("HOUR", Integer.class, root.get(Appointment_.fromDate)),
                                        workday.getStartHour()
                                )
                        ),
                        builder.or(
                                builder.and(
                                        builder.equal(
                                                builder.function("HOUR", Integer.class, root.get(Appointment_.fromDate)),
                                                workday.getEndHour()
                                        ),
                                        builder.lessThanOrEqualTo(
                                                builder.function("MINUTE", Integer.class, root.get(Appointment_.fromDate)),
                                                workday.getEndMinute()
                                        )
                                ),
                                builder.lessThan(
                                        builder.function("HOUR", Integer.class, root.get(Appointment_.fromDate)),
                                        workday.getEndHour()
                                )
                        )
                )
        ));
        return this.selectQuery(builder, query, root);
    }

    @Override
    public List<Appointment> findAllAppointmentsToNotifyUpTo(LocalDateTime to) {
        if (to == null)
            throw new IllegalArgumentException();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Appointment> query = builder.createQuery(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.select(root);
        query.where(
                builder.and(
                    builder.and(
                        builder.equal(root.get(Appointment_.wasNotificationEmailSent),false),
                        builder.and(
                                builder.greaterThanOrEqualTo(root.get(Appointment_.fromDate), LocalDateTime.now()),
                                builder.lessThanOrEqualTo(root.get(Appointment_.fromDate), to)
                        )
                    ),
                        builder.equal(root.get(Appointment_.appointmentStatus), AppointmentStatus.PENDING)
                )
        );

        return this.selectQuery(builder, query, root);
    }

    @Transactional
    @Override
    public void remove(Appointment appointment) {
        if (appointment == null)
            throw new IllegalArgumentException();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaUpdate<Appointment> query = builder.createCriteriaUpdate(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.set(root.get(Appointment_.appointmentStatus), AppointmentStatus.CANCELLED);
        query.where(builder.equal(root.get(Appointment_.id), appointment.getId()));
        this.executeUpdate(query);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        if (id == null)
            throw new IllegalArgumentException();

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaUpdate<Appointment> query = builder.createCriteriaUpdate(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.set(root.get(Appointment_.appointmentStatus), AppointmentStatus.CANCELLED);
        query.where(builder.equal(root.get(Appointment_.id), id));
        this.executeUpdate(query);
    }

    @Transactional
    @Override
    public void cancelAppointments(Collection<Appointment> appointments) {
        if (appointments == null)
            throw new IllegalArgumentException();
        if (appointments.isEmpty())
            return;

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaUpdate<Appointment> query = builder.createCriteriaUpdate(Appointment.class);
        Root<Appointment> root = query.from(Appointment.class);

        query.set(root.get(Appointment_.appointmentStatus), AppointmentStatus.CANCELLED);
        Path<?> expression = root.get(Appointment_.id);
        Predicate predicate = expression.in(appointments.stream().map(GenericModel::getId).collect(Collectors.toList()));
        query.where(predicate);

        this.executeUpdate(query);
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<Appointment> query, Root<Appointment> root) {
        query.orderBy(builder.asc(root.get(Appointment_.fromDate)));
    }
}
