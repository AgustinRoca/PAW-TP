package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.PatientDao;
import ar.edu.itba.paw.models.*;
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
public class PatientDaoImplTest {
    private static final int STARTING_ID = 1;
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

    private static final String OFFICES_TABLE = "office";
    private static final String LOCALITIES_TABLE = "system_locality";
    private static final String PROVINCES_TABLE = "system_province";
    private static final String COUNTRIES_TABLE = "system_country";
    private static final String USERS_TABLE = "users";
    private static final String PICTURES_TABLE = "picture";
    private static final String PATIENTS_TABLE = "patient";

    @Autowired
    private PatientDao patientDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert userJdbcInsert;
    private SimpleJdbcInsert pictureJdbcInsert;
    private SimpleJdbcInsert officeJdbcInsert;
    private SimpleJdbcInsert localityJdbcInsert;
    private SimpleJdbcInsert provinceJdbcInsert;
    private SimpleJdbcInsert countryJdbcInsert;
    private SimpleJdbcInsert patientJdbcInsert;


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
        try {
            u.setEmail(EMAIL_2);
            u.setPassword(PASSWORD_2);
            u.setFirstName(FIRST_NAME_2);
            u.setSurname(SURNAME_2);
            u.setPhone(PHONE_2);
            u.setProfilePicture(pictureModel2());
//            u.setVerificationToken(null);
//            u.setVerificationTokenCreatedDate(null);
            u.setVerified(true);
            u.setId(STARTING_ID + 1);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return u;
    }

    private Picture pictureModel2(){
        Picture p = new Picture();
        p.setId(STARTING_ID + 1);
        p.setMimeType(MIME_TYPE);
        p.setSize(IMG_SIZE);
        p.setData(IMG_DATA);
        p.setName(PICTURE);
        return p;
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
        o.setId(STARTING_ID + 1);
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
        p.setId(STARTING_ID);
        p.setOffice(officeModel());
        p.setUser(userModel());
        return p;
    }

    private Patient patientModel2(){
        Patient p = new Patient();
        p.setId(STARTING_ID);
        p.setOffice(officeModel2());
        p.setUser(userModel2());
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
        map.put("profile_id", PROFILE_ID);
        map.put("token", TOKEN);
        map.put("token_created_date", null);
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
        insertPicture();
        Map<String, Object> map = new HashMap<>();
        map.put("first_name", FIRST_NAME_2);
        map.put("surname", SURNAME_2);
        map.put("password", PASSWORD_2);
        map.put("email", EMAIL_2);
        map.put("phone", PHONE_2);
        map.put("profile_id", PROFILE_ID_2);
        map.put("token", TOKEN);
        map.put("token_created_date", null);
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
        officeMap.put("locality_id", STARTING_ID);
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
        officeMap.put("locality_id", STARTING_ID);
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);
    }
    
    private void insertPatient() {
        insertUser();
        insertOffice();
        Map<String, Object> patientMap = new HashMap<>();
        patientMap.put("user_id", STARTING_ID);
        patientMap.put("office_id", STARTING_ID);
        patientJdbcInsert.execute(patientMap);
    }

    private void insertAnotherPatient() {
        insertAnotherOffice();
        insertAnotherUser();
        Map<String, Object> patientMap = new HashMap<>();
        patientMap.put("user_id", STARTING_ID + 1);
        patientMap.put("office_id", STARTING_ID + 1);
        patientJdbcInsert.execute(patientMap);
    }

    /* --------------------- MÉTODO: patientDao.create(Patient) -------------------------------------------- */

    @Test
    public void testCreatePatientSuccessfully() {
        // 1. Precondiciones
        insertOffice();
        insertUser();
        Patient p = patientModel();
        p.setId(null);

        // 2. Ejercitar
        Patient patient = this.patientDao.create(p);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, PATIENTS_TABLE));
        assertEquals(userModel(), patient.getUser());
        assertEquals(officeModel(), patient.getOffice());
    }

    @Test
    public void testCreateAnotherPatientSuccessfully() {
        // 1. Precondiciones
        insertUser();
        insertOffice();
        insertAnotherPatient();
        Patient p = patientModel();
        p.setId(null);

        // 2. Ejercitar
        Patient patient = this.patientDao.create(p);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, PATIENTS_TABLE));
        assertEquals(userModel(), patient.getUser());
        assertEquals(officeModel(), patient.getOffice());
    }

    @Test
    public void testCreatePatientNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Patient patient = this.patientDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreatePatientEmptyPatientFail()
    {
        // 1. Precondiciones

        Patient p = new Patient();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Patient patient = this.patientDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException (no data) o DataIntegrityViolationException (no id), se hace esto porque depende de cual chequea primero.
    }

    @Test
    public void testCreatePatientEmptyUserFail()
    {
        // 1. Precondiciones

        Patient p = patientModel();
        p.setUser(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Patient patient = this.patientDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    @Test
    public void testCreatePatientEmptyOfficeFail()
    {
        // 1. Precondiciones

        Patient p = patientModel();
        p.setOffice(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Patient patient = this.patientDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    /* --------------------- MÉTODO: patientDao.findById(String) -------------------------------------------- */

    @Test
    public void testFindPatientById()
    {
        // 1. Precondiciones

        insertPatient();
        insertAnotherPatient();

        // 2. Ejercitar
        Optional<Patient> patient = this.patientDao.findById(STARTING_ID);

        // 3. Postcondiciones
        assertTrue(patient.isPresent());
        assertEquals(userModel(), patient.get().getUser());
        assertEquals(officeModel(), patient.get().getOffice());
    }

    @Test
    public void testFindPatientByIdDoesntExist()
    {
        // 1. Precondiciones

        insertPatient();

        // 2. Ejercitar
        Optional<Patient> patient = this.patientDao.findById(STARTING_ID + 1);

        // 3. Postcondiciones
        assertFalse(patient.isPresent());
    }

    @Test
    public void testFindPatientByIdNull()
    {
        // 1. Precondiciones

        insertPatient();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Patient> patient = this.patientDao.findById(null);

        // 3. Postcondiciones
        assertFalse(patient.isPresent());
    }

    /* --------------------- MÉTODO: patientDao.findByIds(Collection<String>) -------------------------------------------- */

    @Test
    public void testFindPatientByIds()
    {
        // 1. Precondiciones

        insertPatient();
        insertAnotherPatient();

        // 2. Ejercitar
        Collection<Patient> patients = this.patientDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(patients);
        assertEquals(2, patients.size());
        for (Patient p : patients){
            assertTrue(p.getId().equals(STARTING_ID) || p.getId().equals(STARTING_ID + 1));
        }
    }

    @Test
    public void testFindPatientByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertPatient();

        // 2. Ejercitar
        Collection<Patient> patients = this.patientDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(patients);
        assertEquals(1, patients.size());
        for (Patient p : patients){
            assertEquals(patientModel(), p);
        }
    }

    @Test
    public void testFindPatientByIdsDontExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Patient> patients = this.patientDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(patients);
        assertTrue(patients.isEmpty());
    }

    /* --------------------- MÉTODO: patientDao.list() -------------------------------------------- */

    @Test
    public void testPatientList()
    {
        // 1. Precondiciones

        insertPatient();
        insertAnotherPatient();

        // 2. Ejercitar
        Collection<Patient> patients = this.patientDao.list();

        // 3. Postcondiciones
        assertNotNull(patients);
        assertEquals(2, patients.size());
    }

    @Test
    public void testPatientEmptyList()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Patient> patients = this.patientDao.list();

        // 3. Postcondiciones
        assertNotNull(patients);
        assertTrue(patients.isEmpty());
    }

    /* --------------------- MÉTODO: patientDao.update(Patient) -------------------------------------------- */

    @Test
    public void testPatientUpdate()
    {
        // 1. Precondiciones

        insertPatient();
        insertAnotherPatient();
        Map<String, Object> officeMap = new HashMap<>();
        officeMap.put("name", "Consultorio");
        officeMap.put("email", OFFICE_EMAIL);
        officeMap.put("phone", OFFICE_PHONE);
        officeMap.put("locality_id", STARTING_ID);
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);
        Office o = officeModel();
        o.setId(STARTING_ID + 2);
        o.setName("Consultorio");
        Patient p = patientModel();
        p.setOffice(o);


        // 2. Ejercitar
        this.patientDao.update(p);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
        assertEquals(3,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFICES_TABLE, "name = 'Consultorio'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, PATIENTS_TABLE, "office_id =" + (STARTING_ID)));
    }

    @Test
    public void testPatientUpdateNull()
    {
        // 1. Precondiciones

        insertPatient();
        insertAnotherPatient();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.patientDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    @Test
    public void testPatientUpdateNotExistentPatient()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        insertAnotherPatient();
        Patient p = patientModel();
        p.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.patientDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    @Test
    public void testPatientUpdatePatientWithNullUser()
    {
        // 1. Precondiciones

        insertPatient();
        Patient p = patientModel();
        p.setUser(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.patientDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
        assertEquals(patientModel(), p);
    }

    @Test
    public void testPatientUpdatePatientWithNullId()
    {
        // 1. Precondiciones

        insertPatient();
        Patient p = patientModel();
        p.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.patientDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    /* --------------------- MÉTODO: patientDao.remove(String id) -------------------------------------------- */

    @Test
    public void testPatientRemoveById()
    {
        // 1. Precondiciones

        insertPatient();

        // 2. Ejercitar
        this.patientDao.remove(STARTING_ID);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    @Test
    public void testPatientRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertPatient();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.patientDao.remove(STARTING_ID + 1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    @Test
    public void testPatientRemoveByNullId()
    {
        // 1. Precondiciones

        insertPatient();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.patientDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }
    /* --------------------- MÉTODO: patientDao.remove(Patient) -------------------------------------------- */

    @Test
    public void testPatientRemoveByModel()
    {
        // 1. Precondiciones

        insertPatient();
        Patient p = patientModel();

        // 2. Ejercitar
        this.patientDao.remove(p);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    @Test
    public void testPatientRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertUser();
        insertOffice();
        insertAnotherPatient();
        Patient p = patientModel();
        p.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.patientDao.remove(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    @Test
    public void testPatientRemoveByNullModel()
    {
        // 1. Precondiciones

        insertPatient();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.patientDao.remove((Patient) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PATIENTS_TABLE));
    }

    /* --------------------- MÉTODO: patientDao.count() -------------------------------------------- */

    @Test
    public void testPatientCount()
    {
        // 1. Precondiciones

        insertPatient();
        insertAnotherPatient();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.patientDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testPatientCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.patientDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }
}
