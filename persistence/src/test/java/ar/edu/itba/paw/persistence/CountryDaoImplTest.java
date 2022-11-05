package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.CountryDao;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.ModelMetadata;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Sql("classpath:sql/schema.sql")
@ContextConfiguration(classes = TestConfig.class)
public class CountryDaoImplTest {
    private static final String COUNTRY = "Argentina";
    private static final String COUNTRY_ID = "AR";

    private static final String COUNTRY_2 = "Brasil";
    private static final String COUNTRY_ID_2 = "BR";

    private static final String COUNTRIES_TABLE = "system_country";

    @Autowired
    private CountryDao countryDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(this.ds);
        this.jdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(COUNTRIES_TABLE);
        cleanAllTables();
    }

    /* --------------------- FUNCIONES AUXILIARES -------------------------------------------- */

    private void cleanAllTables(){
        this.jdbcTemplate.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
    }

    /** Devuelve un country con id=COUNTRY_ID y name=COUNTRY **/
    private Country countryModel(){
        Country c = new Country();
        c.setName(COUNTRY);
        c.setId(COUNTRY_ID);
        return c;
    }


    /** Inserta en la db el pais con id=COUNTRY_ID y name=COUNTRY **/
    private void insertCountry() {
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", COUNTRY_ID);
        map.put("name", COUNTRY);
        jdbcInsert.execute(map);
    }

    /** Inserta en la db el pais con id=COUNTRY_ID_2 y name=COUNTRY_2 **/
    private void insertAnotherCountry() {
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", COUNTRY_ID_2);
        map.put("name", COUNTRY_2);
        jdbcInsert.execute(map);
    }

    /* --------------------- MÉTODO: countryDao.create(Country) -------------------------------------------- */

    @Test
    public void testCreateCountrySuccessfully()
    {
        // 1. Precondiciones

        Country c = countryModel();

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, COUNTRIES_TABLE));
        assertEquals(COUNTRY, country.getName());
        assertEquals(COUNTRY_ID, country.getId());
    }

    @Test
    public void testCreateAnotherCountrySuccessfully()
    {
        // 1. Precondiciones

        insertCountry();
        Country c = new Country();
        c.setName(COUNTRY_2);
        c.setId(COUNTRY_ID_2);

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, COUNTRIES_TABLE));
        assertEquals(COUNTRY_2, country.getName());
        assertEquals(COUNTRY_ID_2, country.getId());
    }

    @Test
    public void testCreateCountryRepeatedKeyFail()
    {
        // 1. Precondiciones

        insertCountry();
        Country c = countryModel();
        expectedException.expect(DataIntegrityViolationException.class);


        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        // Que el metodo tire DuplicateKeyException
    }

    @Test
    public void testCreateCountryNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Country country = this.countryDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreateCountryEmptyCountryFail()
    {
        // 1. Precondiciones

        Country c = new Country();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException (no name) o DataIntegrityViolationException (no id), se hace esto porque depende de cual chequea primero.
    }

    @Test
    public void testCreateCountryEmptyIdFail()
    {
        // 1. Precondiciones

        Country c = new Country();
        c.setName(COUNTRY);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        // Que el metodo tire DataIntegrityViolationException
    }

    @Test
    public void testCreateCountryEmptyNameFail()
    {
        // 1. Precondiciones

        Country c = new Country();
        c.setId(COUNTRY_ID);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    @Test
    public void testCreateCountryTooLongIdFail()
    {
        // 1. Precondiciones

        Country c = new Country();
        c.setName(COUNTRY);
        c.setId("CCC"); // Los countries IDs deben tener 2 caracteres
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        // Que el metodo tire DataIntegrityViolationException
    }

    @Test
    public void testCreateCountryTooShortIdFail()
    {
        // 1. Precondiciones

        Country c = new Country();
        c.setName(COUNTRY);
        c.setId("C"); // Los countries IDs deben tener 2 caracteres
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        // Que el metodo tire DataIntegrityViolationException
    }

    @Test
    public void testCreateCountryInvalidCharInIdFail()
    {
        // 1. Precondiciones

        Country c = new Country();
        c.setName(COUNTRY);
        c.setId("C2"); // Los countries IDs deben tener 2 caracteres
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        // Que el metodo tire DataIntegrityViolationException
    }

    @Test
    public void testCreateCountryCaseInsensitiveId()
    {
        // 1. Precondiciones

        Country c = new Country();
        c.setName(COUNTRY);
        c.setId(COUNTRY_ID.toLowerCase());

        // 2. Ejercitar
        Country country = this.countryDao.create(c);

        // 3. Postcondiciones
        assertEquals(COUNTRY_ID, country.getId());
    }

    /* --------------------- MÉTODO: countryDao.findById(String) -------------------------------------------- */

    @Test
    public void testFindCountryById()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();

        // 2. Ejercitar
        Optional<Country> country = this.countryDao.findById(COUNTRY_ID);

        // 3. Postcondiciones
        assertTrue(country.isPresent());
        assertEquals(COUNTRY, country.get().getName());
    }

    @Test
    public void testFindCountryByIdDoesntExist() {
        // 1. Precondiciones

        insertCountry();
        
        // 2. Ejercitar
        Optional<Country> country = this.countryDao.findById(COUNTRY_ID_2);

        // 3. Postcondiciones
        assertFalse(country.isPresent());
    }

    @Test
    public void testFindCountryByIdNull()
    {
        // 1. Precondiciones

        insertCountry();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Country> country = this.countryDao.findById(null);

        // 3. Postcondiciones
        assertFalse(country.isPresent());
    }

    @Test
    public void testFindCountryByIdInvalidId()
    {
        // 1. Precondiciones

        insertCountry();

        // 2. Ejercitar
        Optional<Country> country = this.countryDao.findById("CCC");

        // 3. Postcondiciones
        assertFalse(country.isPresent());
    }

    /* --------------------- MÉTODO: countryDao.findByIds(Collection<String>) -------------------------------------------- */

    @Test
    public void testFindCountryByIds()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();

        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.findByIds(Arrays.asList(COUNTRY_ID, COUNTRY_ID_2));

        // 3. Postcondiciones
        assertNotNull(countries);
        assertEquals(2, countries.size());
        for (Country c : countries){
            assertTrue(c.getId().equals(COUNTRY_ID) || c.getId().equals(COUNTRY_ID_2));
        }
    }

    @Test
    public void testFindCountryByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertCountry();

        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.findByIds(Arrays.asList(COUNTRY_ID, COUNTRY_ID_2));

        // 3. Postcondiciones
        assertNotNull(countries);
        assertEquals(1, countries.size());
        for (Country c : countries){
            assertEquals(countryModel(), c);
        }
    }

    @Test
    public void testFindCountryByIdsDontExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.findByIds(Arrays.asList(COUNTRY_ID, COUNTRY_ID_2));

        // 3. Postcondiciones
        assertNotNull(countries);
        assertTrue(countries.isEmpty());
    }

    /* --------------------- MÉTODO: countryDao.findByName(String) -------------------------------------------- */

    @Test
    public void testFindCountryByName() {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", "AT");
        map.put("name", COUNTRY);
        jdbcInsert.execute(map);

        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.findByName(COUNTRY);

        // 3. Postcondiciones
        assertNotNull(countries);
        assertEquals(2, countries.size());
        for (Country c : countries){
            assertEquals(COUNTRY, c.getName());
        }
    }

    @Test
    public void testFindCountryByNameDoesntExist() {
        // 1. Precondiciones

        insertCountry();

        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.findByName(COUNTRY_2);

        // 3. Postcondiciones
        assertNotNull(countries);
        assertTrue(countries.isEmpty());
    }

    @Test
    public void testFindCountryByNameNull()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.countryDao.findByName(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testFindCountryByContainingName()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", "AT");
        map.put("name", COUNTRY);
        jdbcInsert.execute(map);


        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.findByName("Arg");

        // 3. Postcondiciones
        assertEquals(2, countries.size());
    }

    /* --------------------- MÉTODO: countryDao.list() -------------------------------------------- */

    @Test
    public void testCountryList()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();

        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.list();

        // 3. Postcondiciones
        assertNotNull(countries);
        assertEquals(2, countries.size());
    }

    @Test
    public void testCountryEmptyList() {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Country> countries = this.countryDao.list();

        // 3. Postcondiciones
        assertNotNull(countries);
        assertTrue(countries.isEmpty());
    }

    /* --------------------- MÉTODO: countryDao.update(Country) -------------------------------------------- */

    @Test
    public void testCountryUpdate() {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();
        Country c = countryModel();
        c.setName("Armenia");

        // 2. Ejercitar
        this.countryDao.update(c);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, COUNTRIES_TABLE, "name = 'Armenia'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, COUNTRIES_TABLE, "name = '"+ COUNTRY +"'"));
    }

    @Test
    public void testCountryUpdateNull()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.countryDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    @Test
    public void testCountryUpdateNotExistentCountry()
    {
        // 1. Precondiciones

        insertAnotherCountry();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.countryDao.update(countryModel());

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    @Test
    public void testCountryUpdateCountryWithNullName()
    {
        // 1. Precondiciones

        insertCountry();
        Country c = new Country();
        c.setId(COUNTRY_ID);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.countryDao.update(c);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
        assertEquals(countryModel(), c);
    }

    @Test
    public void testCountryUpdateCountryWithNullId()
    {
        // 1. Precondiciones

        insertCountry();
        Country c = new Country();
        c.setName(COUNTRY);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.countryDao.update(c);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    /* --------------------- MÉTODO: countryDao.remove(String id) -------------------------------------------- */

    @Test
    public void testCountryRemoveById()
    {
        // 1. Precondiciones

        insertCountry();

        // 2. Ejercitar
        this.countryDao.remove(COUNTRY_ID);

        // 3. Postcondiciones
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    @Test
    public void testCountryRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertCountry();

        // 2. Ejercitar
        expectedException.expect(IllegalArgumentException.class);
        this.countryDao.remove(COUNTRY_ID_2);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    @Test
    public void testCountryRemoveByNullId()
    {
        // 1. Precondiciones

        insertCountry();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.countryDao.remove((String) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    /* --------------------- MÉTODO: countryDao.remove(Country) -------------------------------------------- */

    @Test
    public void testCountryRemoveByModel()
    {
        // 1. Precondiciones

        insertCountry();
        Country c = countryModel();

        // 2. Ejercitar
        this.countryDao.remove(c);

        // 3. Postcondiciones
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    @Test
    public void testCountryRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertAnotherCountry();

        // 2. Ejercitar
        expectedException.expect(IllegalArgumentException.class);
        this.countryDao.remove(countryModel());

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    @Test
    public void testCountryRemoveByNullModel()
    {
        // 1. Precondiciones

        insertCountry();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.countryDao.remove((Country) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }

    /* --------------------- MÉTODO: countryDao.count() -------------------------------------------- */

    @Test
    public void testCountryCount()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherCountry();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.countryDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testCountryCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.countryDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }
}