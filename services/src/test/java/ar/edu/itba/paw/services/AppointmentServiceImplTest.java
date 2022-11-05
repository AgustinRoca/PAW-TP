package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.AppointmentDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.WorkdayService;
import ar.edu.itba.paw.interfaces.services.exceptions.InvalidAppointmentStatusChangeException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.AppointmentServiceImpl;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceImplTest {

    private static final String FIRST_NAME = "Firstname";
    private static final String SURNAME = "Surname";
    private static final String PASSWORD = new BCryptPasswordEncoder().encode("Password");
    private static final String EMAIL = "fsurname@testmail.com";
    private static final int USER_ID = 1;
    private static final int OFFICE_ID = 1;
    private static final String LOCALITY = "Localidad";
    private static final int LOCALITY_ID = 1;
    private static final String STREET = "Calle 123";
    private static final String PHONE = "1112345678";
    private static final String OFFICE_EMAIL = "oficina@testmail.com";
    private static final String OFFICE_NAME = "Oficina";
    private static final String URL = "www.oficinahospital.com";
    private static final int PATIENT_ID = 1;
    private static final int YEAR = LocalDateTime.now().getYear() + 1;
    private static final int MONTH = 1;
    private static final int DAY_OF_MONTH = 29;
    private static final int HOUR = 12;
    private static final int MINUTE = 0;
    private static final String DOCTOR_PHONE = "1111223344";
    private static final String DOCTOR_EMAIL = "doctoremail@testmail.com";
    private static final String DOCTOR_SURNAME = "StSurname";
    private static final String DOCTOR_FIRSTNAME = "StFirstname";
    private static final int REGISTRATION_NUMBER = 1;
    private static final int DOCTOR_ID = 1;
    private static final int APPOINTMENT_ID = 1;
    @InjectMocks
    private final AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();

    @Mock
    private AppointmentDao appointmentDaoMock;
    @Mock
    private WorkdayService workdayServiceMock;
    @Mock
    private PatientService patientServiceMock;
    @Mock
    private EmailService emailServiceMock;


    private User userModel() {
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setSurname(SURNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setId(USER_ID);
        return user;
    }

    private Locality localityModel() {
        Locality locality = new Locality();
        locality.setName(LOCALITY);
        locality.setId(LOCALITY_ID);
        return locality;
    }

    private Office officeModel() {
        Office office = new Office();
        office.setId(OFFICE_ID);
        office.setLocality(localityModel());
        office.setStreet(STREET);
        office.setPhone(PHONE);
        office.setEmail(OFFICE_EMAIL);
        office.setName(OFFICE_NAME);
        office.setUrl(URL);
        return office;
    }

    private Patient patientModel() {
        Patient patient = new Patient();
        patient.setUser(userModel());
        patient.setOffice(officeModel());
        patient.setId(PATIENT_ID);
        return patient;
    }

    private Doctor doctorModel() {
        Doctor doctor = new Doctor();
        doctor.setPhone(DOCTOR_PHONE);
        doctor.setEmail(DOCTOR_EMAIL);
        doctor.setRegistrationNumber(REGISTRATION_NUMBER);
        doctor.setId(DOCTOR_ID);
        return doctor;
    }

    private LocalDateTime dateModel() {
        return new LocalDateTime(YEAR, MONTH, DAY_OF_MONTH, HOUR, MINUTE);
    }

    private Appointment appointmentModel() {
        Appointment appointment = new Appointment();
        appointment.setFromDate(dateModel());
        appointment.setPatient(patientModel());
        appointment.setDoctor(doctorModel());
        appointment.setId(APPOINTMENT_ID);
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        return appointment;
    }

//    @Test
//    public void mockExample() {
//        Doctor doctor = doctorModel();
//        Mockito.when(mockDao.find(Mockito.eq(doctor))).thenReturn(Collections.singletonList(appointmentModel()));
//        List<Appointment> appointments = appointmentService.find(doctor);
//        assertEquals(appointments, Collections.singletonList(appointmentModel()));
//    }

    @Test
    public void changeToPossibleStatusOfPendingAppointmentTest() {
        Appointment appointment = appointmentModel();
        AppointmentStatus status = AppointmentStatus.PENDING;
        AppointmentStatus changeToStatus = AppointmentStatus.CANCELLED;
        try {
            appointment.setAppointmentStatus(status);
            appointmentService.setStatus(appointment, changeToStatus);

            changeToStatus = AppointmentStatus.WAITING;
            appointment.setAppointmentStatus(AppointmentStatus.PENDING);
            appointmentService.setStatus(appointment, changeToStatus);
        } catch (Exception e) {
            fail("Failed to set appointment to status:" + changeToStatus.name() + " because exception " + e.getClass().getName() + " was thrown with message:\n" + e.getMessage());
        }
    }

    @Test(expected = InvalidAppointmentStatusChangeException.class)
    public void changeToSeeingStatusOfPendingAppointmentTest() {
        Appointment appointment = appointmentModel();
        AppointmentStatus status = AppointmentStatus.PENDING;
        AppointmentStatus changeToStatus = AppointmentStatus.SEEN;
        appointment.setAppointmentStatus(status);
        appointmentService.setStatus(appointment, changeToStatus);
    }

    @Test
    public void createAppointmentTest(){
        Workday workday = new Workday();
        workday.setDoctor(doctorModel());
        workday.setDay(WorkdayDay.from(dateModel()));
        workday.setStartHour(HOUR);
        workday.setStartMinute(MINUTE);
        workday.setEndHour(HOUR+1);
        workday.setEndMinute(MINUTE);
        workday.setId(APPOINTMENT_ID);

        AppointmentTimeSlot appointmentTimeSlot = new AppointmentTimeSlot();
        appointmentTimeSlot.setDate(dateModel());
        Mockito.when(workdayServiceMock.doctorWorks(Mockito.eq(doctorModel()), Mockito.eq(appointmentTimeSlot)))
                .thenReturn(true);
        Mockito.when(workdayServiceMock.findByDoctor(doctorModel(), WorkdayDay.from(dateModel())))
                .thenReturn(Collections.singletonList(workday));
        Mockito.when(appointmentDaoMock.findByDoctorsAndDate(Mockito.eq(Collections.singletonList(doctorModel())), Mockito.eq(dateModel())))
                .thenReturn(Collections.emptyList());
        Mockito.when(appointmentDaoMock.create(Mockito.eq(appointmentModel())))
                .thenReturn(appointmentModel());
        Mockito.doNothing().when(emailServiceMock).sendNewAppointmentNotificationEmail(appointmentModel());

        Appointment appointment = appointmentService.create(appointmentModel(), userModel());

        assertEquals(appointmentModel(), appointment);
    }
}
