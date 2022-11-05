package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.exceptions.AppointmentAlreadyCancelledException;
import ar.edu.itba.paw.interfaces.services.exceptions.AppointmentAlreadyCompletedException;
import ar.edu.itba.paw.interfaces.services.exceptions.InvalidAppointmentDateException;
import ar.edu.itba.paw.interfaces.services.exceptions.InvalidAppointmentStatusChangeException;
import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.*;
import org.joda.time.LocalDateTime;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface AppointmentService extends GenericService<Appointment, Integer> {
    List<Appointment> findAllAppointments(Doctor doctor);

    List<Appointment> findAllAppointmentsOfDoctors(Collection<Doctor> doctors);

    List<Appointment> findAllAppointments(Patient patient);

    List<Appointment> findAllAppointmentsOfPatients(Collection<Patient> patients);

    List<Appointment> findTodayAppointments(Collection<Doctor> doctor);

    List<Appointment> findTodayAppointments(Patient patient);

    List<Appointment> findByDate(Doctor doctor, LocalDateTime date);

    List<Appointment> findAppointmentsOfDoctorsFromDate(Collection<Doctor> doctors, LocalDateTime date);

    List<Appointment> findAppointmentsOfDoctorsInDateInterval(Collection<Doctor> doctors, LocalDateTime from, LocalDateTime to);

    List<Appointment> findPendingAppointmentsOfDoctorsInDateInterval(Collection<Doctor> doctors, LocalDateTime from, LocalDateTime to);

    List<Appointment> findAppointmentsOfPatientsInDateInterval(Collection<Patient> patients, LocalDateTime from, LocalDateTime to);

    List<Appointment> findPendingAppointmentsOfPatientsInDateInterval(Collection<Patient> patients, LocalDateTime from, LocalDateTime to);

    List<Appointment> findPendingByWorkday(Workday workday);

    void setStatus(Appointment appointment, AppointmentStatus status) throws
            AppointmentAlreadyCancelledException,
            InvalidAppointmentStatusChangeException,
            AppointmentAlreadyCompletedException;

    List<AppointmentTimeSlot> findAvailableTimeslotsInDateInterval(Doctor doctor, LocalDateTime fromDate, LocalDateTime toDate);

    List<AppointmentTimeSlot> findAvailableTimeslotsfromDate(Doctor doctor, LocalDateTime date);

    List<Appointment> cancelAppointments(Workday workday);

    Map<Workday, Integer> appointmentQtyByWorkdayOfUser(User user);

    List<Appointment> findAllAppointmentsInIntervalToNotify(LocalDateTime from, LocalDateTime to);

    @Override
    @Deprecated
    default void remove(Integer id){
        throw new UnsupportedOperationException();
    }

    void remove(Integer id, User user, Locale locale);

    Appointment create(Appointment model, User patient) throws InvalidAppointmentDateException;

    List<List<AppointmentTimeSlot>> findTimeslotsSortedByWeekday(Doctor doctor, LocalDateTime from, LocalDateTime to);
}
