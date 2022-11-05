package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.MediCareException;
import ar.edu.itba.paw.interfaces.daos.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.interfaces.services.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.interfaces.services.exceptions.InvalidEmailDomain;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.generics.GenericSearchableServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl extends GenericSearchableServiceImpl<UserDao, User, Integer> implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao repository;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Set<String> invalidEmailDomains = new HashSet<>();

    public UserServiceImpl() {
        Resource invalidEmailDomainsFile = new ClassPathResource("/invalidEmailDomains.json");
        try {
            this.invalidEmailDomains = new HashSet<>(Arrays.asList(new ObjectMapper().readValue(invalidEmailDomainsFile.getInputStream(), String[].class)));
        } catch (NullPointerException | IOException e) {
            LOGGER.error("Reading InvalidEmailDomains file. File present: {}", invalidEmailDomainsFile.exists() ? "YES" : "NO");
        }
    }

    @Override
    @Transactional
    public User create(User user) throws EmailAlreadyExistsException {
        if (this.repository.existsEmail(user.getEmail()))
            throw new EmailAlreadyExistsException();
        if (!this.isValidEmailDomain(user.getEmail()))
            throw new InvalidEmailDomain();

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return super.create(user);
    }

    @Override
    public boolean isDoctor(User user) {
        return !this.doctorService.findByUser(user).isEmpty();
    }

    @Override
    @Transactional
    public Patient createNewPatient(Patient patient) throws EmailAlreadyExistsException {
        return this.patientService.create(patient);
    }

    @Override
    @Transactional
    public User createAsDoctor(User user, Doctor doctor) throws EmailAlreadyExistsException {
        User newUser = this.create(user);
        Office office;

        if (doctor.getOffice().getId() == null) {
            office = this.officeService.create(doctor.getOffice());
        } else {
            Optional<Office> officeOptional = this.officeService.findById(doctor.getOffice().getId());
            office = officeOptional.orElseGet(() -> this.officeService.create(doctor.getOffice()));
        }

        doctor.setOffice(office);
        this.doctorService.create(doctor);

        return newUser;
    }

    @Override
    @Transactional
    public void updatePassword(User user, String newPassword) {
        Optional<User> userOptional = this.repository.findById(user.getId());
        if (!userOptional.isPresent())
            throw new NoSuchElementException();

        User userToSave = userOptional.get();

        userToSave.setPassword(this.passwordEncoder.encode(newPassword));
        this.refreshTokenService.removeByUserId(userToSave.getId());

        super.update(userToSave);
    }

    @Override
    @Transactional
    public void update(User user) {
        Optional<User> userOptional = this.repository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            if (!userOptional.get().equals(user)) {
                // The email belongs to another user
                throw new EmailAlreadyExistsException();
            }
        } else {
            if ((userOptional = this.findById(user.getId())).isPresent()) {
                // Already exists in DB, the email has changed
                if (!this.isValidEmailDomain(user.getEmail()))
                    throw new InvalidEmailDomain();
                user.setVerified(false);
                if (user.getVerificationToken() != null) {
                    this.verificationTokenService.remove(user.getVerificationToken().getId());
                    user.setVerificationToken(null);
                }
            } else {
                throw new NoSuchElementException();
            }
        }

        if (user.getVerificationToken() != null) {
            Optional<User> userToken = this.repository.findByVerificationTokenId(user.getVerificationToken().getId());
            if (userToken.isPresent() && !userToken.get().equals(user)) {
                throw new MediCareException("Verification token already exists");
            }
        }

        User userToSave = userOptional.get();
        if (user.getPhone() != null) {
            userToSave.setPhone(user.getPhone());
        }
        if (user.getVerificationToken() != null) {
            userToSave.setVerificationToken(user.getVerificationToken());
        }
        if (user.getEmail() != null) {
            userToSave.setEmail(user.getEmail());
        }
        if (user.getVerified() != null) {
            userToSave.setVerified(user.getVerified());
        }
        if (user.getProfilePicture() != null) {
            userToSave.setProfilePicture(user.getProfilePicture());
        }
        if (user.getFirstName() != null) {
            userToSave.setFirstName(user.getFirstName());
        }
        if (user.getSurname() != null) {
            userToSave.setSurname(user.getSurname());
        }

        super.update(userToSave);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.repository.findByEmail(username);
    }

    @Override
    public Optional<User> findByVerificationTokenId(Integer id) {
        return this.repository.findByVerificationTokenId(id);
    }

    @Override
    @Transactional
    public String generateVerificationToken(User user, Locale locale, String confirmationRelativeUrl) {
        user = this.findById(user.getId()).get();

        if (user.getVerified())
            return null;

        String token;
        if (user.getVerificationToken() != null) {
            token = this.verificationTokenService.refresh(user.getVerificationToken());
        } else {
            VerificationToken verificationToken = this.verificationTokenService.generate();
            user.setVerificationToken(verificationToken);
            this.update(user);
            token = verificationToken.getToken();
        }

        emailService.sendEmailConfirmationEmail(user, token, confirmationRelativeUrl, locale);

        return token;
    }

    @Override
    @Transactional
    public boolean verify(User user, String token) {
        if (user.getVerified()) {
            return false;
        }

        if (user.getVerificationToken() != null && user.getVerificationToken().getToken().equals(token)) {
            user.setVerified(true);
            user.setVerificationToken(null);
            update(user);
            verificationTokenService.remove(user.getVerificationToken().getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected UserDao getRepository() {
        return this.repository;
    }

    private boolean isValidEmailDomain(String email) {
        String domain = email.substring(email.lastIndexOf("@") + 1);
        return !this.invalidEmailDomains.contains(domain);
    }
}
