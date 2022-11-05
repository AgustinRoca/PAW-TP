package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.config.TestConfig;
import ar.edu.itba.paw.interfaces.daos.PictureDao;
import ar.edu.itba.paw.models.ModelMetadata;
import ar.edu.itba.paw.models.Picture;
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
public class PictureDaoImplTest {
    private static final int STARTING_ID = 1;
    private static final String MIME_TYPE = "image/svg+xml";
    private static final String PICTURE = "defaultProfilePic.svg";
    private static final Resource IMG = new ClassPathResource("img/" + PICTURE);
    private static final byte[] IMG_DATA = getImgData(IMG);
    private static final long IMG_SIZE = getImgSize(IMG);
    private static final String PICTURE_2 = "logo.svg";
    private static final Resource IMG_2 = new ClassPathResource("img/" + PICTURE);
    private static final byte[] IMG_2_DATA = getImgData(IMG_2);
    private static final long IMG_2_SIZE = getImgSize(IMG_2);

    private static final String PICTURES_TABLE = "picture";

    @Autowired
    private PictureDao pictureDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert pictureJdbcInsert;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private DataSource ds;

    @Before
    public void setUp(){
        this.jdbcTemplate = new JdbcTemplate(this.ds);
        this.pictureJdbcInsert = new SimpleJdbcInsert(this.ds)
                .withTableName(PICTURES_TABLE)
                .usingGeneratedKeyColumns("picture_id");
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

    private void cleanAllTables(){
        this.jdbcTemplate.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
    }

    /** Devuelve una Picture con
     * data = IMG_DATA
     * mimeType = MIME_TYPE
     * name = NAME
     * size = IMG_SIZE
     **/
    private Picture pictureModel(){
        Picture p = new Picture();
        try {
            p.setData(IMG_DATA);
            p.setMimeType(MIME_TYPE);
            p.setName(PICTURE);
            p.setSize(IMG_SIZE);
            p.setId(STARTING_ID);
        } catch (Exception e) {
            fail(e.getMessage());
        }
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
     * data = IMG_2_DATA
     * mimeType = MIME_TYPE
     * name = NAME_2
     * size = IMG_2_SIZE
     **/
    private void insertAnotherPicture() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", IMG_2_DATA);
        map.put("mime_type", MIME_TYPE);
        map.put("size", IMG_2_SIZE);
        map.put("name", PICTURE_2);
        pictureJdbcInsert.execute(map);
    }

    /* --------------------- MÉTODO: pictureDao.create(Picture) -------------------------------------------- */

    @Test
    public void testCreatePictureSuccessfully() {
        // 1. Precondiciones
        Picture p = pictureModel();
        p.setId(null);

        // 2. Ejercitar
        Picture picture = this.pictureDao.create(p);

        // 3. Postcondiciones
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, PICTURES_TABLE));
        assertEquals(MIME_TYPE, picture.getMimeType());
        assertEquals(PICTURE, picture.getName());
        assertEquals(IMG_SIZE, (long)picture.getSize());
        assertArrayEquals(IMG_DATA, picture.getData());
    }

    @Test
    public void testCreateAnotherPictureSuccessfully() {
        // 1. Precondiciones

        insertAnotherPicture();
        Picture p = pictureModel();
        p.setId(null);

        // 2. Ejercitar
        Picture picture = this.pictureDao.create(p);

        // 3. Postcondiciones
        assertEquals(2, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, PICTURES_TABLE));
        assertEquals(MIME_TYPE, picture.getMimeType());
        assertEquals(PICTURE, picture.getName());
        assertEquals(IMG_SIZE, (long)picture.getSize());
        assertArrayEquals(IMG_DATA, picture.getData());
    }

    @Test
    public void testCreatePictureNullFail()
    {
        // 1. Precondiciones

        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Picture picture = this.pictureDao.create(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
    }

    @Test
    public void testCreatePictureEmptyPictureFail()
    {
        // 1. Precondiciones

        Picture p = new Picture();
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Picture picture = this.pictureDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException (no data) o DataIntegrityViolationException (no id), se hace esto porque depende de cual chequea primero.
    }

    @Test
    public void testCreatePictureEmptyDataFail()
    {
        // 1. Precondiciones

        Picture p = pictureModel();
        p.setData(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Picture picture = this.pictureDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    @Test
    public void testCreatePictureEmptySizeFail()
    {
        // 1. Precondiciones

        Picture p = pictureModel();
        p.setSize(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Picture picture = this.pictureDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    @Test
    public void testCreatePictureEmptyMimeFail()
    {
        // 1. Precondiciones

        Picture p = pictureModel();
        p.setMimeType(null);
        expectedException.expect(PersistenceException.class);

        // 2. Ejercitar
        Picture picture = this.pictureDao.create(p);

        // 3. Postcondiciones
        // Que el metodo tire IllegalStateException
    }

    /* --------------------- MÉTODO: pictureDao.findById(String) -------------------------------------------- */

    @Test
    public void testFindPictureById()
    {
        // 1. Precondiciones

        insertPicture();
        insertAnotherPicture();

        // 2. Ejercitar
        Optional<Picture> picture = this.pictureDao.findById(STARTING_ID);

        // 3. Postcondiciones
        assertTrue(picture.isPresent());
        assertEquals(PICTURE, picture.get().getName());
    }

    @Test
    public void testFindPictureByIdDoesntExist()
    {
        // 1. Precondiciones

        insertPicture();

        // 2. Ejercitar
        Optional<Picture> picture = this.pictureDao.findById(STARTING_ID + 1);

        // 3. Postcondiciones
        assertFalse(picture.isPresent());
    }

    @Test
    public void testFindPictureByIdNull()
    {
        // 1. Precondiciones

        insertPicture();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        Optional<Picture> picture = this.pictureDao.findById(null);

        // 3. Postcondiciones
        assertFalse(picture.isPresent());
    }

    /* --------------------- MÉTODO: pictureDao.findByIds(Collection<String>) -------------------------------------------- */

    @Test
    public void testFindPictureByIds()
    {
        // 1. Precondiciones

        insertPicture();
        insertAnotherPicture();

        // 2. Ejercitar
        Collection<Picture> pictures = this.pictureDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(pictures);
        assertEquals(2, pictures.size());
        for (Picture p : pictures){
            assertTrue(p.getId().equals(STARTING_ID) || p.getId().equals(STARTING_ID + 1));
        }
    }

    @Test
    public void testFindPictureByIdsNotAllPresent()
    {
        // 1. Precondiciones

        insertPicture();

        // 2. Ejercitar
        Collection<Picture> pictures = this.pictureDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(pictures);
        assertEquals(1, pictures.size());
        for (Picture p : pictures){
            assertEquals(pictureModel(), p);
        }
    }

    @Test
    public void testFindPictureByIdsDontExist()
    {
        // 1. Precondiciones

        
        // 2. Ejercitar
        Collection<Picture> pictures = this.pictureDao.findByIds(Arrays.asList(STARTING_ID, STARTING_ID + 1));

        // 3. Postcondiciones
        assertNotNull(pictures);
        assertTrue(pictures.isEmpty());
    }

    /* --------------------- MÉTODO: pictureDao.list() -------------------------------------------- */

    @Test
    public void testPictureList()
    {
        // 1. Precondiciones

        insertPicture();
        insertAnotherPicture();

        // 2. Ejercitar
        Collection<Picture> pictures = this.pictureDao.list();

        // 3. Postcondiciones
        assertNotNull(pictures);
        assertEquals(2, pictures.size());
    }

    @Test
    public void testPictureEmptyList()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        Collection<Picture> pictures = this.pictureDao.list();

        // 3. Postcondiciones
        assertNotNull(pictures);
        assertTrue(pictures.isEmpty());
    }
    
    /* --------------------- MÉTODO: pictureDao.update(Picture) -------------------------------------------- */

    @Test
    public void testPictureUpdate()
    {
        // 1. Precondiciones

        insertPicture();
        insertAnotherPicture();
        Picture p = pictureModel();
        p.setName("Armenia");

        // 2. Ejercitar
        this.pictureDao.update(p);

        // 3. Postcondiciones
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
        assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, PICTURES_TABLE, "name = 'Armenia'"));
        assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, PICTURES_TABLE, "name = '"+ PICTURE +"'"));
    }

    @Test
    public void testPictureUpdateNull()
    {
        // 1. Precondiciones

        insertPicture();
        insertAnotherPicture();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.pictureDao.update(null);

        // 3. Postcondiciones
        // Que el metodo tire NullPointerException
        assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    @Test
    public void testPictureUpdateNotExistentPicture()
    {
        // 1. Precondiciones

        insertAnotherPicture();
        Picture p = pictureModel();
        p.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.pictureDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    @Test
    public void testPictureUpdatePictureWithNullId()
    {
        // 1. Precondiciones

        insertPicture();
        Picture p = pictureModel();
        p.setId(null);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.pictureDao.update(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    /* --------------------- MÉTODO: pictureDao.remove(String id) -------------------------------------------- */

    @Test
    public void testPictureRemoveById()
    {
        // 1. Precondiciones

        insertPicture();

        // 2. Ejercitar
        this.pictureDao.remove(STARTING_ID);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    @Test
    public void testPictureRemoveByIdNotExistent()
    {
        // 1. Precondiciones

        insertPicture();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.pictureDao.remove(STARTING_ID + 1);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    @Test
    public void testPictureRemoveByNullId()
    {
        // 1. Precondiciones

        insertPicture();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.pictureDao.remove((Integer) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }
    /* --------------------- MÉTODO: pictureDao.remove(Picture) -------------------------------------------- */

    @Test
    public void testPictureRemoveByModel()
    {
        // 1. Precondiciones

        insertPicture();
        Picture p = pictureModel();

        // 2. Ejercitar
        this.pictureDao.remove(p);

        // 3. Postcondiciones
        assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    @Test
    public void testPictureRemoveByModelNotExistent()
    {
        // 1. Precondiciones

        insertAnotherPicture();
        Picture p = pictureModel();
        p.setId(STARTING_ID + 1);
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.pictureDao.remove(p);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    @Test
    public void testPictureRemoveByNullModel()
    {
        // 1. Precondiciones

        insertPicture();
        expectedException.expect(IllegalArgumentException.class);

        // 2. Ejercitar
        this.pictureDao.remove((Picture) null);

        // 3. Postcondiciones
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, PICTURES_TABLE));
    }

    /* --------------------- MÉTODO: pictureDao.count() -------------------------------------------- */

    @Test
    public void testPictureCount()
    {
        // 1. Precondiciones

        insertPicture();
        insertAnotherPicture();

        // 2. Ejercitar
        ModelMetadata modelMetadata = this.pictureDao.count();

        // 3. Postcondiciones
        assertEquals(2, (long) modelMetadata.getCount());

    }

    @Test
    public void testPictureCountEmptyTable()
    {
        // 1. Precondiciones


        // 2. Ejercitar
        ModelMetadata modelMetadata = this.pictureDao.count();

        // 3. Postcondiciones
        assertEquals(0, (long) modelMetadata.getCount());

    }
}
