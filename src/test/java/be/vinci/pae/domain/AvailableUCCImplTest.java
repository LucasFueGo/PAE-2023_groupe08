package be.vinci.pae.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.AvailableDAO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

@TestInstance(Lifecycle.PER_CLASS)
class AvailableUCCImplTest extends TestBinder {


  private AvailableFactory availableFactory;
  private AvailableUCC availableUCC;
  private AvailableDAO availableDAO;
  private Available available1;


  @BeforeAll
  void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.availableFactory = locator.getService(AvailableFactory.class);
    this.availableUCC = locator.getService(AvailableUCC.class);
    this.availableDAO = locator.getService(AvailableDAO.class);
  }

  /**
   * Initialize the tests.
   */
  @BeforeEach
  void init() {
    Mockito.reset(availableDAO);
    Available available1 = (Available) availableFactory.getAvailable();
    available1.setId(1);
    available1.setDay(new Date());
    available1.setMornig(true);
  }

  /**
   * Test getAllAvailable successfull.
   */

  @Test
  @DisplayName("Test getAllAvailables successfull")
  void testGetAllAvailables() {
    List<AvailableDTO> availables = new ArrayList<>();
    availables.add(available1);
    Mockito.when(availableDAO.getAllAvaibality()).thenReturn(availables);
    assertEquals(availables, availableUCC.getAllAvailable());
  }

  /**
   * Test getAvailableById successfull.
   */
  @Test
  @DisplayName("Test getAvailableById successfull")
  void testGetAvailableById() {
    Available available2 = (Available) availableFactory.getAvailable();
    available2.setId(2);
    available2.setDay(new Date());
    available2.setMornig(false);

    Mockito.when(availableDAO.getAvaibalityById(available2.getId())).thenReturn(available2);
    assertEquals(available2, availableUCC.getAvailableById(available2.getId()));
  }

  /**
   * Test getAvailableById fail.
   */
  @Test
  @DisplayName("Test getAvailableById fail")
  void testGetAvailableByIdFail() {
    Available available2 = (Available) availableFactory.getAvailable();
    available2.setId(2);
    available2.setDay(new Date());
    available2.setMornig(false);

    Mockito.when(availableDAO.getAvaibalityById(available2.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      availableUCC.getAvailableById(available2.getId());
    });
  }

  /**
   * test failed getAvailableById because of negative id.
   */
  @Test
  @DisplayName("test failed getAvailableById because of negative id")
  void testGetAvailableByIdFailNegativeId() {
    assertThrows(BizException.class, () -> availableUCC.getAvailableById(-1));
  }


  /**
   * Test putNewAvailable successfull (afternoon.
   */
  @Test
  @DisplayName("Test putNewAvailable successfull")
  void testPutNewAvailable() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    Available available3 = (Available) availableFactory.getAvailable();
    available3.setId(3);
    available3.setMornig(false);
    Date day = calendar.getTime();
    java.sql.Date sqlDate = new java.sql.Date(day.getTime());
    Mockito.when(availableDAO.addAvailabality(sqlDate, 2)).thenReturn(true);
    assertEquals(true, availableUCC.putNewAvailable(day, false));
  }


  /**
   * Test putNewAvailable successfull (morning).
   */
  @Test
  @DisplayName("Test putNewAvailable successfull (morning)")
  void testPutNewAvailableMorning() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    Date day = calendar.getTime();
    java.sql.Date sqlDate = new java.sql.Date(day.getTime());
    Mockito.when(availableDAO.addAvailabality(sqlDate, 2)).thenReturn(true);
    assertTrue(availableUCC.putNewAvailable(day, true));
  }


  /**
   * Test putNewAvailable fail.
   */
  @Test
  @DisplayName("Test putNewAvailable fail")
  void testPutNewAvailableFail() {

    //Date pastDate = new Date(System.currentTimeMillis() - 1000000);
    Available available3 = (Available) availableFactory.getAvailable();
    available3.setId(3);
    available3.setMornig(false);
    Date day = new Date();
    java.sql.Date sqlDate = new java.sql.Date(day.getTime());
    Mockito.when(availableDAO.addAvailabality(sqlDate, 2)).thenReturn(false);
    // assertEquals(true, availableUCC.putNewAvailable(pastDate, false));
    assertThrows(BizException.class, () -> availableUCC.putNewAvailable(day, false));

  }

  /**
   * Test getAllAvailable fail.
   */
  @Test
  @DisplayName("Test getAllAvailable fail")
  void testGetAllAvailaibleFail() {
    Mockito.when(availableDAO.getAllAvaibality()).thenReturn(null);

    assertThrows(BizException.class, () -> {
      availableUCC.getAllAvailable();
    });

  }


  /**
   * Test getAvailableById fail.
   */
  @Test
  @DisplayName("Test getAvailableById fail")
  void testGetAvailaibleByIdFail() {
    Mockito.when(availableDAO.getAvaibalityById(1)).thenReturn(null);

    assertThrows(BizException.class, () -> {
      availableUCC.getAvailableById(1);
    });

  }
}