package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.ProvinceDao;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.ModelMetadata;
import ar.edu.itba.paw.models.Province;
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
public class ProvinceDaoImplTest {
    private static final String PROVINCE = "Buenos Aires";
    private static final String PROVINCE_2 = "Corrientes";
    private static final int STARTING_ID = 1;
    private static final String COUNTRY = "Argentina";
    private static final String COUNTRY_ID = "AR";

    private static final String PROVINCES_TABLE = "system_province";
    private static final String COUNTRIES_TABLE = "system_country";

    @Autowired
    private ProvinceDao provinceDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert provinceJdbcInsert;
    private SimpleJdbcInsert countryJdbcInsert;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(this.ds);
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

    /** Inserta en la db la provincia con country_id=COUNTRY_ID y name=PROVINCE_2 **/
    private void insertAnotherProvince() {
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", COUNTRY_ID);
        map.put("name", PROVINCE_2);
        provinceJdbcInsert.execute(map);
    }

    /* --------------------- MÉTODO: provinceDao.create(Province) -------------------------------------------- */

    @Test
    public void testCreateProvinceSuccessfully()
    {
        // 1. Precondiciones
        insertCountry();
        Province p = provinceModel();
        p.setId(null);

        // 2. Ejercitar
        Province province = this.provinceDao.create(p);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, PROVINCES_TABLE));
        assertEquals(PROVINCE, province.getName());
        assertEquals(COUNTRY_ID, province.getCountry().getId());
        assertEquals(STARTING_ID, (int) province.getId());
    }

    @Test
    public void testCreateAnotherProvinceSuccessfully()
    {
        // 1. Precondiciones

        insertProvince();
        Province p = new Province();
        p.setName(PROVINCE_2);
        p.setCountry(countryModel());

        // 2. Ejercitar
        Province province = this.provinceDao.create(p);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, PROVINCES_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, COUNTRIES_TABLE));
        assertEquals(PROVINCE_2, province.getName());
        assertEquals(countryModel(), province.getCountry());
    }

    @Test
    public void testCreateProvinceNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Province province = this.provinceDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreateProvinceEmptyProvinceFail()
    {
        // 1. Precondiciones

        Province p = new Province();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Province province = this.provinceDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException (no name)
    }

    @Test
    public void testCreateProvinceEmptyCountryFail()
    {
        // 1. Precondiciones

        Province p = new Province();
        p.setName(PROVINCE);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Province province = this.provinceDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire PropertyValueException
    }

    @Test
        public void testCreateProvinceEmptyNameFail()
    {
        // 1. Precondiciones

        insertCountry();
        Province p = new Province();
        p.setCountry(countryModel());
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Province province = this.provinceDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    /* --------------------- MÉTODO: provinceDao.findById(int) -------------------------------------------- */

    @Test
    public void testFindProvinceById()
    {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();

        // 2. Ejercitar
        Optional<Province> province = this.provinceDao.findById(STARTING_ID);

        // 3. Postcondiciones
        assertTrue(province.isPresent());
        assertEquals(PROVINCE, province.get().getName());
    }

    @Test
    public void testFindProvinceByIdDoesntExist() {
        // 1. Precondiciones

        insertProvince();

        // 2. Ejercitar
        Optional<Province> province = this.provinceDao.findById(STARTING_ID+1);

        // 3. Postcondiciones
        assertFalse(province.isPresent());
    }

    @Test
    public void testFindProvinceByIdNull()
    {
        // 1. Precondiciones

        insertProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Province> province = this.provinceDao.findById(null);

        // 3. Postcondiciones
        assertFalse(province.isPresent());
    }

    /* --------------------- MÉTODO: provinceDao.findByIds(Collection<Integer>) -------------------------------------------- */

    @Test
    public void testFindProvincesByIds()
    {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();

        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID+1));

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertEquals(2, provinces.size());
        for (Province p : provinces){
            assertTrue(p.getId().equals(STARTING_ID) || p.getId().equals(STARTING_ID+1));
        }
    }

    @Test
    public void testFindProvincesByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertProvince();

        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID+1));

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertEquals(1, provinces.size());
        for (Province p : provinces){
            assertEquals(provinceModel(), p);
        }
    }

    @Test
    public void testFindProvincesByIdsDoesntExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID+1));

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    /* --------------------- MÉTODO: provinceDao.findByName(String) -------------------------------------------- */

    @Test
    public void testFindProvincesByName() {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        Map<String, Object> map = new HashMap<>();
        map.put("name", PROVINCE);
        map.put("country_id", COUNTRY_ID);
        provinceJdbcInsert.execute(map);

        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.findByName(PROVINCE);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertEquals(2, provinces.size());
        for (Province p : provinces){
            assertEquals(PROVINCE, p.getName());
        }
    }

    @Test
    public void testFindProvincesByNameDoesntExist() {
        // 1. Precondiciones

        insertProvince();

        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.findByName(PROVINCE_2);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    @Test
    public void testFindProvinceByNameNull()
    {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.findByName(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testFindProvinceByContainingName()
    {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        Map<String, Object> map = new HashMap<>();
        map.put("country_id", COUNTRY_ID);
        map.put("name", PROVINCE);
        provinceJdbcInsert.execute(map);


        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.findByName("buen");

        // 3. Postcondiciones
        assertEquals(2, provinces.size());
    }

    /* --------------------- MÉTODO: provinceDao.list() -------------------------------------------- */

    @Test
    public void testProvinceList()
    {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();

        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.list();

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertEquals(2, provinces.size());
    }

    @Test
    public void testProvincesEmptyList() {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Province> provinces = this.provinceDao.list();

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    /* --------------------- MÉTODO: provinceDao.update(Province) -------------------------------------------- */

    @Test
    public void testProvinceUpdate() {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        Province p = provinceModel();
        p.setName("Buen Ayre");

        // 2. Ejercitar
        this.provinceDao.update(p);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, PROVINCES_TABLE, "name = 'Buen Ayre'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, PROVINCES_TABLE, "name = '" + PROVINCE + "'"));

    }

    @Test
    public void testProvinceUpdateNull()
    {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    @Test
    public void testProvinceUpdateNotExistentProvince()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherProvince();
        Province p = provinceModel();
        p.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    @Test
    public void testProvinceUpdateProvinceWithNullName()
    {
        // 1. Precondiciones

        insertProvince();
        Province p = new Province();
        p.setCountry(countryModel());
        p.setId(STARTING_ID);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.provinceDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
        assertEquals(provinceModel(), p);
    }

    @Test
    public void testProvinceUpdateProvinceWithNullId()
    {
        // 1. Precondiciones

        insertProvince();
        Province p = provinceModel();
        p.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, COUNTRIES_TABLE));
    }
    /* --------------------- MÉTODO: provinceDao.remove(int id) -------------------------------------------- */

    @Test
    public void testProvinceRemoveById()
    {
        // 1. Precondiciones

        insertProvince();

        // 2. Ejercitar
        this.provinceDao.remove(STARTING_ID);

        // 3. Postcondiciones
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    @Test
    public void testProvinceRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.remove(STARTING_ID + 1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    @Test
    public void testProvinceRemoveByNullId()
    {
        // 1. Precondiciones

        insertProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    /* --------------------- MÉTODO: provinceDao.remove(Province) -------------------------------------------- */

    @Test
    public void testProvinceRemoveByModel()
    {
        // 1. Precondiciones

        insertProvince();
        Province p = provinceModel();

        // 2. Ejercitar
        this.provinceDao.remove(p);

        // 3. Postcondiciones
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    @Test
    public void testProvinceRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertCountry();
        insertAnotherProvince();
        Province p = provinceModel();
        p.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.remove(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    @Test
    public void testProvinceRemoveByNullModel()
    {
        // 1. Precondiciones

        insertProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.provinceDao.remove((Province) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PROVINCES_TABLE));
    }

    /* --------------------- MÉTODO: countryDao.count() -------------------------------------------- */

    @Test
    public void testProvinceCount()
    {
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.provinceDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testProvinceCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.provinceDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }

    /* --------------------- MÉTODO: countryDao.findByCountry(Country) -------------------------------------------- */

    @Test
    public void testProvinceFindByCountry(){
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();

        Map<String, Object> countryMap = new HashMap<>();
        countryMap.put("country_id", "BR");
        countryMap.put("name", "Brasil");
        countryJdbcInsert.execute(countryMap);

        Map<String, Object> map = new HashMap<>();
        map.put("country_id", "BR");
        map.put("name", PROVINCE_2);
        provinceJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountry(countryModel());

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertEquals(2, provinces.size());
    }

    @Test
    public void testProvinceFindByCountryDoesntExists(){
        // 1. Precondiciones


        Map<String, Object> countryMap = new HashMap<>();
        countryMap.put("country_id", "BR");
        countryMap.put("name", "Brasil");
        countryJdbcInsert.execute(countryMap);

        Map<String, Object> map = new HashMap<>();
        map.put("country_id", "BR");
        map.put("name", PROVINCE_2);
        provinceJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountry(countryModel());

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    @Test
    public void testProvinceFindByCountryNull(){
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountry(null);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    /* --------------------- MÉTODO: provinceDao.findByCountryAndName(Country, String) -------------------------------------------- */

    @Test
    public void testProvinceFindByCountryAndName(){
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();

        Map<String, Object> countryMap = new HashMap<>();
        countryMap.put("country_id", "BR");
        countryMap.put("name", "Brasil");
        countryJdbcInsert.execute(countryMap);

        Map<String, Object> map = new HashMap<>();
        map.put("country_id", "BR");
        map.put("name", PROVINCE_2);
        provinceJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountryAndName(countryModel(), PROVINCE_2);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertEquals(1, provinces.size());
        for (Province p: provinces){
            assertEquals(PROVINCE_2, p.getName());
            assertEquals(countryModel(), p.getCountry());
        }
    }

    @Test
    public void testProvinceFindByCountryAndNameCountryDoesntExists(){
        // 1. Precondiciones


        Map<String, Object> countryMap = new HashMap<>();
        countryMap.put("country_id", "BR");
        countryMap.put("name", "Brasil");
        countryJdbcInsert.execute(countryMap);

        Map<String, Object> map = new HashMap<>();
        map.put("country_id", "BR");
        map.put("name", PROVINCE_2);
        provinceJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountryAndName(countryModel(), PROVINCE_2);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    @Test
    public void testProvinceFindByCountryAndNameCountryNull(){
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountryAndName(null, PROVINCE);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    @Test
    public void testProvinceFindByCountryAndNameNameDoesntExists(){
        // 1. Precondiciones


        Map<String, Object> countryMap = new HashMap<>();
        countryMap.put("country_id", "BR");
        countryMap.put("name", "Brasil");
        countryJdbcInsert.execute(countryMap);

        Map<String, Object> map = new HashMap<>();
        map.put("country_id", "BR");
        map.put("name", PROVINCE_2);
        provinceJdbcInsert.execute(map);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountryAndName(countryModel(), PROVINCE);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    @Test
    public void testProvinceFindByCountryAndNameNameNull(){
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountryAndName(countryModel(), null);

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertTrue(provinces.isEmpty());
    }

    @Test
    public void testProvinceFindByCountryAndNameNameContains(){
        // 1. Precondiciones

        insertProvince();
        insertAnotherProvince();

        // 2. Ejercitar
        List<Province> provinces = this.provinceDao.findByCountryAndName(countryModel(), "corr");

        // 3. Postcondiciones
        assertNotNull(provinces);
        assertEquals(1, provinces.size());
        for (Province p: provinces){
            assertEquals(PROVINCE_2, p.getName());
            assertEquals(countryModel(), p.getCountry());
        }
    }

}
