package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.OfficeDao;
import ar.edu.itba.paw.models.*;
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
@Sql(scripts = "classpath:sql/schema.sql")
@ContextConfiguration(classes = TestConfig.class)
public class OfficeDaoImplTest
{
    private static final int STARTING_ID = 1 ;
    private static final String OFFICE = "Hospital Nacional";
    private static final String OFFICE_2 = "Sanatorio Provincial";
    private static final String STREET = "Av 9 de Julio 123";
    private static final String LOCALITY = "Capital Federal";
    private static final String PROVINCE = "Buenos Aires";
    private static final String COUNTRY = "Argentina";
    private static final String COUNTRY_ID = "AR";
    private static final String PHONE = "1234567890";
    private static final String EMAIL = "test@test.com";
    private static final String URL = "www.hnacional.com";

    private static final String OFFICES_TABLE = "office";
    private static final String LOCALITIES_TABLE = "system_locality";
    private static final String PROVINCES_TABLE = "system_province";
    private static final String COUNTRIES_TABLE = "system_country";

    @Autowired
    private OfficeDao officeDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert officeJdbcInsert;
    private SimpleJdbcInsert localityJdbcInsert;
    private SimpleJdbcInsert provinceJdbcInsert;
    private SimpleJdbcInsert countryJdbcInsert;

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
        cleanAllTables();
    }

    /* ---------------------- FUNCIONES AUXILIARES ---------------------------------------------------------------- */

    private void cleanAllTables(){
        this.jdbcTemplate.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
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
        o.setEmail(EMAIL);
        o.setPhone(PHONE);
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
        officeMap.put("email", EMAIL);
        officeMap.put("phone", PHONE);
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
        officeMap.put("email", EMAIL);
        officeMap.put("phone", PHONE);
        officeMap.put("locality_id", STARTING_ID); // Identity de HSQLDB empieza en 0
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);
    }

    /* --------------------- MÉTODO: officeDao.create(Office) -------------------------------------------- */

    @Test
    public void testCreateOfficeSuccessfully()
    {
        // 1. Precondiciones
        insertLocality();
        Office o = officeModel();

        // 2. Ejercitar
        o.setId(null);
        Office office = this.officeDao.create(o);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, OFFICES_TABLE));
        assertEquals(OFFICE, office.getName());
        assertEquals(localityModel(), office.getLocality());
        assertEquals(PHONE, office.getPhone());
        assertEquals(STREET, office.getStreet());
        assertEquals(EMAIL, office.getEmail());
        assertEquals(URL, office.getUrl());
    }

    @Test
    public void testCreateAnotherOfficeSuccessfully()
    {
        // 1. Precondiciones

        insertOffice();
        Office o = officeModel();
        o.setName(OFFICE_2);
        o.setId(null);

        // 2. Ejercitar
        Office office = this.officeDao.create(o);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, OFFICES_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, LOCALITIES_TABLE));
        assertEquals(OFFICE_2, office.getName());
        assertEquals(localityModel(), office.getLocality());
        assertEquals(PHONE, office.getPhone());
        assertEquals(STREET, office.getStreet());
        assertEquals(EMAIL, office.getEmail());
        assertEquals(URL, office.getUrl());
    }

    @Test
    public void testCreateOfficeNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Office office = this.officeDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreateOfficeEmptyOfficeFail()
    {
        // 1. Precondiciones

        Office o = new Office();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Office office = this.officeDao.create(o);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException (no name)
    }

    @Test
    public void testCreateOfficeEmptyLocalityFail()
    {
        // 1. Precondiciones

        Office o = new Office();
        o.setName(OFFICE);
        expectedException.expect(PersistenceException.class);


        // 2. Ejercitar
        Office office = this.officeDao.create(o);

        // 3. Postcondiciones
        // Que el metodo tire DataIntegrityViolationException
    }

    @Test
    public void testCreateLocalityEmptyNameFail()
    {
        // 1. Precondiciones

        insertLocality();
        Office o = new Office();
        o.setLocality(localityModel());
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Office office = this.officeDao.create(o);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    /* --------------------- MÉTODO: officeDao.findById(int) -------------------------------------------- */

    @Test
    public void testFindOfficeById()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();

        // 2. Ejercitar
        Optional<Office> office = this.officeDao.findById(STARTING_ID);

        // 3. Postcondiciones
        assertTrue(office.isPresent());
        assertEquals(OFFICE, office.get().getName());
    }

    @Test
    public void testFindOfficeByIdDoesntExist()
    {
        // 1. Precondiciones

        insertOffice();

        // 2. Ejercitar
        Optional<Office> office = this.officeDao.findById(STARTING_ID + 1);

        // 3. Postcondiciones
        assertFalse(office.isPresent());
    }

    @Test
    public void testFindOfficeByIdNull()
    {
        // 1. Precondiciones

        insertLocality();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Office> office = this.officeDao.findById(null);

        // 3. Postcondiciones
        assertFalse(office.isPresent());
    }

    /* --------------------- MÉTODO: officeDao.findByIds(Collection<Integer>) -------------------------------------------- */

    @Test
    public void testFindOfficesByIds()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();

        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(offices);
        assertEquals(2, offices.size());
        for (Office o : offices){
            assertTrue(o.getId().equals(STARTING_ID) || o.getId().equals(STARTING_ID+1));
        }
    }

    @Test
    public void testFindOfficesByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertOffice();

        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID+1));

        // 3. Postcondiciones
        assertNotNull(offices);
        assertEquals(1, offices.size());
        for (Office o : offices){
            assertEquals(officeModel(), o);
        }
    }

    @Test
    public void testFindOfficesByIdsDoesntExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID+1));

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    /* --------------------- MÉTODO: officeDao.findByName(String) -------------------------------------------- */

    @Test
    public void testFindOfficesByName()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();

        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.findByName(OFFICE);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertEquals(1, offices.size());
        for (Office o : offices){
            assertEquals(OFFICE, o.getName());
        }
    }

    @Test
    public void testFindOfficesByNameDoesntExist()
    {
        // 1. Precondiciones

        insertOffice();

        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.findByName(OFFICE_2);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    @Test
    public void testFindOfficeByNameNull()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.officeDao.findByName(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testFindOfficeByContainingName()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();
        Map<String, Object> map = new HashMap<>();
        map.put("locality_id", STARTING_ID);
        map.put("name", OFFICE);
        map.put("street", STREET);
        officeJdbcInsert.execute(map);

        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.findByName("hosp");

        // 3. Postcondiciones
        assertEquals(2, offices.size());
    }

    /* --------------------- MÉTODO: officeDao.list() -------------------------------------------- */

    @Test
    public void testOfficeList()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();

        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.list();

        // 3. Postcondiciones
        assertNotNull(offices);
        assertEquals(2, offices.size());
    }

    @Test
    public void testOfficesEmptyList()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Office> offices = this.officeDao.list();

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    /* --------------------- MÉTODO: officeDao.update(Office) -------------------------------------------- */


    @Test
    public void testOfficeUpdate()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();
        Office o = officeModel();
        o.setName("Consultorio");

        // 2. Ejercitar
        this.officeDao.update(o);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFICES_TABLE, "name = 'Consultorio'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFICES_TABLE, "name = '" + OFFICE + "'"));

    }

    @Test
    public void testOfficeUpdateNull()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.officeDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testOfficeUpdateNotExistentOffice()
    {
        // 1. Precondiciones

        insertLocality();
        insertAnotherOffice();
        Office o = officeModel();
        o.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.officeDao.update(o);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    @Test
    public void testOfficeUpdateOfficeWithNullName()
    {
        // 1. Precondiciones

        insertOffice();
        Office o = officeModel();
        o.setName(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.officeDao.update(o);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testOfficeUpdateOfficeWithNullId()
    {
        // 1. Precondiciones

        insertOffice();
        Office o = officeModel();
        o.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.officeDao.update(o);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testOfficeUpdateOfficeWithNullStreet()
    {
        // 1. Precondiciones

        insertOffice();
        Office o = officeModel();
        o.setStreet(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.officeDao.update(o);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testOfficeUpdateOfficeWithNullNotRequired()
    {
        // 1. Precondiciones

        insertOffice();
        Office o = officeModel();
        o.setEmail(null);
        o.setPhone(null);
        o.setUrl(null);

        // 2. Ejercitar
        this.officeDao.update(o);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    /* --------------------- MÉTODO: officeDao.remove(int id) -------------------------------------------- */

    @Test
    public void testOfficeRemoveById()
    {
        // 1. Precondiciones

        insertOffice();

        // 2. Ejercitar
        this.officeDao.remove(STARTING_ID);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testLocalityRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertOffice();

        // 2. Ejercitar
        expectedException.expect(IllegalArgumentException.class);
        this.officeDao.remove(STARTING_ID + 1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testLocalityRemoveByNullId()
    {
        // 1. Precondiciones

        insertOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.officeDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    /* --------------------- MÉTODO: officeDao.remove(Office) -------------------------------------------- */

    @Test
    public void testLocalityRemoveByModel()
    {
        // 1. Precondiciones

        insertOffice();
        Office o = officeModel();

        // 2. Ejercitar
        this.officeDao.remove(o);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testOfficeRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertLocality();
        insertAnotherOffice();
        Office o = officeModel();
        o.setId(STARTING_ID + 1);

        // 2. Ejercitar
        expectedException.expect(IllegalArgumentException.class);
        this.officeDao.remove(o);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    @Test
    public void testOfficeRemoveByNullModel()
    {
        // 1. Precondiciones

        insertOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.officeDao.remove((Office) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFICES_TABLE));
    }

    /* --------------------- MÉTODO: countryDao.count() -------------------------------------------- */

    @Test
    public void testOfficeCount()
    {
        // 1. Precondiciones

        insertOffice();
        insertAnotherOffice();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.officeDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testOfficeCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.officeDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }

    /* --------------------- MÉTODO: officeDao.findByCountry(Country) -------------------------------------------- */
    @Test
    public void testFindByCountry(){
        // 1. Precondiciones


        insertOffice();

        // Insertar pais 2
        Map<String, Object> countryMap = new HashMap<>();
        countryMap.put("name", "Brasil");
        countryMap.put("country_id", "BR");
        countryJdbcInsert.execute(countryMap);

        // Insertar provincia 2
        Map<String, Object> provinceMap = new HashMap<>();
        provinceMap.put("name", "Brasilia");
        provinceMap.put("country_id", "BR");
        provinceJdbcInsert.execute(provinceMap);

        // Insertar localidad 2
        Map<String, Object> localityMap = new HashMap<>();
        localityMap.put("name", "Fabella");
        localityMap.put("province_id", STARTING_ID + 1);
        localityJdbcInsert.execute(localityMap);

        // Insertar oficina 2
        Map<String, Object> officeMap = new HashMap<>();
        officeMap.put("name", OFFICE_2);
        officeMap.put("email", EMAIL);
        officeMap.put("phone", PHONE);
        officeMap.put("locality_id", STARTING_ID + 1); // Identity de HSQLDB empieza en 0
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);

        Country c = countryModel();

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByCountry(c);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertEquals(1, offices.size());
    }

    @Test
    public void testFindByCountryDoesntExists() {
        // 1. Precondiciones

        insertOffice();

        //Crear Country modelo
        Country c = new Country();
        c.setName("Brasil");
        c.setId("BR");

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByCountry(c);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    @Test
    public void testFindByCountryCountryNull(){
        // 1. Precondiciones

        insertOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByCountry(null);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    /* --------------------- MÉTODO: officeDao.findByProvince(Province) -------------------------------------------- */
    @Test
    public void testFindByProvince(){
        // 1. Precondiciones


        insertOffice();

        // Insertar provincia 2
        Map<String, Object> provinceMap = new HashMap<>();
        provinceMap.put("name", "Brasilia");
        provinceMap.put("country_id", COUNTRY_ID);
        provinceJdbcInsert.execute(provinceMap);

        // Insertar localidad 2
        Map<String, Object> localityMap = new HashMap<>();
        localityMap.put("name", "Fabella");
        localityMap.put("province_id", STARTING_ID + 1);
        localityJdbcInsert.execute(localityMap);

        // Insertar oficina 2
        Map<String, Object> officeMap = new HashMap<>();
        officeMap.put("name", OFFICE_2);
        officeMap.put("email", EMAIL);
        officeMap.put("phone", PHONE);
        officeMap.put("locality_id", STARTING_ID + 1); // Identity de HSQLDB empieza en 0
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);

        Province p = provinceModel();

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByProvince(p);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertEquals(1, offices.size());
    }

    @Test
    public void testFindByProvinceDoesntExists(){
        // 1. Precondiciones

        insertOffice();

        //Crear Country modelo
        Province p = provinceModel();
        p.setId(STARTING_ID + 1);
        p.setName("Brasilia");

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByProvince(p);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    @Test
    public void testFindByProvinceProvinceNull(){
        // 1. Precondiciones

        insertOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByProvince(null);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    /* --------------------- MÉTODO: officeDao.findByLocality(Locality) -------------------------------------------- */
    @Test
    public void testFindByLocality(){
        // 1. Precondiciones


        insertOffice();

        // Insertar localidad 2
        Map<String, Object> localityMap = new HashMap<>();
        localityMap.put("name", "Fabella");
        localityMap.put("province_id", STARTING_ID);
        localityJdbcInsert.execute(localityMap);

        // Insertar oficina 2
        Map<String, Object> officeMap = new HashMap<>();
        officeMap.put("name", OFFICE_2);
        officeMap.put("email", EMAIL);
        officeMap.put("phone", PHONE);
        officeMap.put("locality_id", STARTING_ID + 1); // Identity de HSQLDB empieza en 0
        officeMap.put("street", STREET);
        officeMap.put("url", URL);
        officeJdbcInsert.execute(officeMap);

        Locality l = localityModel();

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByLocality(l);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertEquals(1, offices.size());
    }

    @Test
    public void testFindByLocalityDoesntExists(){
        // 1. Precondiciones

        insertOffice();

        //Crear Country modelo
        Locality l = localityModel();
        l.setId(STARTING_ID + 1);
        l.setName("Brasilia");

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByLocality(l);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }

    @Test
    public void testFindByLocalityLocalityNull(){
        // 1. Precondiciones

        insertOffice();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Collection<Office> offices = officeDao.findByLocality(null);

        // 3. Postcondiciones
        assertNotNull(offices);
        assertTrue(offices.isEmpty());
    }
}
