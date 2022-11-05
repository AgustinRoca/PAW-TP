package ar.edu.itba.paw.webapp.models;

import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.User;

import java.util.Collection;

public class UserMe {
    private final User user;
    private final Collection<Patient> patients;
    private final Collection<Doctor> doctors;

    protected UserMe(User user, Collection<Patient> patients, Collection<Doctor> doctors) {
        this.user = user;
        this.patients = patients;
        this.doctors = doctors;
    }

    public User getUser() {
        return this.user;
    }

    public Collection<Patient> getPatients() {
        return this.patients;
    }

    public Collection<Doctor> getDoctors() {
        return this.doctors;
    }
}
