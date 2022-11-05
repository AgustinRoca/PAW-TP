package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericSearchableService;
import ar.edu.itba.paw.models.DoctorSpecialty;
import ar.edu.itba.paw.models.User;

public interface DoctorSpecialtyService extends GenericSearchableService<DoctorSpecialty, Integer> {
    void addToUser(int doctorSpecialty, User user);

    void removeFromUser(int doctorSpecialty, User user);
}
