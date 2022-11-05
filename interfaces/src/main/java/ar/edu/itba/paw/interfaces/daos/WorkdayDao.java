package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.List;

public interface WorkdayDao extends GenericDao<Workday, Integer> {
    List<Workday> findByUser(User user);

    List<Workday> findByDoctor(Doctor doctor);

    List<Workday> findByDoctor(Doctor doctor, WorkdayDay day);

    boolean doctorWorks(Doctor doctor, AppointmentTimeSlot timeSlot);

    Collection<Workday> create(Collection<Workday> workdays);
}
