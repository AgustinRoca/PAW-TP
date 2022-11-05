package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.WorkdayDao;
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
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Sql(scripts = "classpath:sql/schema.sql")
@ContextConfiguration(classes = TestConfig.class)
public class WorkdayDaoImplTest
{
    private static final int STARTING_ID = 1;
    private static final String OFFICE = "Hospital Nacional";
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
    private static final String MIME_TYPE = "image/svg+xml";
    private static final String PICTURE = "defaultProfilePic.svg";
    private static final Resource IMG = new ClassPathResource("img/" + PICTURE);
    private static final byte[] IMG_DATA = getImgData(IMG);
    private static final long IMG_SIZE = getImgSize(IMG);
    private static final int REGISTRATION_NUMBER = 123;
    private static final int START_HOUR = 8;
    private static final int START_MINUTE = 30;
    private static final int END_HOUR = 13;
    private static final int END_MINUTE = 45;
    private static final WorkdayDay DAY = WorkdayDay.MONDAY;
    private static final int START_HOUR_2 = 15;
    private static final int START_MINUTE_2 = 30;
    private static final int END_HOUR_2 = 18;
    private static final int END_MINUTE_2 = 0;
    private static final WorkdayDay DAY_2 = WorkdayDay.TUESDAY;

    private static final String OFFICES_TABLE = "office";
    private static final String LOCALITIES_TABLE = "system_locality";
    private static final String PROVINCES_TABLE = "system_province";
    private static final String COUNTRIES_TABLE = "system_country";
    private static final String USERS_TABLE = "users";
    private static final String PICTURES_TABLE = "picture";
    private static final String DOCTORS_TABLE = "doctor";
    private static final String WORKDAYS_TABLE = "workday";

    @Autowired
    private WorkdayDao workdayDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert officeJdbcInsert;
    private SimpleJdbcInsert localityJdbcInsert;
    private SimpleJdbcInsert provinceJdbcInsert;
    private SimpleJdbcInsert countryJdbcInsert;
    private SimpleJdbcInsert userJdbcInsert;
    private SimpleJdbcInsert pictureJdbcInsert;
    private SimpleJdbcInsert doctorJdbcInsert;
    private SimpleJdbcInsert workdayJdbcInsert;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
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
        this.workdayJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(WORKDAYS_TABLE)
                .usingGeneratedKeyColumns("workday_id");
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
        map.put("verification_token", TOKEN);
        map.put("verification_token_created_date", null);
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

    /** Inserta en la db el workday con
     * doctor_id=STARTING_ID
     * start_hour=START_HOUR
     * start_minute=START_MINUTE
     * end_hour=END_HOUR
     * end_minute=END_MINUTE
     * day=DAY
     **/
    private void insertWorkday(){
        insertDoctor();
        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID);
        map.put("start_hour", START_HOUR);
        map.put("start_minute", START_MINUTE);
        map.put("end_hour", END_HOUR);
        map.put("end_minute", END_MINUTE);
        map.put("day", DAY);
        workdayJdbcInsert.execute(map);
    }

    /** Inserta en la db el workday con
     * doctor_id=STARTING_ID
     * start_hour=START_HOUR_2
     * start_minute=START_MINUTE_2
     * end_hour=END_HOUR_2
     * end_minute=END_MINUTE_2
     * day=DAY_2
     **/
    private void insertAnotherWorkday(){
        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);
    }

    /** Devuelve un workday con
     * doctor_id=STARTING_ID
     * start_hour=START_HOUR
     * start_minute=START_MINUTE
     * end_hour=END_HOUR
     * end_minute=END_MINUTE
     * day=DAY
     **/
    private Workday workdayModel(){
        Workday w = new Workday();
        w.setId(STARTING_ID);
        w.setDoctor(doctorModel());
        w.setEndMinute(END_MINUTE);
        w.setEndHour(END_HOUR);
        w.setStartMinute(START_MINUTE);
        w.setStartHour(START_HOUR);
        w.setDay(DAY);
        return w;
    }

    /** Devuelve un workday con
     * doctor_id=STARTING_ID
     * start_hour=START_HOUR
     * start_minute=START_MINUTE
     * end_hour=END_HOUR
     * end_minute=END_MINUTE
     * day=DAY
     **/
    private Workday workdayModel2(){
        Workday w = new Workday();
        w.setId(STARTING_ID + 1);
        w.setDoctor(doctorModel());
        w.setEndMinute(END_MINUTE_2);
        w.setEndHour(END_HOUR_2);
        w.setStartMinute(START_MINUTE_2);
        w.setStartHour(START_HOUR_2);
        w.setDay(DAY_2);
        return w;
    }
    
    /* --------------------- MÉTODO: workdayDao.create(Workday) -------------------------------------------- */

    @Test
    public void testCreateWorkdaySuccessfully() {
        // 1. Precondiciones
        insertDoctor();
        Workday w = workdayModel();
        w.setId(null);


        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(doctorModel(), workday.getDoctor());
        assertEquals(START_HOUR, (int)workday.getStartHour());
        assertEquals(START_MINUTE, (int)workday.getStartMinute());
        assertEquals(END_HOUR, (int)workday.getEndHour());
        assertEquals(END_MINUTE, (int)workday.getEndMinute());
        assertEquals(DAY, workday.getDay());
    }

    @Test
    public void testCreateAnotherWorkdaySuccessfully() {
        // 1. Precondiciones
        insertDoctor();
        insertAnotherWorkday();
        Workday w = workdayModel();
        w.setId(null);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(doctorModel(), workday.getDoctor());
        assertEquals(START_HOUR, (int)workday.getStartHour());
        assertEquals(START_MINUTE, (int)workday.getStartMinute());
        assertEquals(END_HOUR, (int)workday.getEndHour());
        assertEquals(END_MINUTE, (int)workday.getEndMinute());
        assertEquals(DAY, workday.getDay());
    }

    @Test
    public void testCreateWorkdayNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create((Workday) null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreateWorkdayEmptyWorkdayFail()
    {
        // 1. Precondiciones

        Workday w = new Workday();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateWorkdayEmptyDoctorFail()
    {
        // 1. Precondiciones

        insertDoctor();
        Workday w = workdayModel();
        w.setDoctor(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateWorkdayEmptyStartMinuteFail()
    {
        // 1. Precondiciones

        insertDoctor();
        Workday w = workdayModel();
        w.setStartMinute(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateWorkdayEmptyStartHourFail()
    {
        // 1. Precondiciones

        insertDoctor();
        Workday w = workdayModel();
        w.setStartHour(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateWorkdayEmptyEndHourFail()
    {
        // 1. Precondiciones

        insertDoctor();
        Workday w = workdayModel();
        w.setEndHour(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }
    
    @Test
    public void testCreateWorkdayEmptyEndMinuteFail()
    {
        // 1. Precondiciones

        insertDoctor();
        Workday w = workdayModel();
        w.setEndMinute(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateWorkdayEmptyDayFail()
    {
        // 1. Precondiciones

        insertDoctor();
        Workday w = workdayModel();
        w.setDay(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    @Test
    public void testCreateWorkdayInvalidHoursFail()
    {
        // 1. Precondiciones

        insertDoctor();
        Workday w = workdayModel();
        w.setStartMinute(w.getEndMinute() + 1);
        w.setStartHour(w.getEndHour());
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Workday workday = this.workdayDao.create(w);

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }

    /* --------------------- MÉTODO: workdayDao.findById(String) -------------------------------------------- */

    @Test
    public void testFindWorkdayById()
    {
        // 1. Precondiciones

        insertWorkday();
        insertAnotherWorkday();

        // 2. Ejercitar
        Optional<Workday> workday = this.workdayDao.findById(STARTING_ID);

        // 3. Postcondiciones
        assertTrue(workday.isPresent());
        assertEquals(STARTING_ID, (int) workday.get().getId());
    }

    @Test
    public void testFindWorkdayByIdDoesntExist()
    {
        // 1. Precondiciones

        insertWorkday();

        // 2. Ejercitar
        Optional<Workday> workday = this.workdayDao.findById(STARTING_ID + 1);

        // 3. Postcondiciones
        assertFalse(workday.isPresent());
    }

    @Test
    public void testFindWorkdayByIdNull()
    {
        // 1. Precondiciones

        insertWorkday();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Workday> workday = this.workdayDao.findById(null);

        // 3. Postcondiciones
        assertFalse(workday.isPresent());
    }

    /* --------------------- MÉTODO: workdayDao.findByIds(Collection<String>) -------------------------------------------- */

    @Test
    public void testFindWorkdayByIds()
    {
        // 1. Precondiciones

        insertWorkday();
        insertAnotherWorkday();

        // 2. Ejercitar
        Collection<Workday> workdays = this.workdayDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(2, workdays.size());
        for (Workday w : workdays){
            assertTrue(w.getId().equals(STARTING_ID) || w.getId().equals(STARTING_ID + 1));
        }
    }

    @Test
    public void testFindWorkdayByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertWorkday();

        // 2. Ejercitar
        Collection<Workday> workdays = this.workdayDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(1, workdays.size());
        for (Workday w : workdays){
            assertEquals(workdayModel(), w);
        }
    }

    @Test
    public void testFindWorkdayByIdsDontExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Workday> workdays = this.workdayDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertTrue(workdays.isEmpty());
    }

    /* --------------------- MÉTODO: workdayDao.list() -------------------------------------------- */

    @Test
    public void testWorkdayList()
    {
        // 1. Precondiciones

        insertWorkday();
        insertAnotherWorkday();

        // 2. Ejercitar
        Collection<Workday> workdays = this.workdayDao.list();

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(2, workdays.size());
    }

    @Test
    public void testWorkdayEmptyList()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Workday> workdays = this.workdayDao.list();

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertTrue(workdays.isEmpty());
    }

    /* --------------------- MÉTODO: workdayDao.update(Workday) -------------------------------------------- */

    @Test
    public void testWorkdayUpdate()
    {
        // 1. Precondiciones

        insertWorkday();
        insertAnotherWorkday();
        Workday w = workdayModel();
        w.setDay(WorkdayDay.WEDNESDAY);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, WORKDAYS_TABLE, "day = '"+ WorkdayDay.WEDNESDAY.name() +"'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, WORKDAYS_TABLE, "day = '"+ DAY +"'"));
    }

    @Test
    public void testWorkdayUpdateNull()
    {
        // 1. Precondiciones

        insertWorkday();
        insertAnotherWorkday();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.workdayDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    @Test
    public void testWorkdayUpdateNotExistentWorkday()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherWorkday();
        Workday w = workdayModel();
        w.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    @Test
    public void testWorkdayUpdateWorkdayWithNullDoctor()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();
        w.setDoctor(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(workdayModel(), w);
    }

    @Test
    public void testWorkdayUpdateWorkdayWithNullId()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();
        w.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    @Test
    public void testWorkdayUpdateWorkdayWithNullStartHour()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();
        w.setStartHour(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(workdayModel(), w);
    }

    @Test
    public void testWorkdayUpdateWorkdayWithNullStartMinute()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();
        w.setStartMinute(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(workdayModel(), w);
    }

    @Test
    public void testWorkdayUpdateWorkdayWithNullEndHour()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();
        w.setEndHour(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(workdayModel(), w);
    }

    @Test
    public void testWorkdayUpdateWorkdayWithNullEndMinute()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();
        w.setEndMinute(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(workdayModel(), w);
    }

    @Test
    public void testWorkdayUpdateWorkdayWithNullDay()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();
        w.setDay(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.workdayDao.update(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(workdayModel(), w);
    }

    /* --------------------- MÉTODO: workdayDao.remove(String id) -------------------------------------------- */

    @Test
    public void testWorkdayRemoveById()
    {
        // 1. Precondiciones

        insertWorkday();

        // 2. Ejercitar
        this.workdayDao.remove(STARTING_ID);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    @Test
    public void testWorkdayRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertWorkday();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.workdayDao.remove(STARTING_ID + 1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    @Test
    public void testWorkdayRemoveByNullId()
    {
        // 1. Precondiciones

        insertWorkday();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.workdayDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }
    /* --------------------- MÉTODO: workdayDao.remove(Workday) -------------------------------------------- */

    @Test
    public void testWorkdayRemoveByModel()
    {
        // 1. Precondiciones

        insertWorkday();
        Workday w = workdayModel();

        // 2. Ejercitar
        this.workdayDao.remove(w);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    @Test
    public void testWorkdayRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertDoctor();
        insertAnotherWorkday();
        Workday w = workdayModel();
        w.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.workdayDao.remove(w);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    @Test
    public void testWorkdayRemoveByNullModel()
    {
        // 1. Precondiciones

        insertWorkday();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.workdayDao.remove((Workday) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
    }

    /* --------------------- MÉTODO: workdayDao.count() -------------------------------------------- */

    @Test
    public void testWorkdayCount()
    {
        // 1. Precondiciones

        insertWorkday();
        insertAnotherWorkday();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.workdayDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testWorkdayCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.workdayDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }

    /* --------------------- MÉTODO: workdayDao.findByDoctor(Doctor) -------------------------------------------- */

    @Test
    public void testWorkdayFindByDoctor()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(doctorModel());

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(1, workdays.size());
        for (Workday workday: workdays){
            assertEquals(doctorModel(), workday.getDoctor());
        }
    }

    @Test
    public void testWorkdayFindByDoctorNull()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(null);

        // 3. Postcondiciones
        // que el metodo tire NullPointerException
    }

    @Test
    public void testWorkdayFindByDoctorDoesntExists()
    {
        // 1. Precondiciones

        insertWorkday();
        Doctor s = doctorModel();
        s.setId(STARTING_ID + 1);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(s);

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(0, workdays.size());
    }

    /* --------------------- MÉTODO: workdayDao.findByDoctor(Doctor, WorkdayDay) -------------------------------------------- */

    @Test
    public void testWorkdayFindByDoctorWorkdayDay()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(doctorModel(), WorkdayDay.MONDAY);

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(1, workdays.size());
        for (Workday workday: workdays){
            assertEquals(doctorModel(), workday.getDoctor());
        }
    }

    @Test
    public void testWorkdayFindByDoctorWorkdayDayDoctorNull()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(null, WorkdayDay.MONDAY);

        // 3. Postcondiciones
        // que el metodo tire NullPointerException
    }

    @Test
    public void testWorkdayFindByDoctorWorkdayDayDoctorDoesntExists()
    {
        // 1. Precondiciones

        insertWorkday();
        Doctor s = doctorModel();
        s.setId(STARTING_ID + 1);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(s, WorkdayDay.MONDAY);

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(0, workdays.size());
    }

    @Test
    public void testWorkdayFindByDoctorWorkdayDayWorkdayDayNull()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(doctorModel(), null);

        // 3. Postcondiciones
        // que el metodo tire NullPointerException
    }

    @Test
    public void testWorkdayFindByDoctorWorkdayDayWorkdayDayDoesntExists()
    {
        // 1. Precondiciones

        insertWorkday();
        Doctor s = doctorModel();
        s.setId(STARTING_ID + 1);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(s, WorkdayDay.FRIDAY);

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(0, workdays.size());
    }

    @Test
    public void testWorkdayFindByDoctorWorkdayDayBothNull()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByDoctor(null, null);

        // 3. Postcondiciones
        // que el metodo tire NullPointerException
    }

    /* --------------------- MÉTODO: workdayDao.findByUser(User) -------------------------------------------- */

    @Test
    public void testWorkdayFindByUser()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByUser(userModel());

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(1, workdays.size());
        for (Workday workday: workdays){
            assertEquals(userModel(), workday.getDoctor().getUser());
        }
    }

    @Test
    public void testWorkdayFindByUserNull()
    {
        // 1. Precondiciones

        insertWorkday();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("first_name", FIRST_NAME);
        userMap.put("surname", SURNAME);
        userMap.put("password", PASSWORD);
        userMap.put("email", EMAIL);
        userMap.put("phone", PHONE);
        userMap.put("profile_id", PROFILE_ID);
        userMap.put("verification_token", null);
        userMap.put("verification_token_created_date", null);
        userJdbcInsert.execute(userMap);

        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("first_name", FIRST_NAME);
        doctorMap.put("registration_number", REGISTRATION_NUMBER);
        doctorMap.put("surname", SURNAME);
        doctorMap.put("email", EMAIL);
        doctorMap.put("phone", PHONE);
        doctorMap.put("user_id", STARTING_ID + 1);
        doctorMap.put("office_id", STARTING_ID);
        doctorJdbcInsert.execute(doctorMap);

        Map<String, Object> map = new HashMap<>();
        map.put("doctor_id", STARTING_ID+1);
        map.put("start_hour", START_HOUR_2);
        map.put("start_minute", START_MINUTE_2);
        map.put("end_hour", END_HOUR_2);
        map.put("end_minute", END_MINUTE_2);
        map.put("day", DAY_2);
        workdayJdbcInsert.execute(map);

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByUser(null);

        // 3. Postcondiciones
        // que el metodo tire NullPointerException
    }

    @Test
    public void testWorkdayFindByUserDoesntExists()
    {
        // 1. Precondiciones

        insertWorkday();
        User u = userModel();
        u.setId(STARTING_ID + 1);

        // 2. Ejercitar
        List<Workday> workdays = this.workdayDao.findByUser(u);

        // 3. Postcondiciones
        assertNotNull(workdays);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(0, workdays.size());
    }

    /* --------------------- MÉTODO: workdayDao.isDoctorWorking(Doctor, AppointmentTimeSlot) -------------------------------------------- */

    @Test
    public void testWorkdayIsDoctorWorking()
    {
        // 1. Precondiciones

        insertWorkday();
        LocalDateTime startDate = new LocalDateTime(2020, 5, 25,START_HOUR, START_MINUTE); // 25/5/2020 es lunes
        AppointmentTimeSlot appointmentTimeSlot = new AppointmentTimeSlot();
        appointmentTimeSlot.setDate(startDate);

        // 2. Ejercitar
        boolean isDoctorWorking = workdayDao.doctorWorks(doctorModel(), appointmentTimeSlot);

        // 3. Postcondiciones
        assertTrue(isDoctorWorking);
    }

    @Test
    public void testWorkdayIsntDoctorWorking()
    {
        // 1. Precondiciones

        insertWorkday();
        LocalDateTime startDate = new LocalDateTime(2020, 5, 27, START_HOUR, START_MINUTE); // 27/5/2020 es miercoles
        AppointmentTimeSlot appointmentTimeSlot = new AppointmentTimeSlot();
        appointmentTimeSlot.setDate(startDate);

        // 2. Ejercitar
        boolean isDoctorWorking = workdayDao.doctorWorks(doctorModel(), appointmentTimeSlot);

        // 3. Postcondiciones
        assertFalse(isDoctorWorking);
    }

    @Test
    public void testWorkdayIsDoctorWorkingNullTimeSlot()
    {
        // 1. Precondiciones

        insertWorkday();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        boolean isDoctorWorking = workdayDao.doctorWorks(doctorModel(), null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testWorkdayIsDoctorWorkingNullBoth()
    {
        // 1. Precondiciones

        insertWorkday();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        boolean isDoctorWorking = workdayDao.doctorWorks(null, null);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    @Test
    public void testWorkdayIsDoctorWorkingNullDoctor()
    {
        // 1. Precondiciones

        insertWorkday();
        LocalDateTime startDate = new LocalDateTime(2020, 5, 25,START_HOUR, START_MINUTE); // 25/5/2020 es lunes
        AppointmentTimeSlot appointmentTimeSlot = new AppointmentTimeSlot();
        appointmentTimeSlot.setDate(startDate);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        boolean isDoctorWorking = workdayDao.doctorWorks(null, appointmentTimeSlot);

        // 3. Postcondiciones
        // Que tire NullPointerException
    }

    /* --------------------- MÉTODO: workdayDao.create(List<Workday>) -------------------------------------------- */

    @Test
    public void testCreateWorkdaysSuccessfully() {
        // 1. Precondiciones
        insertDoctor();
        List<Workday> ws = new ArrayList<>();
        for (int i=0; i<7; i++){
            Workday workday = workdayModel();
            workday.setId(null);
            ws.add(workday);
        }

        // 2. Ejercitar
        List<Workday> workdays = new ArrayList<>(this.workdayDao.create(ws));

        // 3. Postcondiciones
        assertEquals(7, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, WORKDAYS_TABLE));
        assertEquals(7, workdays.size());
        workdays.sort(Comparator.comparingInt(Workday::getId));
        assertEquals((int) workdays.get(0).getId(), STARTING_ID);
        assertEquals((int) workdays.get(6).getId(), STARTING_ID + 6);

    }

    @Test
    public void testCreateWorkdaysNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Workday> workdays = new ArrayList<>(this.workdayDao.create((List<Workday>) null));

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreateWorkdaysEmptyWorkdayFail()
    {
        // 1. Precondiciones
        List<Workday> workdays = new ArrayList<>();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        workdays = new ArrayList<>(this.workdayDao.create(workdays));

        // 3. Postcondiciones
        // Que el metodo tire PersistenceException
    }
}