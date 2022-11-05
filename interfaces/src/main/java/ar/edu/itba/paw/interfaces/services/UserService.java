package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.interfaces.services.generic.GenericSearchableService;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.Patient;
import ar.edu.itba.paw.models.User;

import java.util.Locale;
import java.util.Optional;

public interface UserService extends GenericSearchableService<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationTokenId(Integer id);

    boolean isDoctor(User user);

    User createAsDoctor(User user, Doctor doctor) throws EmailAlreadyExistsException;

    void updatePassword(User user, String newPassword);

    void update(User user) throws EmailAlreadyExistsException;

    Patient createNewPatient(Patient patient);

    String generateVerificationToken(User user, Locale locale, String confirmationRelativeUrl);

    boolean verify(User user, String token);
}
