package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.AppointmentDao;
import ar.edu.itba.paw.models.*;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:sql/schema.sql")
@ContextConfiguration(classes = TestConfig.class)
public class AppointmentDaoImplTest {
    private static final int STARTING_ID = 1;
    private static final String FIRST_NAME = "Nombre";
    private static final String SURNAME = "Apellido";
    private static final String EMAIL = "napellido@test.com";
    private static final String PHONE = "1123456789";
    private static final String PASSWORD = "pass1234";
    private static final String TOKEN = "123";
    private static final String FIRST_NAME_2 = "Roberto";
    private static final String SURNAME_2 = "Rodriguez";
    private static final String EMAIL_2 = "napellido2@test2.com";
    private static final String PHONE_2 = "(011) 1123456789";
    private static final String PASSWORD_2 = "password1234";
    private static final String MIME_TYPE = "image/svg+xml";
    private static final String PICTURE = "defaultProfilePic.svg";
    private static final Resource IMG = new ClassPathResource("img/" + PICTURE);
    private static final byte[] IMG_DATA = getImgData(IMG);
    private static final long IMG_SIZE = getImgSize(IMG);
    private static final String OFFICE = "Hospital Nacional";
    private static final String OFFICE_2 = "Sanatorio Provincial";
    private static final String STREET = "Av 9 de Julio 123";
    private static final String LOCALITY = "Capital Federal";
    private static final String PROVINCE = "Buenos Aires";
    private static final String COUNTRY = "Argentina";
    private static final String COUNTRY_ID = "AR";
    private static final String OFFICE_PHONE = "1234567890";
    private static final String OFFICE_EMAIL = "test@test.com";
    private static final String URL = "www.hnacional.com";
    private static final int REGISTRATION_NUMBER = 123;
    private static final AppointmentStatus STATUS = AppointmentStatus.PENDING;
    private static final int YEAR = LocalDateTime.now().getYear() + 1;
    private static final int MONTH = 5;
    private static final int DAY = 25;
    private static final int HOUR = 11;
    private static final int MINUTE = 0;
    private static final int HOUR_2 = 15;
    private static final int MINUTE_2 = 0;
    private static final String FROM_DATE = YEAR + "-05-25 11:00:00.000000";
    private static final String FROM_DATE_2 = YEAR + "-05-25 15:00:00.000000";
    private static final String FROM_DATE_TO_NOTIFY = (LocalDateTime.now().getYear() + 1) + "-05-25 11:00:00.000000";
    private static final String FROM_DATE_2_TO_NOTIFY = (LocalDateTime.now().getYear() + 1) + "-05-25 15:00:00.000000";
    private static final String MOTIVE = "motive";
    private static final String MESSAGE = "message";
    private static final boolean WAS_NOTIFICATION_EMAIL_SENT = true;
    private static final Locale LOCALE = new Locale("es");

    private static final int USER_ID_1 = STARTING_ID;
    private static final int USER_ID_2 = STARTING_ID + 1;
    private static final int PICTURE_ID_1 = STARTING_ID;
    private static final int PICTURE_ID_2 = STARTING_ID + 1;
    private static final int LOCALITY_ID = STARTING_ID;
    private static final int PROVINCE_ID = STARTING_ID;
    private static final int OFFICE_ID_1 = STARTING_ID;
    private static final int OFFICE_ID_2 = STARTING_ID + 1;
    private static final int PATIENT_ID_1 = STARTING_ID;
    private static final int PATIENT_ID_2 = STARTING_ID + 1;
    private static final int DOCTOR_ID_1 = STARTING_ID;
    private static final int DOCTOR_ID_2 = STARTING_ID + 1;
    private static final int APPOINTMENT_ID_1 = STARTING_ID;
    private static final int APPOINTMENT_ID_2 = STARTING_ID + 1;

    private static final int PROFILE_ID_1 = PICTURE_ID_1;
    private static final int PROFILE_ID_2 = PICTURE_ID_2;

    private static final String OFFICES_TABLE = "office";
    private static final String LOCALITIES_TABLE = "system_locality";
    private static final String PROVINCES_TABLE = "system_province";
    private static final String COUNTRIES_TABLE = "system_country";
    private static final String USERS_TABLE = "users";
    private static final String PICTURES_TABLE = "picture";
    private static final String PATIENTS_TABLE = "patient";
    private static final String DOCTORS_TABLE = "doctor";
    private static final String APPOINTMENTS_TABLE = "appointment";

    @Autowired
    private AppointmentDao appointmentDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert userJdbcInsert;
    private SimpleJdbcInsert pictureJdbcInsert;
    private SimpleJdbcInsert officeJdbcInsert;
    private SimpleJdbcInsert localityJdbcInsert;
    private SimpleJdbcInsert provinceJdbcInsert;
    private SimpleJdbcInsert countryJdbcInsert;
    private SimpleJdbcInsert patientJdbcInsert;
    private SimpleJdbcInsert doctorJdbcInsert;
    private SimpleJdbcInsert appointmentJdbcInsert;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(this.ds);
        this.userJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(USERS_TABLE)
                .usingGeneratedKeyColumns("users_id");
        this.pictureJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(PICTURES_TABLE)
                .usingGeneratedKeyColumns("picture_id");
        this.officeJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(OFFICES_TABLE)
                .usingGeneratedKeyColumns("office_id");
        this.localityJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(LOCALITIES_TABLE)
                .usingGeneratedKeyColumns("locality_id");
        this.provinceJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(PROVINCES_TABLE)
                .usingGeneratedKeyColumns("province_id");
        this.countryJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(COUNTRIES_TABLE);
        this.patientJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(PATIENTS_TABLE)
                .usingGeneratedKeyColumns("patient_id");
        this.doctorJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(DOCTORS_TABLE)
                .usingGeneratedKeyColumns("doctor_id");
        this.appointmentJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(APPOINTMENTS_TABLE)
                .usingGeneratedKeyColumns("appointment_id");
        cleanAllTables();
    }

    /* ---------------------- FUNCIONES AUXILIARES ---------------------------------------------------------------- */

    private static byte[] getImgData(Resource img){
        try {
            return Files.readAllBytes(img.getFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static long getImgSize(Resource img){
        try {
            return img.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void cleanAllTables() {
        this.jdbcTemplate.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
    }

    /**
     * Devuelve un User con
     * firstName = IMG_DATA
     * surname = SURNAME
     * password = PASSWORD
     * email = EMAIL
     * phone = PHONE
     * profileID = PROFILE_ID
     * token = TOKEN
     * tokenCreatedDate = null
     * id = STARTING_ID
     **/
    private User userModel() {
        User u = new User();
        u.setEmail(EMAIL);
        u.setPassword(PASSWORD);
        u.setFirstName(FIRST_NAME);
        u.setSurname(SURNAME);
        u.setPhone(PHONE);
        u.setProfilePicture(pictureModel());
        u.setVerificationToken(null);
        u.setVerified(true);
        u.setId(USER_ID_1);
        return u;
    }

    /**
     * Devuelve un User con
     * firstName = FIRST_NAME_2
     * surname = SURNAME_2
     * password = PASSWORD_2
     * email = EMAIL_2
     * phone = PHONE_2
     * profileID = PROFILE_ID_2
     * token = TOKEN
     * tokenCreatedDate = null
     * id = STARTING_ID + 1
     **/
    private User userModel2() {
        User u = new User();
        u.setEmail(EMAIL_2);
        u.setPassword(PASSWORD_2);
        u.setFirstName(FIRST_NAME_2);
        u.setSurname(SURNAME_2);
        u.setPhone(PHONE_2);
        u.setProfilePicture(pictureModel());
//        u.setVerificationToken(null);
//        u.setVerificationTokenCreatedDate(null);
        u.setVerified(true);
        u.setId(USER_ID_2);
        return u;
    }

    /** Devuelve una locality con id=STARTING_ID, name=PROVINCE y como country el devuelto en countryModel() **/
    private Locality localityModel(){
        Locality l = new Locality();
        l.setName(LOCALITY);
        l.setProvince(provinceModel());
        l.setId(LOCALITY_ID);
        return l;
    }

    /** Devuelve una province con id=STARTING_ID, name=PROVINCE y como country el devuelto en countryModel() **/
    private Province provinceModel(){
        Province p = new Province();
        p.setId(PROVINCE_ID);
        p.setCountry(countryModel());
        p.setName(PROVINCE);
        return p;
    }

    /** Devuelve un country con id=COUNTRY_ID y name=COUNTRY **/
    private Country countryModel(){
        Country c = new Country();
        c.setName(COUNTRY);
        c.setId(COUNTRY_ID);
        return c;
    }

    /** Devuelve un office con
     * id=STARTING_ID
     * name=OFFICE
     * email=EMAIL
     * phone=PHONE
     * street=STREET
     * url=URL
     * y como locality toma el devuelto por localityModel()
     **/
    private Office officeModel(){
        Office o = new Office();
        o.setId(OFFICE_ID_1);
        o.setName(OFFICE);
        o.setEmail(OFFICE_EMAIL);
        o.setPhone(OFFICE_PHONE);
        o.setLocality(localityModel());
        o.setStreet(STREET);
        o.setUrl(URL);
        return o;
    }

    /** Devuelve un office con
     * id=STARTING_ID + 1
     * name=OFFICE_2
     * email=EMAIL
     * phone=PHONE
     * street=STREET
     * url=URL
     * y como locality toma el devuelto por localityModel()
     **/
    private Office officeModel2(){
        Office o = new Office();
        o.setId(OFFICE_ID_2);
        o.setName(OFFICE_2);
        o.setEmail(OFFICE_EMAIL);
        o.setPhone(OFFICE_PHONE);
        o.setLocality(localityModel());
        o.setStreet(STREET);
        o.setUrl(URL);
        return o;
    }

    private Patient patientModel(){
        Patient p = new Patient();
        p.setId(PATIENT_ID_1);
        p.setOffice(officeModel());
        p.setUser(userModel());
        return p;
    }

    private Patient patientModel2(){
        Patient p = new Patient();
        p.setId(PATIENT_ID_2);
        p.setOffice(officeModel2());
        p.setUser(userModel2());
        return p;
    }

    private Picture pictureModel(){
        Picture p = new Picture();
        p.setId(PICTURE_ID_1);
        p.setMimeType(MIME_TYPE);
        p.setSize(IMG_SIZE);
        p.setData(IMG_DATA);
        p.setName(PICTURE);
        return p;
    }

    /** Inserta en la db la imagen con
     * data = IMG_DATA
     * mimeType = MIME_TYPE
     * name = NAME
     * size = IMG_SIZE
     **/
    private void insertPicture() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", IMG_DATA);
        map.put("mime_type", MIME_TYPE);
        map.put("size", IMG_SIZE);
        map.put("name", PICTURE);
        pictureJdbcInsert.execute(map);
    }

    /** Inserta en la db la imagen con
     * data = IMG_DATA
     * mimeType = MIME_TYPE
     * name = NAME
     * size = IMG_SIZE
     **/
    private void insertAnotherPicture() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", IMG_DATA);
        map.put("mime_type", MIME_TYPE);
        map.put("size", IMG_SIZE);
        map.put("name", PICTURE);
        pictureJdbcInsert.execute(map);
    }

    /**
     * Inserta en la db el user con
     * firstName = FIRST_NAME
     * surname = SURNAME
     * password = PASSWORD
     * email = EMAIL
     * phone = PHONE
     * profileID = PROFILE_ID
     * token = TOKEN
     * tokenCreatedDate = null
     **/
    private void insertUser() {
        insertPicture();
        Map<String, Object> map = new HashMap<>();
        map.put("first_name", FIRST_NAME);
        map.put("surname", SURNAME);
        map.put("password", PASSWORD);
        map.put("email", EMAIL);
        map.put("phone", PHONE);
        map.put("profile_id", PROFILE_ID_1);
        map.put("verification_token_id", null);
        map.put("refresh_token_id", null);
        userJdbcInsert.execute(map);
    }

    /**
     * Inserta en la db el user con
     * firstName = FIRST_NAME_2
     * surname = SURNAME_2
     * password = PASSWORD_2
     * email = EMAIL_2
     * phone = PHONE_2
     * profileID = PROFILE_ID_2
     * token = TOKEN
     * tokenCreatedDate = null
     **/
    private void insertAnotherUser() {
        insertAnotherPicture();
        Map<String, Object> map = new HashMap<>();
        map.put("first_name", FIRST_NAME_2);
        map.put("surname", SURNAME_2);
        map.put("password", PASSWORD_2);
        map.put("email", EMAIL_2);
        map.put("phone", PHONE_2);
        map.put("profile_id", PROFILE_ID_2);
        map.put("verification_token_id", null);
        map.put("refresh_token_id", null);
        userJdbcInsert.execute(map);
    }

    /** Inserta en la db el pais con id=COUNTRY_ID y name=COUNTRY **/
    private void insertCountry(){
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", COUNTRY_ID);
        map.put("name", COUNTRY);
        countryJdbcInsert.execute(map);
    }

    /** Inserta en la db la provincia con country_id=COUNTRY_ID y name=PROVINCE **/
    private void insertProvince(){
        insertCountry();
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", COUNTRY_ID);
        map.put("name", PROVINCE);
        provinceJdbcInsert.execute(map);
    }

    /** Inserta en la db la localidad con country_id=STARTING_ID y name=LOCALITY **/
    private void insertLocality(){
        insertProvince();
        Map<String, Object> map = new HashMap<>();
        map.put("province_id", PROVINCE_ID);
        map.put("name", LOCALITY);
        localityJdbcInsert.execute(map);
    }

    /** Inserta en la db la oficina con
     * name=OFFICE
     * email=EMAIL
     * phone=PHONE
     * street=STREET
     * url=URL
     * localityId=STARTING_ID
     **/
    private void insertOffice(){
        insertLocality();
        Map<String, Object> officeMap = new HashMap<>();
        officeMap.put("name", OFFICE);
        officeMap.put("email", OFFICE_EMAIL);
        officeMap.put("phone", OFFICE_PHONE);
        officeMap.put("locality_id", LOCALITY_ID);
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);
    }

    /** Inserta en la db la oficina con
     * name=OFFICE_2
     * email=EMAIL
     * phone=PHONE
     * street=STREET
     * url=URL
     * localityId=STARTING_ID
     **/
    private void insertAnotherOffice(){
        Map<String, Object> officeMap = new HashMap<>();
        officeMap.put("name", OFFICE_2);
        officeMap.put("email", OFFICE_EMAIL);
        officeMap.put("phone", OFFICE_PHONE);
        officeMap.put("locality_id", LOCALITY_ID);
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);
    }

    private void insertPatient() {
        insertUser();
        insertOffice();
        Map<String, Object> patientMap = new HashMap<>();
        patientMap.put("user_id", USER_ID_1);
        patientMap.put("office_id", OFFICE_ID_1);
        patientJdbcInsert.execute(patientMap);
    }

    private void insertAnotherPatient() {
        insertAnotherOffice();
        insertAnotherUser();
        Map<String, Object> patientMap = new HashMap<>();
        patientMap.put("user_id", USER_ID_2);
        patientMap.put("office_id", OFFICE_ID_2);
        patientJdbcInsert.execute(patientMap);
    }

    /** Inserta en la db el doctor con
     * firstName=FIRST_NAME
     * surname=SURNAME
     * registrationNumber=REGISTRATION_NUMBER
     * email=EMAIL
     * phone=PHONE
     * user_id=STARTING_ID
     * office_id=STARTING_ID
     **/
    private void insertDoctor(){
        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL); // Identity de HSQLDB empieza en 0
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", USER_ID_1);
        doctorMap.put("office_id", OFFICE_ID_1);
        doctorJdbcInsert.execute(doctorMap);
    }

    private void insertAnotherDoctor(){
        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME_2);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME_2);
        doctorMap.put("email", EMAIL); // Identity de HSQLDB empieza en 0
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", USER_ID_2);
        doctorMap.put("office_id", OFFICE_ID_2);
        doctorJdbcInsert.execute(doctorMap);
    }

    private Doctor doctorModel(){
        Doctor s = new Doctor();
        s.setRegistrationNumber(REGISTRATION_NUMBER);
        s.setEmail(EMAIL);
        s.setPhone(PHONE);
        s.setId(DOCTOR_ID_1);
        s.setUser(userModel());
        s.setOffice(officeModel());
        return s;
    }

    private Doctor doctorModel2(){
        Doctor s = new Doctor();
        s.setRegistrationNumber(REGISTRATION_NUMBER);
        s.setEmail(EMAIL);
        s.setPhone(PHONE);
        s.setId(DOCTOR_ID_2);
        s.setUser(userModel2());
        s.setOffice(officeModel2());
        return s;
    }

    private void insertAppointment() {
        insertPatient();
        insertDoctor();
        Map<String, Object> appointmentMap = new HashMap<>();
        appointmentMap.put("status", STATUS.name());
        appointmentMap.put("patient_id", PATIENT_ID_1);
        appointmentMap.put("doctor_id", DOCTOR_ID_1);
        appointmentMap.put("from_date", FROM_DATE);
        appointmentMap.put("motive", MOTIVE);
        appointmentMap.put("message", MESSAGE);
        appointmentMap.put("locale", LOCALE.toString());
        appointmentMap.put("was_notification_email_sent", WAS_NOTIFICATION_EMAIL_SENT);
        appointmentJdbcInsert.execute(appointmentMap);
    }

    private void insertAnotherAppointment(){
        insertAnotherPatient();
        insertAnotherDoctor();
        Map<String, Object> appointmentMap = new HashMap<>();
        appointmentMap.put("status", STATUS.name());
        appointmentMap.put("patient_id", PATIENT_ID_2);
        appointmentMap.put("doctor_id", DOCTOR_ID_2);
        appointmentMap.put("from_date", FROM_DATE_2);
        appointmentMap.put("motive", MOTIVE);
        appointmentMap.put("message", MESSAGE);
        appointmentMap.put("locale", LOCALE.toString());
        appointmentMap.put("was_notification_email_sent", WAS_NOTIFICATION_EMAIL_SENT);
        appointmentJdbcInsert.execute(appointmentMap);
    }

    private void insertAppointmentToNotify(){
        insertAnotherPatient();
        insertAnotherDoctor();
        Map<String, Object> appointmentMap = new HashMap<>();
        appointmentMap.put("status", STATUS.name());
        appointmentMap.put("patient_id", PATIENT_ID_2);
        appointmentMap.put("doctor_id", DOCTOR_ID_2);
        appointmentMap.put("from_date", FROM_DATE_TO_NOTIFY);
        appointmentMap.put("motive", MOTIVE);
        appointmentMap.put("message", MESSAGE);
        appointmentMap.put("locale", LOCALE.toString());
        appointmentMap.put("was_notification_email_sent", false);
        appointmentJdbcInsert.execute(appointmentMap);
    }

    private void insertAnotherAppointmentToNotify(){
        insertAnotherPatient();
        insertAnotherDoctor();
        Map<String, Object> appointmentMap = new HashMap<>();
        appointmentMap.put("status", STATUS.name());
        appointmentMap.put("patient_id", PATIENT_ID_2);
        appointmentMap.put("doctor_id", DOCTOR_ID_2);
        appointmentMap.put("from_date", FROM_DATE_2_TO_NOTIFY);
        appointmentMap.put("motive", MOTIVE);
        appointmentMap.put("message", MESSAGE);
        appointmentMap.put("locale", LOCALE.toString());
        appointmentMap.put("was_notification_email_sent", false);
        appointmentJdbcInsert.execute(appointmentMap);
    }

    private Appointment appointmentModel(){
        Appointment appointment = new Appointment();
        appointment.setAppointmentStatus(STATUS);
        appointment.setDoctor(doctorModel());
        appointment.setPatient(patientModel());
        appointment.setFromDate(new LocalDateTime(YEAR,MONTH,DAY,HOUR,MINUTE));
        appointment.setMessage(MESSAGE);
        appointment.setMotive(MOTIVE);
        appointment.setId(APPOINTMENT_ID_1);
        appointment.setWasNotificationEmailSent(WAS_NOTIFICATION_EMAIL_SENT);
        appointment.setLocale(LOCALE);
        return appointment;
    }

    private Appointment appointmentModel2(){
        Appointment appointment = new Appointment();
        appointment.setAppointmentStatus(STATUS);
        appointment.setDoctor(doctorModel2());
        appointment.setPatient(patientModel2());
        appointment.setFromDate(new LocalDateTime(YEAR,MONTH,DAY,HOUR_2,MINUTE_2));
        appointment.setMessage(MESSAGE);
        appointment.setMotive(MOTIVE);
        appointment.setId(APPOINTMENT_ID_2);
        appointment.setWasNotificationEmailSent(WAS_NOTIFICATION_EMAIL_SENT);
        appointment.setLocale(LOCALE);
        return appointment;
    }

    /* --------------------- MÉTODO: doctorSpecialtyDao.create(DoctorSpecialty) -------------------------------------------- */

    @Test
    public void testCreateAppointmentSuccessfully() {
        // 1. Precondiciones

        Appointment a = appointmentModel();
        insertPatient();
        insertDoctor();
        a.setId(null);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(STATUS, appointment.getAppointmentStatus());
        assertEquals(MESSAGE, appointment.getMessage());
        assertEquals(MOTIVE, appointment.getMotive());
        assertEquals(doctorModel(), appointment.getDoctor());
        assertEquals(patientModel(), appointment.getPatient());
        assertEquals(new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE), appointment.getFromDate());
    }

    @Test
    public void testCreateAnotherAppointmentSuccessfully() {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();
        Appointment a = appointmentModel();
        a.setId(null);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(STATUS, appointment.getAppointmentStatus());
        assertEquals(MESSAGE, appointment.getMessage());
        assertEquals(MOTIVE, appointment.getMotive());
        assertEquals(doctorModel(), appointment.getDoctor());
        assertEquals(patientModel(), appointment.getPatient());
        assertEquals(new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE), appointment.getFromDate());
    }

    @Test
    public void testCreateAppointmentNullFail()
    {
        // 1. Precondiciones
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire IllegalArgumentException
    }

    @Test
    public void testCreateAppointmentEmptyAppointmentFail()
    {
        // 1. Precondiciones

        Appointment a = new Appointment();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException.
    }

    @Test
    public void testCreateAppointmentEmptyStatusFail()
    {
        // 1. Precondiciones

        Appointment a = appointmentModel();
         a.setAppointmentStatus(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateAppointmentEmptyDoctorFail()
    {
        // 1. Precondiciones

        Appointment a = appointmentModel();
        a.setDoctor(null);
        expectedException.expect(PersistenceException.class);
        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateAppointmentEmptyPatientFail()
    {
        // 1. Precondiciones

        Appointment a = appointmentModel();
        a.setPatient(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateAppointmentEmptyMotiveFail()
    {
        // 1. Precondiciones

        Appointment a = appointmentModel();
        a.setMotive(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateAppointmentEmptyDateFail()
    {
        // 1. Precondiciones

        Appointment a = appointmentModel();
        a.setFromDate(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Appointment appointment = this.appointmentDao.create(a);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    /* --------------------- MÉTODO: appointmentDao.findById(String) -------------------------------------------- */

    @Test
    public void testFindAppointmentById()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        Optional<Appointment> appointment = this.appointmentDao.findById(APPOINTMENT_ID_1);

        // 3. Postcondiciones
        assertTrue(appointment.isPresent());
        assertEquals(appointmentModel(), appointment.get());
    }

    @Test
    public void testFindAppointmentByIdDoesntExist()
    {
        // 1. Precondiciones

        insertAppointment();

        // 2. Ejercitar
        Optional<Appointment> appointment = this.appointmentDao.findById(APPOINTMENT_ID_1 + 1);

        // 3. Postcondiciones
        assertFalse(appointment.isPresent());
    }

    @Test
    public void testFindAppointmentByIdNull()
    {
        // 1. Precondiciones

        insertAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Appointment> appointment = this.appointmentDao.findById(null);

        // 3. Postcondiciones
        assertFalse(appointment.isPresent());
    }

    /* --------------------- MÉTODO: appointmentDao.findByIds(Collection<String>) -------------------------------------------- */

    @Test
    public void testFindAppointmentByIds()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        Collection<Appointment> appointments = this.appointmentDao.findByIds(Arrays.asList(APPOINTMENT_ID_1, APPOINTMENT_ID_2));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(2, appointments.size());
        for (Appointment a : appointments){
            assertTrue( a.getId().equals(APPOINTMENT_ID_1) ||  a.getId().equals(APPOINTMENT_ID_2));
        }
    }

    @Test
    public void testFindAppointmentByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertAppointment();

        // 2. Ejercitar
        Collection<Appointment> appointments = this.appointmentDao.findByIds(Arrays.asList(APPOINTMENT_ID_1, APPOINTMENT_ID_2));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment a : appointments){
            assertEquals(appointmentModel(), a);
        }
    }

    @Test
    public void testFindAppointmentByIdsDontExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Appointment> appointments = this.appointmentDao.findByIds(Arrays.asList(APPOINTMENT_ID_1, APPOINTMENT_ID_2));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    /* --------------------- MÉTODO: appointmentDao.list() -------------------------------------------- */

    @Test
    public void testAppointmentList()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        Collection<Appointment> appointments = this.appointmentDao.list();

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(2, appointments.size());
    }

    @Test
    public void testAppointmentEmptyList()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Appointment> appointments = this.appointmentDao.list();

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    /* --------------------- MÉTODO: appointmentDao.update(Appointment) -------------------------------------------- */

    @Test
    public void testAppointmentUpdate()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        Appointment a = appointmentModel();
        a.setFromDate(new LocalDateTime(YEAR, MONTH, DAY, HOUR_2, MINUTE_2, 0));

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(2,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "from_date = '"+ FROM_DATE_2 +"'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "from_date = '"+ FROM_DATE +"'"));
    }

    @Test
    public void testAppointmentUpdateNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.appointmentDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
    }

    @Test
    public void testAppointmentUpdateNotExistentAppointment()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();
        Appointment a = appointmentModel();
        a.setId(APPOINTMENT_ID_2);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
    }

    @Test
    public void testAppointmentUpdateAppointmentWithNullAppointmentStatus()
    {
        // 1. Precondiciones

        insertAppointment();
        Appointment a = appointmentModel();
        a.setAppointmentStatus(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(appointmentModel(), a);
    }

    @Test
    public void testAppointmentUpdateAppointmentWithNullId()
    {
        // 1. Precondiciones

        insertAppointment();
        Appointment a = appointmentModel();
        a.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
    }

    @Test
    public void testAppointmentUpdateAppointmentWithNullDoctor()
    {
        // 1. Precondiciones

        insertAppointment();
        Appointment a = appointmentModel();
        a.setDoctor(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(appointmentModel(), a);
    }

    @Test
    public void testAppointmentUpdateAppointmentWithNullPatient()
    {
        // 1. Precondiciones

        insertAppointment();
        Appointment a = appointmentModel();
        a.setPatient(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(appointmentModel(), a);
    }

    @Test
    public void testAppointmentUpdateAppointmentWithNullMotive()
    {
        // 1. Precondiciones

        insertAppointment();
        Appointment a = appointmentModel();
        a.setMotive(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(appointmentModel(), a);
    }

    @Test
    public void testAppointmentUpdateAppointmentWithNullDate()
    {
        // 1. Precondiciones

        insertAppointment();
        Appointment a = appointmentModel();
        a.setFromDate(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.appointmentDao.update(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(appointmentModel(), a);
    }

    /* --------------------- MÉTODO: appointmentDao.remove(String id) -------------------------------------------- */

    @Test
    public void testAppointmentRemoveById()
    {
        // 1. Precondiciones

        insertAppointment();

        // 2. Ejercitar
        this.appointmentDao.remove(APPOINTMENT_ID_1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'CANCELLED'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'PENDING'"));
    }

    @Test
    public void testAppointmentRemoveByIdNotExistent()
    {
        // 1. Precondiciones
        insertAppointment();

        // 2. Ejercitar
        this.appointmentDao.remove(APPOINTMENT_ID_2);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'CANCELLED'"));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'PENDING'"));
    }

    @Test
    public void testAppointmentRemoveByNullId()
    {
        // 1. Precondiciones

        insertAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.appointmentDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
    }
    /* --------------------- MÉTODO: appointmentDao.remove(Appointment) -------------------------------------------- */

    @Test
    public void testAppointmentRemoveByModel()
    {
        // 1. Precondiciones

        insertAppointment();
        Appointment a = appointmentModel();
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "appointment_id = 1"));


        // 2. Ejercitar
        this.appointmentDao.remove(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'CANCELLED'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'PENDING'"));
    }

    @Test
    public void testAppointmentRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();
        Appointment a = appointmentModel();
        a.setId(APPOINTMENT_ID_2);

        // 2. Ejercitar
        this.appointmentDao.remove(a);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'CANCELLED'"));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'PENDING'"));
    }

    @Test
    public void testAppointmentRemoveByNullModel()
    {
        // 1. Precondiciones

        insertAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.appointmentDao.remove((Appointment) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
    }

    /* --------------------- MÉTODO: appointmentDao.count() -------------------------------------------- */

    @Test
    public void testAppointmentCount()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.appointmentDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testAppointmentCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.appointmentDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }

    /* --------------------- MÉTODO: appointmentDao.find(Doctor) -------------------------------------------- */

    @Test
    public void testAppointmentFindDoctor()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.find(doctorModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(doctorModel(), appointment.getDoctor());
        }
    }

    @Test
    public void testAppointmentFindDoctorNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.find((Doctor) null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindDoctorDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.find(doctorModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    /* --------------------- MÉTODO: appointmentDao.find(Patient) -------------------------------------------- */

    @Test
    public void testAppointmentFindPatient()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.find(patientModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(patientModel(), appointment.getPatient());
        }
    }

    @Test
    public void testAppointmentFindPatientNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.find((Patient) null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPatientDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.find(patientModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    /* --------------------- MÉTODO: appointmentDao.findByDate(Patient, DateTime) -------------------------------------------- */

    @Test
    public void testAppointmentFindPatientDate()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDate(patientModel(), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(patientModel(), appointment.getPatient());
        }
    }

    @Test
    public void testAppointmentFindPatientDateDateNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDate(patientModel(), null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPatientDatePatientNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDate(null, new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPatientDateBothNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDate(null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPatientDatePatientDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDate(patientModel(), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindPatientDateDateDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDate(patientModel(), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    /* --------------------- MÉTODO: appointmentDao.findByPatients(List<Patient>) -------------------------------------------- */

    @Test
    public void testAppointmentFindByPatients()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatients(Collections.singletonList(patientModel()));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(patientModel(), appointment.getPatient());
        }
    }

    @Test
    public void testAppointmentFindByPatientsNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatients(null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByPatientsDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatients(Collections.singletonList(patientModel()));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    /* --------------------- MÉTODO: appointmentDao.findByPatientsAndDate(Collection<Patient>, DateTime) -------------------------------------------- */

    @Test
    public void testAppointmentFindByPatientAndDate()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsAndDate(
                Collections.singletonList(patientModel()),
                new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE),
                new LocalDateTime(YEAR, MONTH, DAY, HOUR + 1, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(patientModel(), appointment.getPatient());
        }
    }

    @Test
    public void testAppointmentFindByPatientAndDateDateNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsAndDate(Collections.singletonList(patientModel()), null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByPatientAndDatePatientNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsAndDate(null, new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE), LocalDateTime.now());

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByPatientAndDateBothNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsAndDate(null, null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByPatientAndDatePatientDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsAndDate(Collections.singletonList(patientModel()), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE), LocalDateTime.now());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindByPatientAndDateDateDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsAndDate(Collections.singletonList(patientModel()), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE), LocalDateTime.now().plusYears(1));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }
    
    /* --------------------- MÉTODO: appointmentDao.findByPatientsFromDate(List<Patient>, DateTime) -------------------------------------------- */

    @Test
    public void testAppointmentFindByPatientFromDate()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsFromDate(Collections.singletonList(patientModel()), new LocalDateTime(YEAR, MONTH, DAY, 0, 0));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(patientModel(), appointment.getPatient());
        }
    }

    @Test
    public void testAppointmentFindByPatientFromDateDateNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsFromDate(Collections.singletonList(patientModel()), null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByPatientFromDatePatientNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsFromDate(null, new LocalDateTime(YEAR, MONTH, DAY, 0, 0));

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByPatientFromDateBothNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsFromDate(null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByPatientFromDatePatientDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsFromDate(Collections.singletonList(patientModel()), new LocalDateTime(YEAR, MONTH, DAY, 0, 0));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindByPatientFromDateDateDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByPatientsFromDate(Collections.singletonList(patientModel()), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }
    
    /* --------------------- MÉTODO: appointmentDao.findByDoctors(List<Doctors>) -------------------------------------------- */

    @Test
    public void testAppointmentFindByDoctors()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctors(Collections.singletonList(doctorModel()));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(doctorModel(), appointment.getDoctor());
        }
    }

    @Test
    public void testAppointmentFindByDoctorsNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctors(null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorsDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctors(Collections.singletonList(doctorModel()));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }
    
    /* --------------------- MÉTODO: appointmentDao.findByDoctorsAndDate(Collection<Doctors>, DateTime) -------------------------------------------- */

    @Test
    public void testAppointmentFindByDoctorAndDate()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(doctorModel(), appointment.getDoctor());
        }
    }

    @Test
    public void testAppointmentFindByDoctorAndDateDateNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorAndDateDoctorNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(null, new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorAndDateBothNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorAndDateDoctorDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindByDoctorAndDateDateDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }
    
    /* --------------------- MÉTODO: appointmentDao.findByDoctorsAndDate(List<Patient>, DateTime from, DateTime to) -------------------------------------------- */

    @Test
    public void testAppointmentFindByDoctorsAndDateFromTo()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), new LocalDateTime(YEAR, MONTH, DAY, 0, 0), new LocalDateTime(YEAR, MONTH, DAY, 23, 59));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(doctorModel(), appointment.getDoctor());
        }
    }

    @Test
    public void testAppointmentFindByDoctorsAndDateFromToFromDateNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), null, new LocalDateTime(YEAR, MONTH, DAY, 23, 59));

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorsAndDateFromToToDateNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), new LocalDateTime(YEAR, MONTH, DAY, 0, 0),null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorAndDateFromToDoctorNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(null, new LocalDateTime(YEAR, MONTH, DAY, 0, 0), new LocalDateTime(YEAR, MONTH, DAY, 23, 59));

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorAndDateFromToAllNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(null, null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindByDoctorAndDateFromToDoctorDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), new LocalDateTime(YEAR, MONTH, DAY, 0, 0), new LocalDateTime(YEAR, MONTH, DAY, 23, 59));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindByDoctorAndDateFromToDateDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findByDoctorsAndDate(Collections.singletonList(doctorModel()), new LocalDateTime(YEAR, MONTH, DAY, 0, 0), new LocalDateTime(YEAR, MONTH, DAY, 23, 59));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }
    
    /* --------------------- MÉTODO: appointmentDao.findPending(Doctor) -------------------------------------------- */

    @Test
    public void testAppointmentFindPendingDoctor()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(doctorModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(doctorModel(), appointment.getDoctor());
            assertEquals(STATUS, appointment.getAppointmentStatus());
        }
    }

    @Test
    public void testAppointmentFindPendingDoctorNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending((Doctor) null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPendingDoctorDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(doctorModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }


    /* --------------------- MÉTODO: appointmentDao.findPending(Patient) -------------------------------------------- */

    @Test
    public void testAppointmentFindPendingPatient()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(patientModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(patientModel(), appointment.getPatient());
            assertEquals(STATUS, appointment.getAppointmentStatus());
        }
    }

    @Test
    public void testAppointmentFindPendingPatientNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending((Patient) null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPendingPatientDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(patientModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }
    
    /* --------------------- MÉTODO: appointmentDao.findPending(Patient, Doctor) -------------------------------------------- */

    @Test
    public void testAppointmentFindPendingPatientDoctor()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(patientModel(), doctorModel());

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        for (Appointment appointment: appointments){
            assertEquals(patientModel(), appointment.getPatient());
            assertEquals(doctorModel(), appointment.getDoctor());
            assertEquals(STATUS, appointment.getAppointmentStatus());
        }
    }

    @Test
    public void testAppointmentFindPendingPatientDoctorPatientNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(null, doctorModel());

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPendingPatientDoctorDoctorNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(patientModel(), null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPendingPatientDoctorBothNull()
    {
        // 1. Precondiciones

        insertAppointment();
        insertAnotherAppointment();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testAppointmentFindPendingPatientDoctorPatientDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();
        Patient p = patientModel();
        p.setId(PATIENT_ID_2);
        Doctor s = doctorModel();
        s.setUser(userModel2());
        s.setOffice(officeModel2());

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(p, s);

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindPendingPatientDoctorDoctorDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();
        insertDoctor();
        insertAnotherAppointment();
        Patient p = patientModel2();
        Doctor s = doctorModel();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findPending(p, s);

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindAllAppointmentsInIntervalToNotify(){
        // 1. Precondiciones
        insertAppointment();
        insertAnotherAppointment();
        insertAppointmentToNotify();
        insertAnotherAppointmentToNotify();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findAllAppointmentsToNotifyUpTo(
                new LocalDateTime(LocalDateTime.now().getYear() + 1, MONTH, DAY, HOUR_2, MINUTE_2));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertEquals(2, appointments.size());
        assertTrue((appointments.get(0).getId() == 3 && appointments.get(1).getId() == 4) ||
                (appointments.get(0).getId() == 4 && appointments.get(1).getId() == 3));
    }

    @Test
    public void testAppointmentFindAllAppointmentsInIntervalToNotifyEmpty(){
        // 1. Precondiciones
        insertAppointment();
        insertAnotherAppointment();
        insertAppointmentToNotify();

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findAllAppointmentsToNotifyUpTo(
                new LocalDateTime(YEAR - 1, MONTH, DAY, HOUR_2, MINUTE_2));

        // 3. Postcondiciones
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testAppointmentFindAllAppointmentsInIntervalToNotifyNullTo(){
        // 1. Precondiciones
        insertAppointment();
        insertAnotherAppointment();
        insertAppointmentToNotify();
        insertAnotherAppointmentToNotify();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Appointment> appointments = this.appointmentDao.findAllAppointmentsToNotifyUpTo(
                null);

        // 3. Postcondiciones
        // IllegalArgumentException
    }

    /* --------------------- MÉTODO: appointmentDao.cancelAppointments(List<Appointments>) -------------------------------------------- */

    @Test
    public void testAppointmentCancelAppointments()
    {
        // 1. Precondiciones
        insertAppointment();
        insertAnotherAppointment();
        insertAppointmentToNotify();
        insertAnotherAppointmentToNotify();
        List<Appointment> appointments = new LinkedList<>();
        appointments.add(appointmentModel());
        appointments.add(appointmentModel2());

        // 2. Ejercitar
        this.appointmentDao.cancelAppointments(appointments);

        // 3. Postcondiciones
        assertEquals(4,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(2,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'CANCELLED'"));
        assertEquals(2,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'PENDING'"));
    }

    @Test
    public void testAppointmentCancelAppointmentsNotExistent()
    {
        // 1. Precondiciones
        insertAppointment();
        insertAnotherAppointment();
        List<Appointment> appointments = new LinkedList<>();
        Appointment appointment = appointmentModel();
        appointment.setId(3);
        Appointment appointment2 = appointmentModel2();
        appointment2.setId(4);
        appointments.add(appointment);
        appointments.add(appointment2);

        // 2. Ejercitar
        this.appointmentDao.cancelAppointments(appointments);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'CANCELLED'"));
        assertEquals(2,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'PENDING'"));
    }

    @Test
    public void testAppointmentCancelAppointmentsNull()
    {
        // 1. Precondiciones
        insertAppointment();
        insertAnotherAppointment();
        insertAppointmentToNotify();
        insertAnotherAppointmentToNotify();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.appointmentDao.cancelAppointments(null);

        // 3. Postcondiciones
        // IllegalArgumentException
    }

    @Test
    public void testAppointmentCancelAppointmentsEmptyList()
    {
        // 1. Precondiciones
        insertAppointment();
        insertAnotherAppointment();
        insertAppointmentToNotify();
        insertAnotherAppointmentToNotify();
        List<Appointment> appointments = new LinkedList<>();

        // 2. Ejercitar
        this.appointmentDao.cancelAppointments(appointments);

        // 3. Postcondiciones
        assertEquals(4,JdbcTestUtils.countRowsInTable(jdbcTemplate, APPOINTMENTS_TABLE));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'CANCELLED'"));
        assertEquals(4,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, APPOINTMENTS_TABLE, "status = 'PENDING'"));
    }
}
