package ar.edu.itba.paw.webapp.models;

import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.User;

import java.util.Collection;

public abstract class UserMeFactory {
    public static UserMe withPatients(User user, Collection<Patient> patients) {
        return new UserMe(user, patients, null);
    }

    public static UserMe withDoctors(User user, Collection<Doctor> doctors) {
        return new UserMe(user, null, doctors);
    }
}
