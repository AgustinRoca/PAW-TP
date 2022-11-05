package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.PatientDao;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.Office;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.generics.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl extends GenericServiceImpl<PatientDao, Patient, Integer> implements PatientService {
    @Autowired
    private PatientDao repository;

    @Override
    public Optional<Patient> findByUserAndOffice(User user, Office office) {
        return this.repository.findByUserAndOffice(user, office);
    }

    @Override
    public List<Patient> findByUser(User user) {
        return this.repository.findByUser(user);
    }

    @Override
    protected PatientDao getRepository() {
        return this.repository;
    }
}
