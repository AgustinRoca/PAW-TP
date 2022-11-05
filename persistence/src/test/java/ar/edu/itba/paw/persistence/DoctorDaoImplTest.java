package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.DoctorDao;
import ar.edu.itba.paw.models.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
public class DoctorDaoImplTest
{
    private static final int STARTING_ID = 1;
    private static final String OFFICE = "Hospital Nacional";
    private static final String OFFICE_2 = "Consultorio Provincial";
    private static final String STREET = "Av 9 de Julio 123";
    private static final String LOCALITY = "Capital Federal";
    private static final String PROVINCE = "Buenos Aires";
    private static final String COUNTRY = "Argentina";
    private static final String COUNTRY_ID = "AR";
    private static final String OFFICE_PHONE = "(011) 1234567890";
    private static final String OFFICE_EMAIL = "test@officetest.com";
    private static final String URL = "www.hnacional.com";
    private static final String FIRST_NAME = "Nombre";
    private static final String SURNAME = "Apellido";
    private static final String EMAIL = "napellido@test.com";
    private static final String PHONE = "1123456789";
    private static final String PASSWORD = "pass1234";
    private static final int PROFILE_ID = STARTING_ID;
    private static final String TOKEN = "123";
    private static final String FIRST_NAME_2 = "Roberto";
    private static final String SURNAME_2 = "Rodriguez";
    private static final String EMAIL_2 = "napellido2@test2.com";
    private static final String PHONE_2 = "(011) 1123456789";
    private static final String PASSWORD_2 = "password1234";
    private static final int PROFILE_ID_2 = STARTING_ID + 1;
    private static final String MIME_TYPE = "image/svg+xml";
    private static final String PICTURE = "defaultProfilePic.svg";
    private static final Resource IMG = new ClassPathResource("img/" + PICTURE);
    private static final byte[] IMG_DATA = getImgData(IMG);
    private static final long IMG_SIZE = getImgSize(IMG);
    private static final int REGISTRATION_NUMBER = 123;
    private static final String DOCTOR_SPECIALTY = "Odontologo";

    private static final String OFFICES_TABLE = "office";
    private static final String LOCALITIES_TABLE = "system_locality";
    private static final String PROVINCES_TABLE = "system_province";
    private static final String COUNTRIES_TABLE = "system_country";
    private static final String USERS_TABLE = "users";
    private static final String PICTURES_TABLE = "picture";
    private static final String DOCTORS_TABLE = "doctor";
    private static final String DOCTOR_SPECIALTIES_TABLE = "system_doctor_specialty";

    @Autowired
    private DoctorDao doctorDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert officeJdbcInsert;
    private SimpleJdbcInsert localityJdbcInsert;
    private SimpleJdbcInsert provinceJdbcInsert;
    private SimpleJdbcInsert countryJdbcInsert;
    private SimpleJdbcInsert userJdbcInsert;
    private SimpleJdbcInsert pictureJdbcInsert;
    private SimpleJdbcInsert doctorJdbcInsert;
    private SimpleJdbcInsert doctorSpecialtiesJdbcInsert;


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DataSource ds;
    
    @Before
    public void setUp(){
        this.jdbcTemplate = new JdbcTemplate(this.ds);
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
        this.userJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(USERS_TABLE)
                .usingGeneratedKeyColumns("users_id");
        this.pictureJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(PICTURES_TABLE)
                .usingGeneratedKeyColumns("picture_id");
        this.doctorJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(DOCTORS_TABLE)
                .usingGeneratedKeyColumns("doctor_id");
        this.doctorSpecialtiesJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(DOCTOR_SPECIALTIES_TABLE)
                .usingGeneratedKeyColumns("specialty_id");
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
     * token = null
     * tokenCreatedDate = null
     * id = STARTING_ID
     **/
    private User userModel() {
        User u = new User();
        try {
            u.setEmail(EMAIL);
            u.setPassword(PASSWORD);
            u.setFirstName(FIRST_NAME);
            u.setSurname(SURNAME);
            u.setPhone(PHONE);
            u.setProfilePicture(pictureModel());
//            u.setVerificationToken(TOKEN);
//            u.setVerificationTokenCreatedDate(null);
            u.setVerified(true);
            u.setId(STARTING_ID);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return u;
    }

    private Picture pictureModel(){
        Picture p = new Picture();
        p.setId(STARTING_ID);
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

    /**
     * Inserta en la db el user con
     * firstName = IMG_DATA
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
        map.put("profile_id", PROFILE_ID);
        map.put("token", TOKEN);
        map.put("token_created_date", null);
        userJdbcInsert.execute(map);
    }

    /**
     * Inserta en la db el user con
     * firstName = FIRST_NAME
     * surname = SURNAME
     * password = PASSWORD
     * email = EMAIL
     * phone = PHONE
     * profileID = PROFILE_ID
     * token = null
     * tokenCreatedDate = null
     **/
    private void insertAnotherUser() {
        insertPicture();
        insertPicture();
        Map<String, Object> map = new HashMap<>();
        map.put("first_name", FIRST_NAME_2);
        map.put("surname", SURNAME_2);
        map.put("password", PASSWORD_2);
        map.put("email", EMAIL_2);
        map.put("phone", PHONE_2);
        map.put("profile_id", PROFILE_ID_2);
        map.put("token", null);
        map.put("token_created_date", null);
        userJdbcInsert.execute(map);
    }

    /** Devuelve una locality con id=STARTING_ID, name=PROVINCE y como country el devuelto en countryModel() **/
    private Locality localityModel(){
        Locality l = new Locality();
        l.setName(LOCALITY);
        l.setProvince(provinceModel());
        l.setId(STARTING_ID);
        return l;
    }

    /** Devuelve una province con id=STARTING_ID, name=PROVINCE y como country el devuelto en countryModel() **/
    private Province provinceModel(){
        Province p = new Province();
        p.setId(STARTING_ID);
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
        o.setId(STARTING_ID);
        o.setName(OFFICE);
        o.setEmail(OFFICE_EMAIL);
        o.setPhone(OFFICE_PHONE);
        o.setLocality(localityModel());
        o.setStreet(STREET);
        o.setUrl(URL);
        return o;
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
        map.put("province_id", STARTING_ID);
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
        officeMap.put("locality_id", STARTING_ID); // Identity de HSQLDB empieza en 0
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);
    }

    /** Inserta en la db la oficina con
     * localityId=STARTING_ID
     * name=OFFICE
     * email=EMAIL
     * phone=PHONE
     * street=STREET
     * url=URL
     **/
    private void insertAnotherOffice(){
        Map<String, Object> officeMap = new HashMap<>();
        officeMap.put("name", OFFICE_2);
        officeMap.put("email", OFFICE_EMAIL);
        officeMap.put("phone", OFFICE_PHONE);
        officeMap.put("locality_id", STARTING_ID); // Identity de HSQLDB empieza en 0
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);
    }
    
    private Doctor doctorModel(){
        Doctor s = new Doctor();
        s.setRegistrationNumber(REGISTRATION_NUMBER);
        s.setEmail(EMAIL);
        s.setPhone(PHONE);
        s.setId(STARTING_ID);
        s.setUser(userModel());
        s.setOffice(officeModel());
        return s;
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
        insertOffice();
        insertUser();
        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL); // Identity de HSQLDB empieza en 0
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);
    }

    private void insertAnotherDoctor(){
        insertAnotherUser();
        insertAnotherOffice();
        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME_2);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME_2);
        doctorMap.put("email", EMAIL_2);
        doctorMap.put("phone", PHONE_2);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID + 1);
        doctorJdbcInsert.execute(doctorMap);
    }

    /** Inserta en la db la especialidad con name=DOCTOR_SPECIALTY **/
    private void insertDoctorSpecialty(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", DOCTOR_SPECIALTY);
        doctorSpecialtiesJdbcInsert.execute(map);
    }

    /** Devuelve un DoctorSpecialty con name=DOCTOR_SPECIALTY **/
    private DoctorSpecialty doctorSpecialtyModel(){
        DoctorSpecialty ss = new DoctorSpecialty();
        ss.setName(DOCTOR_SPECIALTY);
        ss.setId(STARTING_ID);
        return ss;
    }

    /* --------------------- MÉTODO: doctorDao.create(Doctor) -------------------------------------------- */

    @Test
    public void testCreateDoctorSuccessfully() {
        // 1. Precondiciones
        insertUser();
        insertOffice();
        Doctor s = doctorModel();
        s.setId(null);

        // 2. Ejercitar
        Doctor doctor = this.doctorDao.create(s);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, DOCTORS_TABLE));
        assertEquals(FIRST_NAME, doctor.getUser().getFirstName());
        assertEquals(EMAIL, doctor.getEmail());
        assertEquals(officeModel(), doctor.getOffice());
        assertEquals(PHONE, doctor.getPhone());
        assertEquals(REGISTRATION_NUMBER, doctor.getRegistrationNumber());
        assertEquals(new LinkedList<>(), doctor.getDoctorSpecialties());
        assertEquals(SURNAME, doctor.getUser().getSurname());
        assertEquals(userModel(), doctor.getUser());
    }

    @Test
    public void testCreateAnotherDoctorSuccessfully() {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        insertAnotherDoctor();
        Doctor s = doctorModel();

        // 2. Ejercitar
        s.setId(null);
        Doctor doctor = this.doctorDao.create(s);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, DOCTORS_TABLE));
        assertEquals(FIRST_NAME, doctor.getUser().getFirstName());
        assertEquals(EMAIL, doctor.getEmail());
        assertEquals(officeModel(), doctor.getOffice());
        assertEquals(PHONE, doctor.getPhone());
        assertEquals(REGISTRATION_NUMBER, doctor.getRegistrationNumber());
        assertEquals(new LinkedList<>(), doctor.getDoctorSpecialties());
        assertEquals(SURNAME, doctor.getUser().getSurname());
        assertEquals(userModel(), doctor.getUser());
    }

    @Test
    public void testCreateDoctorNullFail()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Doctor doctor = this.doctorDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreateDoctorEmptyDoctorFail()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        Doctor s = new Doctor();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Doctor doctor = this.doctorDao.create(s);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException (no data) o DataIntegrityViolationException (no id), se hace esto porque depende de cual chequea primero.
    }

    @Test
    public void testCreateDoctorEmptyUserFail()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        Doctor s = doctorModel();
        s.setUser(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Doctor doctor = this.doctorDao.create(s);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    @Test
    public void testCreateDoctorEmptyOfficeFail()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        Doctor s = doctorModel();
        s.setOffice(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Doctor doctor = this.doctorDao.create(s);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    @Test
    public void testCreateDoctorEmptyEmailFail()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        Doctor s = doctorModel();
        s.setEmail(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Doctor doctor = this.doctorDao.create(s);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    /* --------------------- MÉTODO: doctorDao.findById(String) -------------------------------------------- */

    @Test
    public void testFindDoctorById()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Optional<Doctor> doctor = this.doctorDao.findById(STARTING_ID);

        // 3. Postcondiciones
        assertTrue(doctor.isPresent());
        assertEquals(doctorModel(), doctor.get());
    }

    @Test
    public void testFindDoctorByIdDoesntExist()
    {
        // 1. Precondiciones

        insertDoctor();

        // 2. Ejercitar
        Optional<Doctor> doctor = this.doctorDao.findById(STARTING_ID + 1);

        // 3. Postcondiciones
        assertFalse(doctor.isPresent());
    }

    @Test
    public void testFindDoctorByIdNull()
    {
        // 1. Precondiciones

        insertDoctor();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Doctor> doctor = this.doctorDao.findById(null);

        // 3. Postcondiciones
        assertFalse(doctor.isPresent());
    }

    /* --------------------- MÉTODO: doctorDao.findByIds(Collection<String>) -------------------------------------------- */

    @Test
    public void testFindDoctorByIds()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Collection<Doctor> doctors = this.doctorDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
        for (Doctor s : doctors){
            assertTrue(s.getId().equals(STARTING_ID) || s.getId().equals(STARTING_ID + 1));
        }
    }

    @Test
    public void testFindDoctorByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertDoctor();

        // 2. Ejercitar
        Collection<Doctor> doctors = this.doctorDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
        for (Doctor s : doctors){
            assertEquals(doctorModel(), s);
        }
    }

    @Test
    public void testFindDoctorByIdsDontExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Doctor> doctors = this.doctorDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertTrue(doctors.isEmpty());
    }

    /* --------------------- MÉTODO: doctorDao.list() -------------------------------------------- */

    @Test
    public void testDoctorList()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Collection<Doctor> doctors = this.doctorDao.list();

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    @Test
    public void testDoctorEmptyList()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Doctor> doctors = this.doctorDao.list();

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertTrue(doctors.isEmpty());
    }

    /* --------------------- MÉTODO: doctorDao.update(Doctor) -------------------------------------------- */

    @Test
    public void testDoctorUpdate()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();
        Doctor s = doctorModel();
        s.setPhone("Armenia");

        // 2. Ejercitar
        this.doctorDao.update(s);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, DOCTORS_TABLE, "phone = 'Armenia'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, DOCTORS_TABLE, "phone = '"+ PHONE +"'"));
    }

    @Test
    public void testDoctorUpdateNull()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    @Test
    public void testDoctorUpdateNotExistentDoctor()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        insertAnotherDoctor();
        Doctor s = doctorModel();
        s.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorDao.update(s);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    @Test
    public void testDoctorUpdateDoctorWithNullId()
    {
        // 1. Precondiciones

        insertDoctor();
        Doctor s = doctorModel();
        s.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorDao.update(s);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    /* --------------------- MÉTODO: doctorDao.remove(String id) -------------------------------------------- */

    @Test
    public void testDoctorRemoveById()
    {
        // 1. Precondiciones

        insertDoctor();

        // 2. Ejercitar
        this.doctorDao.remove(STARTING_ID);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    @Test
    public void testDoctorRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertDoctor();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorDao.remove(STARTING_ID + 1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    @Test
    public void testDoctorRemoveByNullId()
    {
        // 1. Precondiciones

        insertDoctor();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }
    /* --------------------- MÉTODO: doctorDao.remove(Doctor) -------------------------------------------- */

    @Test
    public void testDoctorRemoveByModel()
    {
        // 1. Precondiciones

        insertDoctor();
        Doctor s = doctorModel();

        // 2. Ejercitar
        this.doctorDao.remove(s);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    @Test
    public void testDoctorRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        insertAnotherDoctor();
        Doctor s = doctorModel();
        s.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorDao.remove(s);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    @Test
    public void testDoctorRemoveByNullModel()
    {
        // 1. Precondiciones

        insertDoctor();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorDao.remove((Doctor) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTORS_TABLE));
    }

    /* --------------------- MÉTODO: doctorDao.count() -------------------------------------------- */

    @Test
    public void testDoctorCount()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.doctorDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testDoctorCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.doctorDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }

    /* --------------------- MÉTODO: doctorDao.findBy(Collection<String> names, Collection<String> surnames, Collection<Office>, Collection<DoctorSpecialty>, Collection<Locality>) -------------------------------------------- */

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocality()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy(Collections.singletonList(FIRST_NAME), Collections.singletonList(SURNAME), Collections.singletonList(officeModel()), Collections.emptyList(), Collections.singletonList(localityModel()));

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityNullAll()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((Collection<String>) null, null, null, null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityOnlyFirstName()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy(Collections.singletonList(FIRST_NAME), null, null, null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityOnlySurname()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy(null, Collections.singletonList(SURNAME), null, null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityOnlyOffice()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((Collection<String>) null, null, Collections.singleton(officeModel()), null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityOnlySpecialties()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((Collection<String>) null, null, null, Collections.emptyList(), null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityOnlyLocality()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((Collection<String>) null, null, null, null, Collections.singleton(localityModel()));

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    /* --------------------- MÉTODO: doctorDao.findBy(String name, String surname, Collection<Office>, Collection<DoctorSpecialty>, Collection<Locality>) -------------------------------------------- */

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocality()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy(FIRST_NAME, SURNAME, Collections.singletonList(officeModel()), Collections.emptyList(), Collections.singletonList(localityModel()));

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityNullAll()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((String)null, null, null, null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityOnlyFirstName()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy(FIRST_NAME, null, null, null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityOnlySurname()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy(null, SURNAME, null, null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityOnlyOffice()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((String) null, null, Collections.singleton(officeModel()), null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityOnlySpecialties()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((String) null, null, null, Collections.emptyList(), null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityOnlyLocality()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy((String) null, null, null, null, Collections.singleton(localityModel()));

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    /* --------------------- MÉTODO: doctorDao.findBy(Collection<String> names, Collection<String> surnames, Collection<Office>, Collection<DoctorSpecialty>, Collection<Locality>, int page, int pageSize) -------------------------------------------- */

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityPage()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy(Collections.singletonList(FIRST_NAME), Collections.singletonList(SURNAME), Collections.singletonList(officeModel()), Collections.emptyList(), Collections.singletonList(localityModel()), 1, 1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(0, paginator.getRemainingPages());
        assertEquals(1, (long) paginator.getTotalCount());
        assertEquals(1, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityPageNullAll()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((Collection<String>) null, null, null, null, null, 1, 1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(1, paginator.getRemainingPages());
        assertEquals(2, (long) paginator.getTotalCount());
        assertEquals(2, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityPageOnlyFirstName()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        List<Doctor> doctors = this.doctorDao.findBy(Collections.singletonList(FIRST_NAME), null, null, null, null);

        // 3. Postcondiciones
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityPageOnlySurname()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy(null, Collections.singletonList(SURNAME), null, null, null, 1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(0, paginator.getRemainingPages());
        assertEquals(1, (long) paginator.getTotalCount());
        assertEquals(1, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityPageOnlyOffice()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((Collection<String>) null, null, Collections.singleton(officeModel()), null, null,1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(0, paginator.getRemainingPages());
        assertEquals(1, (long) paginator.getTotalCount());
        assertEquals(1, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityPageOnlySpecialties()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((Collection<String>) null, null, null, Collections.emptyList(), null, 1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(1, paginator.getRemainingPages());
        assertEquals(2, (long) paginator.getTotalCount());
        assertEquals(2, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNamesOfficeDoctorSpecialtyLocalityPageOnlyLocality()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((Collection<String>) null, null, null, null, Collections.singleton(localityModel()), 1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(1, paginator.getRemainingPages());
        assertEquals(2, (long) paginator.getTotalCount());
        assertEquals(2, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    /* --------------------- MÉTODO: doctorDao.findBy(String name, String surname, Collection<Office>, Collection<DoctorSpecialty>, Collection<Locality>, int page, int pageSize) -------------------------------------------- */

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityPage()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy(FIRST_NAME, SURNAME, Collections.singletonList(officeModel()), Collections.emptyList(), Collections.singletonList(localityModel()), 1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(0, paginator.getRemainingPages());
        assertEquals(1, (long) paginator.getTotalCount());
        assertEquals(1, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityPageNullAll()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((String)null, null, null, null, null,1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(1, paginator.getRemainingPages());
        assertEquals(2, (long) paginator.getTotalCount());
        assertEquals(2, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityPageOnlyFirstName()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy(FIRST_NAME, null, null, null, null, 1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(0, paginator.getRemainingPages());
        assertEquals(1, (long) paginator.getTotalCount());
        assertEquals(1, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityPageOnlySurname()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy(null, SURNAME, null, null, null, 1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(0, paginator.getRemainingPages());
        assertEquals(1, (long) paginator.getTotalCount());
        assertEquals(1, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityPageOnlyOffice()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((String) null, null, Collections.singleton(officeModel()), null, null, 1, 1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(0, paginator.getRemainingPages());
        assertEquals(1, (long)paginator.getTotalCount());
        assertEquals(1, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityPageOnlySpecialties()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((String) null, null, null, Collections.emptyList(), null, 1,1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(1, paginator.getRemainingPages());
        assertEquals(2, (long) paginator.getTotalCount());
        assertEquals(2, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }

    @Test
    public void testDoctorFindByNameOfficeDoctorSpecialtyLocalityPageOnlyLocality()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherDoctor();

        // 2. Ejercitar
        Paginator<Doctor> paginator = this.doctorDao.findBy((String) null, null, null, null, Collections.singleton(localityModel()), 1, 1);

        // 3. Postcondiciones
        assertNotNull(paginator);
        assertEquals(1, paginator.getPage());
        assertEquals(1, paginator.getPageSize());
        assertEquals(1, paginator.getRemainingPages());
        assertEquals(2, (long) paginator.getTotalCount());
        assertEquals(2, paginator.getTotalPages());
        assertEquals(1, paginator.getModels().size());
    }
}