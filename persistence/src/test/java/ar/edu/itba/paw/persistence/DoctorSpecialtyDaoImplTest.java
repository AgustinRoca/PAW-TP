package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.DoctorSpecialtyDao;
import ar.edu.itba.paw.models.ModelMetadata;
import ar.edu.itba.paw.models.DoctorSpecialty;
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
public class DoctorSpecialtyDaoImplTest {
    private static final int STARTING_ID = 1;
    private static final String DOCTOR_SPECIALTY = "Oftalmologo";
    private static final Object DOCTOR_SPECIALTY_2 = "Dentista";
    
    private static final String DOCTOR_SPECIALTIES_TABLE = "system_doctor_specialty";

    @Autowired
    private DoctorSpecialtyDao doctorSpecialtyDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert doctorSpecialtyInsert;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(this.ds);
        this.doctorSpecialtyInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(DOCTOR_SPECIALTIES_TABLE)
                .usingGeneratedKeyColumns("speciality_id");
        cleanAllTables();
    }

    /* ---------------------- FUNCIONES AUXILIARES ---------------------------------------------------------------- */

    private void cleanAllTables() {
        this.jdbcTemplate.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
    }

    /** Devuelve una DoctorSpecialty con name=DOCTOR_SPECIALTY **/
    private DoctorSpecialty doctorSpecialtyModel() {
        DoctorSpecialty ss = new DoctorSpecialty();
        ss.setName(DOCTOR_SPECIALTY);
        ss.setId(STARTING_ID);
        return ss;
    }

    /** Inserta en la db la imagen con name=DOCTOR_SPECIALTY **/
    private void insertDoctorSpecialty() {
        Map<String, Object> ssMap = new HashMap<>();
        ssMap.put("name", DOCTOR_SPECIALTY);
        doctorSpecialtyInsert.execute(ssMap);
    }

    /** Inserta en la db la imagen con name=DOCTOR_SPECIALTY_2 **/
    private void insertAnotherDoctorSpecialty() {
        Map<String, Object> ssMap = new HashMap<>();
        ssMap.put("name", DOCTOR_SPECIALTY_2);
        doctorSpecialtyInsert.execute(ssMap);
    }
    /* --------------------- MÉTODO: doctorSpecialtyDao.create(DoctorSpecialty) -------------------------------------------- */

    @Test
    public void testCreateDoctorSpecialtySuccessfully() {
        // 1. Precondiciones

        DoctorSpecialty ss = doctorSpecialtyModel();

        // 2. Ejercitar
        ss.setId(null);
        DoctorSpecialty doctorSpecialty = this.doctorSpecialtyDao.create(ss);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
        assertEquals(DOCTOR_SPECIALTY, doctorSpecialty.getName());
    }

    @Test
    public void testCreateAnotherDoctorSpecialtySuccessfully() {
        // 1. Precondiciones

        insertAnotherDoctorSpecialty();
        DoctorSpecialty ss = doctorSpecialtyModel();
        ss.setId(null);

        // 2. Ejercitar
        DoctorSpecialty doctorSpecialty = this.doctorSpecialtyDao.create(ss);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
        assertEquals(DOCTOR_SPECIALTY, doctorSpecialty.getName());
    }

    @Test
    public void testCreateDoctorSpecialtyNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        DoctorSpecialty doctorSpecialty = this.doctorSpecialtyDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreateDoctorSpecialtyEmptyDoctorSpecialtyFail()
    {
        // 1. Precondiciones

        DoctorSpecialty ss = new DoctorSpecialty();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        DoctorSpecialty doctorSpecialty = this.doctorSpecialtyDao.create(ss);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException (no data) o DataIntegrityViolationException (no id), se hace esto porque depende de cual chequea primero.
    }

    @Test
    public void testCreateDoctorSpecialtyEmptyNameFail()
    {
        // 1. Precondiciones

        DoctorSpecialty ss = doctorSpecialtyModel();
        ss.setName(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        DoctorSpecialty doctorSpecialty = this.doctorSpecialtyDao.create(ss);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    /* --------------------- MÉTODO: doctorSpecialtyDao.findById(String) -------------------------------------------- */

    @Test
    public void testFindDoctorSpecialtyById()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        insertAnotherDoctorSpecialty();

        // 2. Ejercitar
        Optional<DoctorSpecialty> doctorSpecialty = this.doctorSpecialtyDao.findById(STARTING_ID);

        // 3. Postcondiciones
        assertTrue(doctorSpecialty.isPresent());
        assertEquals(DOCTOR_SPECIALTY, doctorSpecialty.get().getName());
    }

    @Test
    public void testFindDoctorSpecialtyByIdDoesntExist()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();

        // 2. Ejercitar
        Optional<DoctorSpecialty> doctorSpecialty = this.doctorSpecialtyDao.findById(STARTING_ID + 1);

        // 3. Postcondiciones
        assertFalse(doctorSpecialty.isPresent());
    }

    @Test
    public void testFindDoctorSpecialtyByIdNull()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<DoctorSpecialty> doctorSpecialty = this.doctorSpecialtyDao.findById(null);

        // 3. Postcondiciones
        assertFalse(doctorSpecialty.isPresent());
    }

    /* --------------------- MÉTODO: doctorSpecialtyDao.findByIds(Collection<String>) -------------------------------------------- */

    @Test
    public void testFindDoctorSpecialtyByIds()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        insertAnotherDoctorSpecialty();

        // 2. Ejercitar
        Collection<DoctorSpecialty> doctorSpecialties = this.doctorSpecialtyDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(doctorSpecialties);
        assertEquals(2, doctorSpecialties.size());
        for (DoctorSpecialty ss : doctorSpecialties){
            assertTrue(ss.getId().equals(STARTING_ID) || ss.getId().equals(STARTING_ID + 1));
        }
    }

    @Test
    public void testFindDoctorSpecialtyByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();

        // 2. Ejercitar
        Collection<DoctorSpecialty> doctorSpecialties = this.doctorSpecialtyDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(doctorSpecialties);
        assertEquals(1, doctorSpecialties.size());
        for (DoctorSpecialty ss : doctorSpecialties){
            assertEquals(doctorSpecialtyModel(), ss);
        }
    }

    @Test
    public void testFindDoctorSpecialtyByIdsDontExist()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<DoctorSpecialty> doctorSpecialties = this.doctorSpecialtyDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(doctorSpecialties);
        assertTrue(doctorSpecialties.isEmpty());
    }

    /* --------------------- MÉTODO: doctorSpecialtyDao.list() -------------------------------------------- */

    @Test
    public void testDoctorSpecialtyList()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        insertAnotherDoctorSpecialty();

        // 2. Ejercitar
        Collection<DoctorSpecialty> doctorSpecialties = this.doctorSpecialtyDao.list();

        // 3. Postcondiciones
        assertNotNull(doctorSpecialties);
        assertEquals(2, doctorSpecialties.size());
    }

    @Test
    public void testDoctorSpecialtyEmptyList()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<DoctorSpecialty> doctorSpecialties = this.doctorSpecialtyDao.list();

        // 3. Postcondiciones
        assertNotNull(doctorSpecialties);
        assertTrue(doctorSpecialties.isEmpty());
    }

    /* --------------------- MÉTODO: doctorSpecialtyDao.update(DoctorSpecialty) -------------------------------------------- */

    @Test
    public void testDoctorSpecialtyUpdate()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        insertAnotherDoctorSpecialty();
        DoctorSpecialty ss = doctorSpecialtyModel();
        ss.setName("Armenia");

        // 2. Ejercitar
        this.doctorSpecialtyDao.update(ss);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE, "name = 'Armenia'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE, "name = '"+ DOCTOR_SPECIALTY +"'"));
    }

    @Test
    public void testDoctorSpecialtyUpdateNull()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        insertAnotherDoctorSpecialty();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    @Test
    public void testDoctorSpecialtyUpdateNotExistentDoctorSpecialty()
    {
        // 1. Precondiciones

        insertAnotherDoctorSpecialty();
        DoctorSpecialty ss = doctorSpecialtyModel();
        ss.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.update(ss);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    @Test
    public void testDoctorSpecialtyUpdateDoctorSpecialtyWithNullName()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        DoctorSpecialty ss = doctorSpecialtyModel();
        ss.setName(null);
        expectedException.expect(DataIntegrityViolationException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.update(ss);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
        assertEquals(doctorSpecialtyModel(), ss);
    }

    @Test
    public void testDoctorSpecialtyUpdateDoctorSpecialtyWithNullId()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        DoctorSpecialty ss = doctorSpecialtyModel();
        ss.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.update(ss);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    /* --------------------- MÉTODO: doctorSpecialtyDao.remove(String id) -------------------------------------------- */

    @Test
    public void testDoctorSpecialtyRemoveById()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();

        // 2. Ejercitar
        this.doctorSpecialtyDao.remove(STARTING_ID);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    @Test
    public void testDoctorSpecialtyRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.remove(STARTING_ID + 1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    @Test
    public void testDoctorSpecialtyRemoveByNullId()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }
    /* --------------------- MÉTODO: doctorSpecialtyDao.remove(DoctorSpecialty) -------------------------------------------- */

    @Test
    public void testDoctorSpecialtyRemoveByModel()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        DoctorSpecialty ss = doctorSpecialtyModel();

        // 2. Ejercitar
        this.doctorSpecialtyDao.remove(ss);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    @Test
    public void testDoctorSpecialtyRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertAnotherDoctorSpecialty();
        DoctorSpecialty ss = doctorSpecialtyModel();
        ss.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.remove(ss);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    @Test
    public void testDoctorSpecialtyRemoveByNullModel()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.doctorSpecialtyDao.remove((DoctorSpecialty) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, DOCTOR_SPECIALTIES_TABLE));
    }

    /* --------------------- MÉTODO: doctorSpecialtyDao.count() -------------------------------------------- */

    @Test
    public void testDoctorSpecialtyCount()
    {
        // 1. Precondiciones

        insertDoctorSpecialty();
        insertAnotherDoctorSpecialty();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.doctorSpecialtyDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testDoctorSpecialtyCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.doctorSpecialtyDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }
}