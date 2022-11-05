package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.Workday;
import org.joda.time.LocalDateTime;

import java.util.Collection;
import java.util.List;

public interface AppointmentDao extends GenericDao<Appointment, Integer> {
    List<Appointment> find(Patient patient);

    List<Appointment> findByPatients(Collection<Patient> patient);

    List<Appointment> find(Doctor doctor);

    List<Appointment> findByDoctors(Collection<Doctor> doctors);

    List<Appointment> findPending(Patient patient);

    List<Appointment> findPending(Doctor doctor);

    List<Appointment> findPending(Patient patient, Doctor doctor);

    List<Appointment> findByDoctorsAndDate(Collection<Doctor> doctors, LocalDateTime date);

    List<Appointment> findByDoctorsAndDate(Collection<Doctor> doctors, LocalDateTime fromDate, LocalDateTime toDate);

    List<Appointment> findPendingByDoctorsAndDate(Collection<Doctor> doctors, LocalDateTime fromDate, LocalDateTime toDate);

    List<Appointment> findByPatientsAndDate(Collection<Patient> patients, LocalDateTime fromDate, LocalDateTime toDate);

    List<Appointment> findPendingByPatientsAndDate(Collection<Patient> patients, LocalDateTime fromDate, LocalDateTime toDate);

    List<Appointment> findByDate(Patient patient, LocalDateTime date);

    List<Appointment> findByPatientsFromDate(Collection<Patient> patients, LocalDateTime from);

    List<Appointment> findPendingByWorkday(Workday workday);

    List<Appointment> findAllAppointmentsToNotifyUpTo(LocalDateTime to);

    void cancelAppointments(Collection<Appointment> appointments);
}
