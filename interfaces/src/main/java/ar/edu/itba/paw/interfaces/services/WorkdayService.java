package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.List;

public interface WorkdayService extends GenericService<Workday, Integer> {
    List<Workday> findByUser(User user);

    List<Workday> findByDoctor(Doctor doctor);

    List<Workday> findByDoctor(Doctor doctor, WorkdayDay day);

    boolean doctorWorks(Doctor doctor, AppointmentTimeSlot timeSlot);

    Collection<Workday> create(Collection<Workday> workdays);

    void remove(Integer id, User user);
}
