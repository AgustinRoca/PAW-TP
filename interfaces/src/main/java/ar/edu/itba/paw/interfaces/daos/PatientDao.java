package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.models.Office;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface PatientDao extends GenericDao<Patient, Integer> {
    Optional<Patient> findByUserAndOffice(User user, Office office);

    List<Patient> findByUser(User user);
}
