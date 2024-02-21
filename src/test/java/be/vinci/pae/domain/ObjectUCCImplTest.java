package be.vinci.pae.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.exception.FatalException;
import be.vinci.pae.services.FilterDAO;
import be.vinci.pae.services.ObjectDAO;
import be.vinci.pae.utils.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * ObjectUCCImplTest class.
 */
@TestInstance(Lifecycle.PER_CLASS)
class ObjectUCCImplTest extends TestBinder {

  static {
    Config.load("jenkins.properties");
  }

  private ObjectFactory objectFactory;
  private ObjectUCC objectUCC;
  private ObjectDAO objectDAO;
  private FilterDAO filterDAO;
  private FilterFactory filterFactory;

  private Object object1;

  private Object object2;

  /**
   * Set up the tests.
   */
  @BeforeAll
  void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.objectFactory = locator.getService(ObjectFactory.class);
    this.filterFactory = locator.getService(FilterFactory.class);
    this.objectUCC = locator.getService(ObjectUCC.class);
    this.objectDAO = locator.getService(ObjectDAO.class);
    this.filterDAO = locator.getService(FilterDAO.class);
  }

  /**
   * Initialize the tests.
   */
  @BeforeEach
  void init() {
    Mockito.reset(objectDAO);
    object1 = (Object) objectFactory.getObject();
    Date today = new Date();
    object1.setDatePassage(today);
    object1.setArrivalTime(14.30);
    object1.setId(0);
    object1.setDescription("table");
    object1.setIdType(1);
    object1.setVersion(1);

    object2 = (Object) objectFactory.getObject();
    object2.setDatePassage(today);
    object2.setArrivalTime(14.30);
    object2.setId(0);
    object2.setDescription("table");
    object2.setIdType(1);
    object2.setVersion(2);

  }

  /**
   * Test successful drop object to workshop.
   */
  @Test
  @DisplayName("Test successful drop object to workshop")
  public void successDropObjectInTheWorkShop() {
    object1.setState("AC");
    object2.setState("DW");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheWorkShop(object1.getId())).thenReturn(object2);

    ObjectDTO resultat = objectUCC.dropObjectInTheWorkShop(0);

    assertEquals(resultat.getState(), object2.getState());
  }

  /**
   * Test failed drop object to workshop - issue with version.
   */
  @Test
  @DisplayName("Test failed drop object to workshop - issue with version")
  public void failedDropObjectInTheWorkShopVersion() {
    object1.setState("AC");
    object2.setState("DW");
    object2.setVersion(3);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheWorkShop(object1.getId())).thenReturn(object2);

    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheWorkShop(0);
    });
  }

  /**
   * Test failed drop object to workshop - bad state.
   */
  @Test
  @DisplayName("Test failed drop object to workshop")
  public void failedDropObjectInTheWorkShop() {
    object1.setState("DW");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheWorkShop(object1.getId())).thenReturn(object1);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheWorkShop(0);
    });
  }

  /**
   * Test failed because null drop object to workshop.
   */
  @Test
  @DisplayName("Test failed because null drop object to workshop")
  public void failedDropObjectInTheWorkShopNull() {
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheWorkShop(0);
    });
  }

  /**
   * Test failed during the drop object to workshop.
   */
  @Test
  @DisplayName("Test failed because during null drop object to workshop")
  public void failedDropObjectDuringToInTheWorkShop() {
    object1.setState("AC");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheWorkShop(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheWorkShop(0);
    });
  }

  // __________________________

  /**
   * Test successful drop object to store.
   */
  @Test
  @DisplayName("Test successful drop object to store")
  public void successDropObjectInTheStore() {
    object1.setState("AC");
    object2.setState("DS");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheStore(object1.getId())).thenReturn(object2);

    // When
    ObjectDTO result = objectUCC.dropObjectInTheStore(object1.getId());

    // Then
    assertEquals(object2, result);
  }

  /**
   * Test successful drop object to store V2.
   */
  @Test
  @DisplayName("Test successful drop object to store V2")
  public void successDropObjectInTheStoreV2() {
    object1.setState("DW");
    object2.setState("AS");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheStore(object1.getId())).thenReturn(object2);

    ObjectDTO result = objectDAO.dropObjectInTheStore(object1.getId());

    assertEquals(object2.getState(), result.getState());

  }

  /**
   * Test failed drop object to store - issue with version.
   */
  @Test
  @DisplayName("Test failed drop object to store - issue with version")
  public void failedDropObjectInTheStoreVersion() {
    object1.setState("AC");
    object2.setState("DS");
    object2.setVersion(3);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheStore(object1.getId())).thenReturn(object2);

    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheStore(0);
    });
  }

  /**
   * Test failed drop object to store - bad state.
   */
  @Test
  @DisplayName("Test failed drop object to store")
  public void failedDropObjectInTheStore() {
    object1.setState("FF");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheStore(object1.getId())).thenReturn(object1);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheStore(0);
    });
  }

  /**
   * Test failed because null drop object to store.
   */
  @Test
  @DisplayName("Test failed because null drop object to store")
  public void failedDropObjectInTheStoreNull() {
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheStore(0);
    });
  }

  /**
   * Test failed during the drop object to store.
   */
  @Test
  @DisplayName("Test failed because during null drop object to store")
  public void failedDropObjectDuringInTheStore() {
    object1.setState("AC");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectInTheStore(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectInTheStore(0);
    });
  }

  // __________________________

  /**
   * Test successful drop object for sale.
   */
  @Test
  @DisplayName("Test successful drop object for sale")
  public void successDropObjectForSale() {
    object1.setState("AS");
    object2.setState("PS");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectForSale(object1.getId(), 6)).thenReturn(object2);

    // When
    ObjectDTO result = objectUCC.dropObjectForSale(object1.getId(), 6);

    assertEquals(object2, result);
  }

  /**
   * Test failed drop object for sale - issue with versions.
   */
  @Test
  @DisplayName("Test failed drop object for sale - issue with version")
  public void failedDropObjectForSaleVersion() {
    object1.setState("AS");
    object2.setState("PS");
    object2.setVersion(3);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectForSale(object1.getId(), 6)).thenReturn(object2);

    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectForSale(0, 6);
    });
  }


  /**
   * Test failed drop object for sale - bad state.
   */
  @Test
  @DisplayName("Test failed drop object for sale")
  public void failedDropObjectForSale() {
    object1.setState("FF");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectForSale(object1.getId(), 6)).thenReturn(object1);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectForSale(0, 6);
    });
  }

  /**
   * Test failed because null drop object for sale.
   */
  @Test
  @DisplayName("Test failed because null drop object for sale")
  public void failedDropObjectForSaleNull() {
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectForSale(0, 6);
    });
  }

  /**
   * Test failed during the drop object for sale.
   */
  @Test
  @DisplayName("Test failed because during null drop object for sale")
  public void failedDropObjectDuringForSale() {
    object1.setState("AS");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectForSale(object1.getId(), 6)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectForSale(0, 6);
    });
  }

  // __________________________


  /**
   * Test successful getAllObjects.
   */
  @Test
  @DisplayName("Test successful getAllObjects")
  public void successGetAllObjects() {
    List<ObjectDTO> liste = new ArrayList<>();
    liste.add(object1);

    Mockito.when(objectDAO.getAllObjects(true, true, true)).thenReturn(liste);

    assertEquals(liste, objectUCC.getAllObjects(true, true, true));
  }


  /**
   * Test failed getAllObjects.
   */
  @Test
  @DisplayName("Test failed getAllObjects because null list")
  public void failedGetAllObjects() {
    Mockito.when(objectDAO.getAllObjects(true, true, true)).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.getAllObjects(true, true, true);
    });
  }

  // __________________________


  /**
   * Test successful getAllObjects (0 params).
   */
  @Test
  @DisplayName("Test successful getAllObjects")
  public void successGetAllObjects0Params() {
    List<ObjectDTO> liste = new ArrayList<>();
    liste.add(object1);

    Mockito.when(objectDAO.getAllObjects()).thenReturn(liste);

    assertEquals(liste, objectUCC.getAllObjects());
  }


  /**
   * Test failed getAllObjects (0 params).
   */
  @Test
  @DisplayName("Test failed getAllObjects because null list")
  public void failedGetAllObjects0Params() {
    Mockito.when(objectDAO.getAllObjects()).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.getAllObjects();
    });
  }

  // __________________________


  /**
   * Test successful getObjectById.
   */
  @Test
  @DisplayName("Test successful getObjectById")
  public void successGetObjectById() {
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);

    assertEquals(object1, objectUCC.getObjectById(0));
  }


  /**
   * Test failed getObjectById.
   */
  @Test
  @DisplayName("Test failed getObjectById because null")
  public void failedGetObjectById() {
    Mockito.when(objectDAO.getAllObjects()).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.getObjectById(0);
    });
  }

  // __________________________


  /**
   * Test successful refuseObject.
   */
  @Test
  @DisplayName("Test successful refuseObject")
  public void successRefuseObject() {
    object1.setState("PR");
    object2.setState("RE");

    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);
    Mockito.when(objectDAO.refuseObject(object1.getId(), "test")).thenReturn(object2);

    ObjectDTO resultat = objectUCC.refuseObject(0, "test");

    assertEquals(object2.getState(), resultat.getState());
  }


  /**
   * Test failed refuseObject - worst state (!= PR).
   */
  @Test
  @DisplayName("Test failed refuseObject worst state")
  public void failedRefuseObject() {
    object1.setState("AS");
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);
    Mockito.when(objectDAO.refuseObject(0, "test")).thenReturn(object1);

    assertThrows(BizException.class, () -> {
      objectUCC.refuseObject(0, "test");
    });

  }

  /**
   * Test failed refuseObject - issue with version.
   */
  @Test
  @DisplayName("Test failed refuseObject issue with version")
  public void failedRefuseObjectVersion() {
    object1.setState("PR");
    object2.setState("RE");
    object2.setVersion(3);
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);
    Mockito.when(objectDAO.refuseObject(0, "test")).thenReturn(object1);

    assertThrows(BizException.class, () -> {
      objectUCC.refuseObject(0, "test");
    });

  }

  /**
   * Test failed refuseObject - id dont exist.
   */
  @Test
  @DisplayName("Test failed refuseObject id dont exist")
  public void failedRefuseObjectIDNull() {
    object1.setState("PR");
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.refuseObject(0, "test");
    });

  }

  /**
   * Test failed refuseObject - refuse don't work.
   */
  @Test
  @DisplayName("Test failed refuseObject - refuse don't work")
  public void failedRefuseObjectRefuseDontWork() {
    object1.setState("PR");
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);
    Mockito.when(objectDAO.refuseObject(0, "test")).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.refuseObject(0, "test");
    });

  }

  // __________________________


  /**
   * Test successful AccepteObject.
   */
  @Test
  @DisplayName("Test successful AccepteObject")
  public void successAccepteObject() {
    object1.setState("PR");
    object2.setState("AC");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.acceptObject(object1.getId())).thenReturn(object2);

    ObjectDTO resultat = objectUCC.acceptObject(object1.getId());

    assertEquals(object2.getState(), resultat.getState());
  }


  /**
   * Test failed AccepteObject - worst state (!= PR).
   */
  @Test
  @DisplayName("Test failed AccepteObject worst state")
  public void failedAccepteObject() {
    object1.setState("AS");
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);
    Mockito.when(objectDAO.acceptObject(0)).thenReturn(object1);

    assertThrows(BizException.class, () -> {
      objectUCC.acceptObject(0);
    });

  }

  /**
   * Test failed AccepteObject - issue with version.
   */
  @Test
  @DisplayName("Test failed AccepteObject issue with version")
  public void failedAccepteObjectVersion() {
    object1.setState("PR");
    object2.setState("AC");
    object2.setVersion(3);
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);
    Mockito.when(objectDAO.acceptObject(0)).thenReturn(object1);

    assertThrows(BizException.class, () -> {
      objectUCC.acceptObject(0);
    });
  }

  /**
   * Test failed AccepteObject - id dont exist.
   */
  @Test
  @DisplayName("Test failed AccepteObject id dont exist")
  public void failedAccepteObjectIDNull() {
    object1.setState("PR");
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.acceptObject(0);
    });

  }

  /**
   * Test failed AccepteObject - refuse don't work.
   */
  @Test
  @DisplayName("Test failed AccepteObject - Accepte don't work")
  public void failedAccepteObjectDontWork() {
    object1.setState("PR");
    Mockito.when(objectDAO.getObjectById(0)).thenReturn(object1);
    Mockito.when(objectDAO.acceptObject(0)).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.acceptObject(0);
    });
  }

  // __________________________


  /**
   * Test successful getObjectsByType.
   */
  @Test
  @DisplayName("Test successful getObjectsByType")
  public void successGetObjectsByType() {
    List<ObjectDTO> liste = new ArrayList<>();
    liste.add(object1);

    Mockito.when(objectDAO.getAllObjectsByType(1)).thenReturn(liste);

    assertEquals(liste, objectUCC.getObjectsByType(1));
  }


  /**
   * Test failed getObjectsByType.
   */
  @Test
  @DisplayName("Test failed getObjectsByType because null list")
  public void failedGetObjectsByType() {
    Mockito.when(objectDAO.getAllObjectsByType(1)).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.getObjectsByType(1);
    });
  }

  // __________________________

  /**
   * Test successful drop object for sold.
   */
  @Test
  @DisplayName("Test successful drop object for sold")
  public void successDropObjectSold() {
    object1.setState("PS");
    object2.setState("SO");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSold(object1.getId())).thenReturn(object2);

    ObjectDTO resultat = objectUCC.dropObjectSold(0);

    assertEquals(object2.getState(), resultat.getState());
  }

  /**
   * Test failed drop object for sold - issue with version.
   */
  @Test
  @DisplayName("Test failed drop object for sold - issue with version")
  public void failedDropObjectSoldVersion() {
    object1.setState("PS");
    object2.setState("SO");
    object2.setVersion(3);
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSold(object1.getId())).thenReturn(object2);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSold(0);
    });
  }

  /**
   * Test failed drop object for sold - bad state.
   */
  @Test
  @DisplayName("Test failed drop object for sold")
  public void failedDropObjectSold() {
    object1.setState("FF");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSold(object1.getId())).thenReturn(object1);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSold(0);
    });
  }

  /**
   * Test failed because null drop object for sold.
   */
  @Test
  @DisplayName("Test failed because null drop object for sold")
  public void failedDropObjectSoldNull() {
    object1.setState("PS");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSold(0);
    });
  }

  /**
   * Test failed during the drop object for sold.
   */
  @Test
  @DisplayName("Test failed because during null drop object for sold")
  public void failedDropObjectDuringSold() {
    object1.setState("PS");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSold(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSold(0);
    });
  }

  // __________________________

  /**
   * test successful getAbsolutelyAllObjects.
   */
  @Test
  @DisplayName("Test successful getAbsolutelyAllObjects")
  public void successGetAbsolutelyAllObjects() {
    List<ObjectDTO> liste = new ArrayList<>();
    liste.add(object1);
    liste.add(object2);

    Mockito.when(objectDAO.getAbsolutelyAllObjects()).thenReturn(liste);

    assertEquals(liste, objectUCC.getAbsolutelyAllObjects());
  }

  /**
   * test failed getAbsolutelyAllObjects - list of objects null.
   */
  @Test
  @DisplayName("Test failed getAbsolutelyAllObjects - list of objects null")
  public void failedGetAbsolutelyAllObjects() {
    Mockito.when(objectDAO.getAbsolutelyAllObjects()).thenReturn(null);

    assertThrows(BizException.class, () -> {
      objectUCC.getAbsolutelyAllObjects();
    });
  }

  // __________________________

  /**
   * propose object successful with a phone number.
   */
  @Test
  @DisplayName("Test successful propose object with a phone number")
  public void successProposeObjectWithPhoneNumber() {

    ObjectDTO proposedObject = objectFactory.getObject();
    proposedObject.setIdPassage(1);
    proposedObject.setIdTimeSlot(1);
    proposedObject.setIdType(1);
    proposedObject.setDescription("test");
    proposedObject.setOfferingMemberPhone("0471/89.78.55");

    Mockito.when(objectDAO.proposeObject(proposedObject)).thenReturn(1);

    int resultat = objectUCC.proposeObject(proposedObject);

    assertEquals(1, resultat);
  }

  /**
   * propose object successful with an idUser (token).
   */
  @Test
  @DisplayName("Test successful propose object with an idUser (token)")
  public void successProposeObjectWithIdUser() {

    ObjectDTO proposedObject = objectFactory.getObject();
    proposedObject.setIdPassage(1);
    proposedObject.setIdTimeSlot(1);
    proposedObject.setIdType(1);
    proposedObject.setDescription("test");
    proposedObject.setIdUser(1);
    proposedObject.setOfferingMemberPhone("");

    Mockito.when(objectDAO.proposeObject(proposedObject)).thenReturn(1);

    int resultat = objectUCC.proposeObject(proposedObject);

    assertEquals(1, resultat);
  }

  /**
   * propose object with wrong writting for phoneNumber.
   */
  @Test
  @DisplayName("Test failed propose object - wrong writting for phoneNumber")
  public void failedProposeObjectWrongNumber() {

    ObjectDTO proposedObject = objectFactory.getObject();
    proposedObject.setIdPassage(1);
    proposedObject.setIdTimeSlot(1);
    proposedObject.setIdType(1);
    proposedObject.setDescription("test");
    proposedObject.setOfferingMemberPhone("0471897855");

    assertThrows(BizException.class, () -> objectUCC.proposeObject(proposedObject));
  }

  /**
   * propose object with null object.
   */
  @Test
  @DisplayName("Test failed propose object - null object")
  public void failedProposeObjectNull() {

    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(-1);

    Mockito.when(objectDAO.proposeObject(object1)).thenReturn(0);
    assertThrows(BizException.class, () -> {
      objectUCC.proposeObject(object1);
    });
  }

  /**
   * Test successful update object - with no file update.
   */
  @Test
  @DisplayName("Test successful update object - with no file update")
  public void successUpdateObject() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");
    File file = new File(
        "src/test/ressources/chaise.jpg");
    InputStream input = new FileInputStream(file);

    object2.setIdPassage(1);
    object2.setIdTimeSlot(1);
    object2.setIdType(1);
    object2.setDescription("test");
    object2.setOfferingMemberPhone("0471/89.78.55");
    object2.setIdUser(1);
    object2.setId(1);
    object2.setVersion(2);
    object2.setUrl("test");

    List<FilterDTO> idTypes = new ArrayList<>();
    FilterDTO filter = filterFactory.getFilter();
    filter.setId(1);
    filter.setName("test");
    idTypes.add(filter);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(filterDAO.getAllFilters()).thenReturn(idTypes);
    Mockito.when(
        objectDAO.updateObjectById(object1.getId(), object1.getDescription(), object1.getUrl(),
            object1.getIdType())).thenReturn(object2);

    Mockito.when(fileDisposition.getFileName()).thenReturn(null);

    ObjectDTO resultat = objectUCC.updateObjectById(object1.getId(), object1.getDescription(),
        input, fileDisposition,
        object1.getIdType());

    assertEquals(object2, resultat);
  }

  // __________________________

  /**
   * Test successful WithdrawObject.
   */
  @Test
  @DisplayName("Test successful WithdrawObject")
  public void successWithdrawObject() {
    object2.setState("WS");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.withdrawObject(object1.getId())).thenReturn(object2);
    ObjectDTO resultat = objectUCC.withdrawObject(object1.getId());
    assertEquals(object2, resultat);
  }

  /**
   * Test failed WithdrawObject - bad state.
   */
  @Test
  @DisplayName("Test failed WithdrawObject - bad state")
  public void failedWithdrawObject() {
    object1.setState("FF");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.withdrawObject(object1.getId())).thenReturn(object1);
    assertThrows(BizException.class, () -> {
      objectUCC.withdrawObject(object1.getId());
    });
  }

  /**
   * Test failed WithdrawObject - null object.
   */
  @Test
  @DisplayName("Test failed WithdrawObject - null object")
  public void failedWithdrawObjectNull() {
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.withdrawObject(object1.getId());
    });
  }

  /**
   * Test successful dropObjectSoldDirectly.
   */
  @Test
  @DisplayName("Test successful dropObjectSoldDirectly")
  public void successDropObjectSoldDirectly() {
    object1.setState("AS");
    object2.setState("SO");

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSoldDirectly(object1.getId(), 4)).thenReturn(object2);
    ObjectDTO resultat = objectUCC.dropObjectSoldDirectly(object1.getId(), 4);
    assertEquals(object2.getState(), resultat.getState());
  }

  /**
   * Test failed dropObjectSoldDirectly - bad state.
   */
  @Test
  @DisplayName("Test failed dropObjectSoldDirectly - bad state")
  public void failedDropObjectSoldDirectly() {
    int price = 4;
    object1.setState("PR");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSoldDirectly(object1.getId(), price)).thenReturn(object1);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSoldDirectly(object1.getId(), price);
    });
  }

  /**
   * Test failed dropObjectSoldDirectly - null object.
   */
  @Test
  @DisplayName("Test failed dropObjectSoldDirectly - null object")
  public void failedDropObjectSoldDirectlyNull() {
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSoldDirectly(object1.getId(), 4);
    });
  }

  /**
   * Test failed dropObjectSoldDirectly - null object.
   */
  @Test
  @DisplayName("Test failed dropObjectSoldDirectly - null object2")
  public void failedDropObjectSoldDirectlyObject2Null() {
    int price = 4;
    object1.setState("AS");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSoldDirectly(object1.getId(), price)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSoldDirectly(object1.getId(), price);
    });
  }

  /**
   * Test failed dropObjectSoldDirectly - null object.
   */
  @Test
  @DisplayName("Test failed dropObjectSoldDirectly - Bad Version")
  public void failedDropObjectSoldDirectlyBadVersion() {
    object1.setVersion(1);
    object1.setState("AS");
    object2.setVersion(8);
    int price = 4;
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSoldDirectly(object1.getId(), price)).thenReturn(object2);

    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSoldDirectly(object1.getId(), price);
    });
  }

  /**
   * Test failed dropObjectSoldDirectly - wrong price.
   */
  @ParameterizedTest
  @ValueSource(ints = {0, 11})
  @DisplayName("Test failed dropObjectSoldDirectly - wrong price")
  public void failedDropObjectSoldDirectlyWrongPrice(int price) {
    object1.setState("DW");
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSoldDirectly(object1.getId(), price);
    });
  }

  /**
   * Test failed dropObjectSoldDirectly - wrong version.
   */
  @Test
  @DisplayName("Test failed dropObjectSoldDirectly - wrong version")
  public void failedDropObjectSoldDirectlyWrongVersion() {
    object1.setState("DW");
    object2.setVersion(3);
    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(objectDAO.dropObjectSoldDirectly(object1.getId(), 4)).thenReturn(object2);
    assertThrows(BizException.class, () -> {
      objectUCC.dropObjectSoldDirectly(object1.getId(), 4);
    });
  }

  // __________________________

  /**
   * Test successful getAllObjects for a specific user.
   */
  @Test
  @DisplayName("Test successful getAllObjects for a specific user")
  public void successGetAllObjectsForUser() {
    List<ObjectDTO> objects = new ArrayList<>();
    objects.add(object1);
    objects.add(object2);
    object1.setIdUser(2);
    object2.setIdUser(2);
    Mockito.when(objectDAO.getAllObjectsOfAUser(2)).thenReturn(objects);
    List<ObjectDTO> resultat = objectUCC.getAllObjectsOfAUser(2);
    assertEquals(objects, resultat);
  }

  /**
   * Test failed getAllObjects for a specific user.
   */
  @Test
  @DisplayName("Test failed getAllObjects for a specific user")
  public void failedGetAllObjectsForUser() {
    List<ObjectDTO> objects = new ArrayList<>();
    Mockito.when(objectDAO.getAllObjectsOfAUser(2)).thenReturn(objects);
    List<ObjectDTO> resultat = objectUCC.getAllObjectsOfAUser(2);
    assertEquals(objects, resultat);
  }

  /**
   * Test failed getAllObjects for a specific user - list of object null.
   */
  @Test
  @DisplayName("Test failed getAllObjects for a specific user - list of object null")
  public void failedGetAllObjectsForUserNull() {
    Mockito.when(objectDAO.getAllObjectsOfAUser(2)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.getAllObjectsOfAUser(2);
    });
  }

  // __________________________

  /**
   * Test successful getStatistic.
   */
  @Test
  @DisplayName("Test successful getStatistic")
  public void successGetStatistic() {
    object1.setState("SO");
    object2.setState("SO");
    Map<String, String> objects = new HashMap<>();
    objects.put("sold-objects", "2");
    Mockito.when(objectDAO.getStatistics("2023-01-01", "2023-05-02")).thenReturn(objects);
    Map<String, String> resultat = objectUCC.getStatistics("2023-01-01", "2023-05-02");
    assertEquals(objects, resultat);
  }

  /**
   * Test failed getStatistic - null map.
   */
  @Test
  @DisplayName("Test failed getStatistic - null map")
  public void failedGetStatisticNull() {
    Mockito.when(objectDAO.getStatistics("2023-01-01", "2023-05-02")).thenReturn(null);
    assertThrows(BizException.class, () -> {
      objectUCC.getStatistics("2023-01-01", "2023-05-02");
    });
  }

  // __________________________

  /**
   * Test successful getFilesExtension.
   */
  @Test
  @DisplayName("Test get files extension")
  public void getFilesExtension() {
    String filename1 = "file.txt";
    String filename2 = "file.docx";
    String filename3 = "file";
    String filename4 = ".file";
    String filename5 = "";

    String ext1 = ObjectUCC.getFileExtension(filename1);
    String ext2 = ObjectUCC.getFileExtension(filename2);
    String ext3 = ObjectUCC.getFileExtension(filename3);
    String ext4 = ObjectUCC.getFileExtension(filename4);
    String ext5 = ObjectUCC.getFileExtension(filename5);

    assertEquals("txt", ext1);
    assertEquals("docx", ext2);
    assertEquals("", ext3);
    assertEquals("", ext4);
    assertEquals("", ext5);
  }


  /**
   * Test fail update object - getObjectById -> NULL.
   */
  @Test
  @DisplayName("Test fail update object - getObjectById -> NULL")
  public void failUpdateObjectObjectNull() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");
    File file = new File(
        "src/test/ressources/chaise.jpg");
    InputStream input = new FileInputStream(file);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(null);

    assertThrows(BizException.class,
        () -> objectUCC.updateObjectById(object1.getId(), object1.getDescription(),
            input, fileDisposition,
            object1.getIdType()));
  }

  /**
   * Test fail update object - getObjectById -> NULL.
   */
  @Test
  @DisplayName("Test fail update object - filters -> NULL")
  public void failUpdateObjectFiltersNull() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");
    File file = new File(
        "src/test/ressources/chaise.jpg");
    InputStream input = new FileInputStream(file);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);

    Mockito.when(filterDAO.getAllFilters()).thenReturn(new ArrayList<>());

    assertThrows(BizException.class,
        () -> objectUCC.updateObjectById(object1.getId(), object1.getDescription(),
            input, fileDisposition,
            object1.getIdType()));
  }

  /**
   * Test fail update object - description -> empty.
   */
  @Test
  @DisplayName("Test fail update object - description -> empty ")
  public void failUpdateObjectEmptyDescrition() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");
    File file = new File(
        "src/test/ressources/chaise.jpg");
    InputStream input = new FileInputStream(file);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);

    assertThrows(BizException.class,
        () -> objectUCC.updateObjectById(object1.getId(), "",
            input, fileDisposition,
            object1.getIdType()));
  }

  /**
   * Test fail update object - bad version.
   */
  @Test
  @DisplayName("Test fail update object - bad version")
  public void failUpdateObjectBadVersion() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");

    object2.setIdPassage(1);
    object2.setIdTimeSlot(1);
    object2.setIdType(1);
    object2.setDescription("test");
    object2.setOfferingMemberPhone("0471/89.78.55");
    object2.setIdUser(1);
    object2.setId(1);
    object2.setVersion(8);
    object2.setUrl("test");

    File file = new File(
        "src/test/ressources/chaise.jpg");
    InputStream input = new FileInputStream(file);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(
        objectDAO.updateObjectById(object1.getId(), object1.getDescription(), object1.getUrl(),
            object1.getIdType())).thenReturn(object2);

    assertThrows(BizException.class,
        () -> objectUCC.updateObjectById(object1.getId(), object1.getDescription(),
            input, fileDisposition,
            object1.getIdType()));
  }


  /**
   * Test fail update object - Catch IoExeption.
   */
  @Test
  @DisplayName("Test fail update object - Catch IoExeption ")
  public void failUpdateObjectCatchIoExeption() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");
    File file = new File(
        "src/test/ressources/chaise.jpg");
    InputStream input = new FileInputStream(file);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenThrow(FatalException.class);

    assertThrows(RuntimeException.class,
        () -> objectUCC.updateObjectById(object1.getId(), "",
            input, fileDisposition,
            object1.getIdType()));
  }


  /**
   * Test succes updateObjectById method - with file update.
   */
  @Test
  @DisplayName("Test successful update object - with no file update")
  public void successUpdateObjectByIdfileDispositionNotNull() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test description 1");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    String oldName = "chaise7.jpg";
    object1.setUrl(oldName);
    File file = new File(
        "src/test/ressources/chaise2.jpg");
    InputStream input = new FileInputStream(file);

    try {
      Files.copy(file.toPath(),
          Path.of(Config.getProperty("ImagePath") + Paths.get(object1.getUrl())));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    object2.setIdPassage(1);
    object2.setIdTimeSlot(1);
    object2.setIdType(2);
    object2.setDescription("test description 2");
    object2.setOfferingMemberPhone("0471/89.78.55");
    object2.setIdUser(1);
    object2.setId(1);
    object2.setVersion(2);
    object2.setUrl("");

    List<FilterDTO> idTypes = new ArrayList<>();
    FilterDTO filter1 = filterFactory.getFilter();
    filter1.setId(object1.getIdType());
    filter1.setName("test1");
    idTypes.add(filter1);
    FilterDTO filter2 = filterFactory.getFilter();
    filter2.setId(object2.getIdType());
    filter2.setName("test2");
    idTypes.add(filter2);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(filterDAO.getAllFilters()).thenReturn(idTypes);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    object2.setUrl(newFileNameArtificiel);
    String fileName = "chaise.jpg";

    Mockito.when(
        objectDAO.updateObjectById(object1.getId(), object1.getDescription(), newFileNameArtificiel,
            object1.getIdType())).thenReturn(object2);

    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class);
        MockedStatic<ObjectUCC> mockedObjectUCC = Mockito.mockStatic(ObjectUCC.class)) {

      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);
      Mockito.when(fileDisposition.getFileName()).thenReturn(fileName);
      mockedObjectUCC.when(() -> ObjectUCC.getFileExtension(fileName)).thenReturn("jpg");
      Mockito.when(
          objectDAO.updateObjectById(object1.getId(), object1.getDescription(), object1.getUrl(),
              object1.getIdType())).thenReturn(object2);

      ObjectDTO resultat = objectUCC.updateObjectById(object1.getId(), object1.getDescription(),
          input, fileDisposition,
          object1.getIdType());

      assertEquals(object2, resultat);
      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Test upload method - succes.
   */
  @Test
  @DisplayName("Test upload method - succes")
  void testUploadSucces() throws IOException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");

    object2.setIdPassage(1);
    object2.setIdTimeSlot(1);
    object2.setIdType(1);
    object2.setDescription("test");
    object2.setOfferingMemberPhone("0471/89.78.55");
    object2.setIdUser(1);
    object2.setId(1);
    object2.setVersion(2);
    object2.setUrl("test");

    InputStream file = Mockito.mock(InputStream.class);
    String fileName = "test.jpg";
    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(fileDisposition.getFileName()).thenReturn(fileName);
    Mockito.when(objectDAO.getVersionObject(object1.getId())).thenReturn(1);

    final String id = "493410b3-dd0b-4b78-97bf-289f51f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    Mockito.when(objectDAO.upload(object1.getId(), newFileNameArtificiel)).thenReturn(object2);

    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);
      objectUCC.upload(object1.getId(), file, fileDisposition);
      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
    }
  }

  /**
   * Test upload method - with bad version.
   */
  @Test
  @DisplayName("Test upload method - with bad version")
  void testUploadWithBadVersion() throws IOException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");

    object2.setIdPassage(1);
    object2.setIdTimeSlot(1);
    object2.setIdType(1);
    object2.setDescription("test");
    object2.setOfferingMemberPhone("0471/89.78.55");
    object2.setIdUser(1);
    object2.setId(1);
    object2.setVersion(8);
    object2.setUrl("test");

    InputStream file = Mockito.mock(InputStream.class);
    String fileName = "test.jpg";
    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(fileDisposition.getFileName()).thenReturn(fileName);
    Mockito.when(objectDAO.getVersionObject(object1.getId())).thenReturn(1);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);

    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      String newFileNameArtificiel = id + ".jpg";

      Mockito.when(objectDAO.upload(object1.getId(), newFileNameArtificiel)).thenReturn(object2);

      assertThrows(BizException.class,
          () -> objectUCC.upload(object1.getId(), file, fileDisposition));
      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));

    }

  }

  /**
   * Test upload method - throw IOExeption.
   */
  @Test
  @DisplayName("Test upload method - throw IOExeption")
  void testUploadWithIOExeption() throws IOException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    object1.setUrl("test");

    object2.setIdPassage(1);
    object2.setIdTimeSlot(1);
    object2.setIdType(1);
    object2.setDescription("test");
    object2.setOfferingMemberPhone("0471/89.78.55");
    object2.setIdUser(1);
    object2.setId(1);
    object2.setVersion(8);
    object2.setUrl("test");

    InputStream file = Mockito.mock(InputStream.class);
    String fileName = "test.jpg";
    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(fileDisposition.getFileName()).thenReturn(fileName);
    Mockito.when(objectDAO.getVersionObject(object1.getId())).thenReturn(1);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);

    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);
      String newFileNameArtificiel = id + ".jpg";

      Mockito.when(objectDAO.upload(object1.getId(), newFileNameArtificiel)).thenReturn(object2);

      Mockito.when(Files.copy(file,
              Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel))))
          .thenThrow(FatalException.class);

      assertThrows(RuntimeException.class,
          () -> objectUCC.upload(object1.getId(), file, fileDisposition));

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));

    }
  }


  /**
   * Test fail UpdateObjectById ThrowIOExeption - Throw IOExeption.
   */
  @Test
  @DisplayName("Test fail update object - Throw IOExeption")
  public void failUpdateObjectThrowIOExeption() throws FileNotFoundException {
    object1.setIdPassage(1);
    object1.setIdTimeSlot(1);
    object1.setIdType(1);
    object1.setDescription("test description 1");
    object1.setOfferingMemberPhone("0471/89.78.55");
    object1.setIdUser(1);
    object1.setId(1);
    object1.setVersion(1);
    String oldName = "chaise7.jpg";
    object1.setUrl(oldName);
    File file = new File(
        "src/test/ressources/chaise2.jpg");
    InputStream input = new FileInputStream(file);

    try {
      Files.copy(file.toPath(),
          Path.of(Config.getProperty("ImagePath") + Paths.get(object1.getUrl())));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    object2.setIdPassage(1);
    object2.setIdTimeSlot(1);
    object2.setIdType(2);
    object2.setDescription("test description 2");
    object2.setOfferingMemberPhone("0471/89.78.55");
    object2.setIdUser(1);
    object2.setId(1);
    object2.setVersion(2);
    object2.setUrl("");

    List<FilterDTO> idTypes = new ArrayList<>();
    FilterDTO filter1 = filterFactory.getFilter();
    filter1.setId(object1.getIdType());
    filter1.setName("test1");
    idTypes.add(filter1);
    FilterDTO filter2 = filterFactory.getFilter();
    filter2.setId(object2.getIdType());
    filter2.setName("test2");
    idTypes.add(filter2);

    FormDataContentDisposition fileDisposition = Mockito.mock(FormDataContentDisposition.class);

    Mockito.when(objectDAO.getObjectById(object1.getId())).thenReturn(object1);
    Mockito.when(filterDAO.getAllFilters()).thenReturn(idTypes);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    object2.setUrl(newFileNameArtificiel);
    String fileName = "chaise.jpg";

    Mockito.when(
        objectDAO.updateObjectById(object1.getId(), object1.getDescription(), newFileNameArtificiel,
            object1.getIdType())).thenReturn(object2);

    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class);
        MockedStatic<ObjectUCC> mockedObjectUCC = Mockito.mockStatic(ObjectUCC.class)) {

      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);
      Mockito.when(fileDisposition.getFileName()).thenReturn(fileName);
      mockedObjectUCC.when(() -> ObjectUCC.getFileExtension(fileName)).thenReturn("jpg");
      Mockito.when(
          objectDAO.updateObjectById(object1.getId(), object1.getDescription(), object1.getUrl(),
              object1.getIdType())).thenReturn(object2);

      try {
        input.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      assertThrows(RuntimeException.class,
          () -> {
            objectUCC.updateObjectById(object1.getId(), object1.getDescription(),
                input, fileDisposition,
                object1.getIdType());
          });
      try {
        Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(oldName)));
        Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }

  }

}