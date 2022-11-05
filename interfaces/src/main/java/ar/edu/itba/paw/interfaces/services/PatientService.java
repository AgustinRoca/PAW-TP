package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.Office;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface PatientService extends GenericService<Patient, Integer> {
    Optional<Patient> findByUserAndOffice(User user, Office office);

    List<Patient> findByUser(User user);
}
