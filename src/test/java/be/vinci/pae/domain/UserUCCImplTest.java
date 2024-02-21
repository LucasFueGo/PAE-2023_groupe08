package be.vinci.pae.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.UserDAO;
import be.vinci.pae.utils.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * UserUCCImplTest class.
 */
@TestInstance(Lifecycle.PER_CLASS)
class UserUCCImplTest extends TestBinder {

  static {
    Config.load("jenkins.properties");
  }

  private UserFactory userFactory;
  private UserUCC userUCC;
  private UserDAO userDAO;
  private User user1;
  private User user2;
  private User userFinalOptimistic;

  /**
   * Set up the tests.
   */
  @BeforeAll
  void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.userFactory = locator.getService(UserFactory.class);
    this.userUCC = locator.getService(UserUCC.class);
    this.userDAO = locator.getService(UserDAO.class);
  }

  /**
   * Initialize the tests.
   */
  @BeforeEach
  void init() {
    Mockito.reset(userDAO);
    user1 = (User) userFactory.getUser();
    user1.setId(1);
    user1.setEmail("john.doe@test.com");
    String hash1 = "pwd_1";
    user1.setRole(1);
    user1.setPassword(user1.hashPassword(hash1));
    user1.setVersion(1);

    user2 = (User) userFactory.getUser();
    user2.setId(2);
    user2.setEmail("jane.doe@test.com");
    String hash2 = "pwd_2";
    user2.setPassword(user2.hashPassword(hash2));

    userFinalOptimistic = (User) userFactory.getUser();
    userFinalOptimistic.setId(1);
    userFinalOptimistic.setEmail("john.doe@test.com");
    String hashFinalOptimistic = "pwd_1";
    userFinalOptimistic.setPassword(userFinalOptimistic.hashPassword(hashFinalOptimistic));
    userFinalOptimistic.setVersion(2);
  }

  /**
   * Test successful connection.
   */
  @Test
  @DisplayName("Test successful connection")
  public void successfulConnection() {
    Mockito.when(userDAO.getUserByEmail("john.doe@test.com")).thenReturn(user1);
    assertEquals(user1, userUCC.login("john.doe@test.com", "pwd_1"));
  }


  /**
   * Test connection failed with wrong email.
   */
  @Test
  @DisplayName("Test connection failed because the email and password are incorrect")
  public void connectionAllWrong() {
    Mockito.when(userDAO.getUserByEmail("jane.doe@test.com")).thenReturn(user2);
    assertNotEquals(user1, userUCC.login("jane.doe@test.com", "pwd_2"));
  }

  /**
   * Test connection failed with wrong password.
   */
  @Test
  @DisplayName("Test connection failed because the email is incorrect")
  public void unsuccessfulConnectionWithIncorrectEmail() {
    Mockito.when(userDAO.getUserByEmail("login2")).thenReturn(user2);
    assertThrows(BizException.class, () -> {
      userUCC.login("login1", "password2");
    });
  }

  /**
   * Test connection failed with wrong password.
   */
  @Test
  @DisplayName("Test connection failed because the password is incorrect")
  public void unsuccessfulConnectionWithIncorrectPassword() {
    Mockito.when(userDAO.getUserByEmail("login2")).thenReturn(user2);

    assertThrows(BizException.class, () -> {
      userUCC.login("login2", "password1");
    });
  }

  /**
   * Test connection failed with empty login and password.
   */
  @Test
  @DisplayName("Test connection failed because the login and password are empty")
  public void connectionEmptyLoginPassword() {
    Mockito.when(userDAO.getUserByEmail("login1")).thenReturn(user1);
    assertThrows(BizException.class, () -> {
      userUCC.login("", "");
    });
  }

  /**
   * Test connection failed with null login.
   */
  @Test
  @DisplayName("Test connection failed because the login is null")
  public void connectionNullLogin() {
    Mockito.when(userDAO.getUserByEmail("login1")).thenReturn(user1);
    assertThrows(BizException.class, () -> {
      userUCC.login(null, "password1");
    });
  }

  /**
   * Test connection failed with null password.
   */
  @Test
  @DisplayName("Test connection failed because the password is null")
  public void connectionNullPassword() {
    Mockito.when(userDAO.getUserByEmail("login1")).thenReturn(user1);
    assertThrows(BizException.class, () -> {
      userUCC.login("login1", null);
    });
  }

  /**
   * Test connection failed with an unregistred login.
   */
  @Test
  @DisplayName("Test connection failed because the login is not registered")
  public void connectionUnregistredLogin() {
    Mockito.when(userDAO.getUserByEmail("login3")).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.login("login3", "password3");
    });
  }


  /**
   * Test registration failed with an already registered email.
   */
  @Test
  @DisplayName("Test registration failed because the email is already registered")
  public void failedRegistrationRegisteredEmail() {
    UserDTO userRegistered = userFactory.getUser();
    userRegistered.setId(0);
    userRegistered.setLastName("Riez");
    userRegistered.setFirstName("Robert");
    userRegistered.setEmail("bert.riez@gmail.be");
    userRegistered.setPhone("+324468546465");
    userRegistered.setRole(0);
    userRegistered.setPassword("Jaune;10.");
    userRegistered.setImage("canape.jpg");

    Mockito.when(userDAO.getUserByEmail(userRegistered.getEmail())).thenReturn(userRegistered);

    assertThrows(BizException.class, () -> {
      userUCC.register(userRegistered, null);
    });
  }

  /**
   * Test registration failed because file error.
   */
  @Test
  @DisplayName("Test registration failed because file error")
  public void failedRegistrationFileError() {
    UserDTO userRegistered = userFactory.getUser();
    userRegistered.setId(0);
    userRegistered.setLastName("Riez");
    userRegistered.setFirstName("Robert");
    userRegistered.setEmail("bert.riez@gmail.be");
    userRegistered.setPhone("+324468546465");
    userRegistered.setRole(0);
    userRegistered.setPassword("Jaune;10.");

    Mockito.when(userDAO.getUserByEmail(userRegistered.getEmail())).thenReturn(null);

    assertThrows(RuntimeException.class, () -> {
      userUCC.register(userRegistered, null);
    });
  }

  /**
   * Test registration failed because file error version 2.
   */
  @Test
  @DisplayName("Test registration failed because file error version 2")
  public void failedRegistrationFileErrorv2() throws IOException {
    UserDTO userRegistered = userFactory.getUser();
    userRegistered.setId(0);
    userRegistered.setLastName("Riez");
    userRegistered.setFirstName("Robert");
    userRegistered.setEmail("bert.riez@gmail.be");
    userRegistered.setPhone("+324468546465");
    userRegistered.setRole(0);
    userRegistered.setImage("canape.jpg");
    userRegistered.setPassword("Jaune;10.");

    Mockito.when(userDAO.getUserByEmail(userRegistered.getEmail())).thenReturn(null);
    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);
    input.close();

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      assertThrows(RuntimeException.class, () -> {
        userUCC.register(userRegistered, input);
      });

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
    }

  }

  /**
   * Test registration with wrong mail.
   */
  @Test
  @DisplayName("Test registration failed because the email is not valid")
  public void failedRegistrationWrongMail() throws FileNotFoundException {
    UserDTO userRegistered = userFactory.getUser();
    userRegistered.setId(0);
    userRegistered.setLastName("Riez");
    userRegistered.setFirstName("Robert");
    userRegistered.setEmail("bert.riezil.be");
    userRegistered.setPhone("+324468546465");
    userRegistered.setRole(0);
    userRegistered.setPassword("Jaune;10.");
    userRegistered.setImage("canape.jpg");
    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      assertThrows(BizException.class, () -> userUCC.register(userRegistered, input));

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Test registration with a email already used.
   */
  @Test
  @DisplayName("Test registration with a email already used")
  public void failedRegistrationMailRegistered() throws FileNotFoundException {
    UserDTO userRegistered = userFactory.getUser();
    userRegistered.setId(0);
    userRegistered.setLastName("Riez");
    userRegistered.setFirstName("Robert");
    userRegistered.setEmail("bert.riez@gmail.be");
    userRegistered.setPhone("+324468546465");
    userRegistered.setRole(0);
    userRegistered.setPassword("Jaune;10.");
    userRegistered.setImage("canape.jpg");
    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      assertThrows(BizException.class, () -> {
        userUCC.register(userRegistered, input);
      });

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }

  /**
   * Test registration with success.
   */
  @Test
  @DisplayName("Test a successful registration")
  public void successfulRegistration() throws FileNotFoundException {
    UserDTO user3Test = userFactory.getUser();
    user3Test.setEmail("thomas.doe@hotmail.be");
    user3Test.setPassword("test123");
    user3Test.setRole(0);
    user3Test.setFirstName("Thomas");
    user3Test.setLastName("Doe");
    user3Test.setPhone("0471/89.78.55");
    user3Test.setImage("canape.jpg");
    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);

    Mockito.when(userDAO.createOne(user3Test)).thenReturn(user3Test);
    Mockito.when(userDAO.getUserByEmail(user3Test.getEmail())).thenReturn(null);


    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      UserDTO resultat = userUCC.register(user3Test, input);
      assertEquals(user3Test, resultat);

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }


  /**
   * Test get all users.
   */
  @Test
  @DisplayName("Test get all users")
  public void getAllUsers() {
    List<UserDTO> users = userUCC.getAllUsers();
    assertNotNull(users);
  }

  /**
   * Test to user.
   */
  @Test
  @DisplayName("Test confirm a user as an user")
  public void confirmUserAsUser() {
    user1.setRole(0);
    userFinalOptimistic.setRole(1);

    Mockito.when(userDAO.getUserById(user1.getId())).thenReturn(user1);
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 0)).thenReturn(userFinalOptimistic);
    UserDTO resultat = userUCC.changeRoleUser(user1.getId(), 0);

    assertEquals(userFinalOptimistic, resultat);
  }

  /**
   * Test to user failed.
   */
  @Test
  @DisplayName("Test confirm a user as an user")
  public void confirmUserAsUserFailed() {
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 0)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.getUserById(1);
    });

  }

  /**
   * Test to helper.
   */
  @Test
  @DisplayName("Test confirm a user as an helper")
  public void confirmUserAsHelper() {
    user1.setRole(0);
    userFinalOptimistic.setRole(1);

    Mockito.when(userDAO.getUserById(user1.getId())).thenReturn(user1);
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 1)).thenReturn(userFinalOptimistic);
    UserDTO resultat = userUCC.changeRoleUser(user1.getId(), 1);

    assertEquals(userFinalOptimistic, resultat);
  }

  /**
   * Test to helper failed.
   */
  @Test
  @DisplayName("Test confirm a user as an helper")
  public void confirmUserAsHelpeFailed() {
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 1)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.getUserById(1);
    });

  }

  /**
   * Test to user but wrong version.
   */
  @Test
  @DisplayName("Test confirm a user as an user but wrong version")
  public void confirmUserAsUserButWrongVersion() {
    user1.setRole(0);
    user1.setVersion(1);
    userFinalOptimistic.setRole(1);
    userFinalOptimistic.setVersion(1);

    Mockito.when(userDAO.getUserById(user1.getId())).thenReturn(user1);
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 0)).thenReturn(userFinalOptimistic);

    assertThrows(BizException.class, () -> {
      userUCC.changeRoleUser(user1.getId(), 0);
    });

  }


  /**
   * Test to admin failed.
   */
  @Test
  @DisplayName("Test confirm a user as an admin")
  public void confirmUserAsAdminFailed() {
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 2)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.getUserById(2);
    });

  }


  /**
   * Test to admin.
   */
  @Test
  @DisplayName("Test confirm a user as an admin")
  public void confirmUserAsAdmin() {
    user1.setRole(0);
    userFinalOptimistic.setRole(2);

    Mockito.when(userDAO.getUserById(user1.getId())).thenReturn(user1);
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 2)).thenReturn(userFinalOptimistic);
    UserDTO resultat = userUCC.changeRoleUser(user1.getId(), 2);

    assertEquals(userFinalOptimistic, resultat);
  }

  /**
   * Test to getUserById success.
   */
  @Test
  @DisplayName("Test getUserById success")
  public void successgetUserById() {
    Mockito.when(userDAO.getUserById(user1.getId())).thenReturn(user1);
    assertEquals(user1, userUCC.getUserById(1));
  }

  /**
   * Test to getUserById failed.
   */
  @Test
  @DisplayName("Test getUserById success")
  public void failedgetUserById() {
    Mockito.when(userDAO.getUserById(user1.getId())).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.getUserById(1);
    });
  }

  /**
   * Test to confirmUser user failed.
   */
  @Test
  @DisplayName("Test failedConfirmUserHelperNull success")
  public void failedConfirmUserUserNull() {
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 0)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.changeRoleUser(1, 0);
    });
  }

  /**
   * Test to confirmUser helper failed.
   */
  @Test
  @DisplayName("Test failedConfirmUserHelperNull success")
  public void failedConfirmUserHelperNull() {
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 1)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.changeRoleUser(1, 1);
    });
  }

  /**
   * Test to confirmUser admin failed.
   */
  @Test
  @DisplayName("Test failedConfirmUserNull success")
  public void failedConfirmUserAdminNull() {
    Mockito.when(userDAO.changeRoleUser(user1.getId(), 2)).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.changeRoleUser(1, 2);
    });
  }

  /**
   * Test to GetAllUsers failed.
   */
  @Test
  @DisplayName("Test failedGetAllUsersNull success")
  public void failedGetAllUsersNull() {
    Mockito.when(userDAO.getAllUsers()).thenReturn(null);
    assertThrows(BizException.class, () -> {
      userUCC.getAllUsers();
    });
  }

  /**
   * Test update with success.
   */
  @Test
  @DisplayName("Test a successfull update")
  public void successfullUpdate() throws FileNotFoundException {
    UserDTO user3Test = userFactory.getUser();
    user3Test.setEmail("thomas.doe@hotmail.be");
    user3Test.setPassword("test123");
    user3Test.setRole(0);
    user3Test.setFirstName("Thomas");
    user3Test.setLastName("Doe");
    user3Test.setPhone("0471/89.78.55");
    user3Test.setImage(null);
    user3Test.setVersion(1);

    UserDTO user4Test = userFactory.getUser();
    user4Test.setEmail("thomas.doe@hotmail.be");
    user4Test.setPassword("test123");
    user4Test.setRole(0);
    user4Test.setFirstName("Thomas");
    user4Test.setLastName("Doe");
    user4Test.setPhone("0471/89.78.55");
    user4Test.setImage(null);
    user4Test.setVersion(2);

    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);

    Mockito.when(userDAO.getUserByEmail(user3Test.getEmail())).thenReturn(user3Test);
    Mockito.when(userDAO.updateUser(user3Test)).thenReturn(user4Test);

    UserDTO resultat = userUCC.update(user3Test, input);
    assertEquals(user4Test, resultat);
  }

  /**
   * Test update with success (user has a image).
   */
  @Test
  @DisplayName("Test a successfull update with Image")
  public void successfullUpdateWithImage() throws FileNotFoundException {
    UserDTO user3Test = userFactory.getUser();
    user3Test.setEmail("thomas.doe@hotmail.be");
    user3Test.setPassword("test123");
    user3Test.setRole(0);
    user3Test.setFirstName("Thomas");
    user3Test.setLastName("Doe");
    user3Test.setPhone("0471/89.78.55");
    user3Test.setImage("canape3.jpg");
    user3Test.setVersion(1);

    UserDTO user4Test = userFactory.getUser();
    user4Test.setEmail("thomas.doe@hotmail.be");
    user4Test.setPassword("test123");
    user4Test.setRole(0);
    user4Test.setFirstName("Thomas");
    user4Test.setLastName("Doe");
    user4Test.setPhone("0471/89.78.55");
    user4Test.setImage("canape3.jpg");
    user4Test.setVersion(2);

    File file = new File(
        "src/test/ressources/inputImage/canape3.jpg");
    InputStream input = new FileInputStream(file);

    Mockito.when(userDAO.getUserByEmail(user3Test.getEmail())).thenReturn(user3Test);
    Mockito.when(userDAO.updateUser(user3Test)).thenReturn(user4Test);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      UserDTO resultat = userUCC.update(user3Test, input);
      assertEquals(user4Test, resultat);

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
      Files.copy(file.toPath(),
          Path.of(Config.getProperty("ImagePath") + Paths.get("canape3.jpg")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Test update with bad version.
   */
  @Test
  @DisplayName("Test failed with bad version")
  public void failUpdateBadVersion() throws FileNotFoundException {
    UserDTO user3Test = userFactory.getUser();
    user3Test.setEmail("thomas.doe@hotmail.be");
    user3Test.setPassword("test123");
    user3Test.setRole(0);
    user3Test.setFirstName("Thomas");
    user3Test.setLastName("Doe");
    user3Test.setPhone("0471/89.78.55");
    user3Test.setImage(null);
    user3Test.setVersion(1);

    UserDTO user4Test = userFactory.getUser();
    user4Test.setEmail("thomas.doe@hotmail.be");
    user4Test.setPassword("test123");
    user4Test.setRole(0);
    user4Test.setFirstName("Thomas");
    user4Test.setLastName("Doe");
    user4Test.setPhone("0471/89.78.55");
    user4Test.setImage(null);
    user4Test.setVersion(1);

    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);

    Mockito.when(userDAO.getUserByEmail(user3Test.getEmail())).thenReturn(user3Test);
    Mockito.when(userDAO.updateUser(user3Test)).thenReturn(user4Test);

    assertThrows(BizException.class, () -> {
      userUCC.update(user3Test, input);
    });

  }

  /**
   * Test update with user dont exist.
   */
  @Test
  @DisplayName("Test failed with user dont exist")
  public void failUpdateUserNoExist() throws FileNotFoundException {
    UserDTO user3Test = userFactory.getUser();
    user3Test.setEmail("thomas.doe@hotmail.be");
    user3Test.setPassword("test123");
    user3Test.setRole(0);
    user3Test.setFirstName("Thomas");
    user3Test.setLastName("Doe");
    user3Test.setPhone("0471/89.78.55");
    user3Test.setImage(null);
    user3Test.setVersion(1);

    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);

    Mockito.when(userDAO.getUserByEmail(user3Test.getEmail())).thenReturn(null);

    assertThrows(BizException.class, () -> {
      userUCC.update(user3Test, input);
    });

  }

  /**
   * Test update with user with no file.
   */
  @Test
  @DisplayName("Test failed with file dont exist")
  public void failUpdateUserWithFile() throws FileNotFoundException {
    UserDTO user3Test = userFactory.getUser();
    user3Test.setEmail("thomas.doe@hotmail.be");
    user3Test.setPassword("test123");
    user3Test.setRole(0);
    user3Test.setFirstName("Thomas");
    user3Test.setLastName("Doe");
    user3Test.setPhone("0471/89.78.55");
    user3Test.setImage("canape.jpg");
    user3Test.setVersion(1);

    UserDTO user4Test = userFactory.getUser();
    user4Test.setEmail("thomas.doe@hotmail.be");
    user4Test.setPassword("test123");
    user4Test.setRole(0);
    user4Test.setFirstName("Thomas");
    user4Test.setLastName("Doe");
    user4Test.setPhone("0471/89.78.55");
    user3Test.setImage("canape.jpg");
    user4Test.setVersion(2);

    File file = new File(
        "src/test/ressources/canape.jpg");
    InputStream input = new FileInputStream(file);
    try {
      input.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Mockito.when(userDAO.getUserByEmail(user3Test.getEmail())).thenReturn(user3Test);
    Mockito.when(userDAO.updateUser(user3Test)).thenReturn(user4Test);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      assertThrows(RuntimeException.class, () -> {
        userUCC.update(user3Test, input);
      });

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Test update with user with new file.
   */
  @Test
  @DisplayName("Test failed with user dont exist")
  public void successUpdateUserWithFile() throws IOException {

    UserDTO user3Test = userFactory.getUser();
    user3Test.setEmail("thomas.doe@hotmail.be");
    user3Test.setPassword("test123");
    user3Test.setRole(0);
    user3Test.setFirstName("Thomas");
    user3Test.setLastName("Doe");
    user3Test.setPhone("0471/89.78.55");
    user3Test.setImage("canape2.jpg");
    user3Test.setVersion(1);

    UserDTO user4Test = userFactory.getUser();
    user4Test.setEmail("thomas.doe@hotmail.be");
    user4Test.setPassword("test123");
    user4Test.setRole(0);
    user4Test.setFirstName("Thomas");
    user4Test.setLastName("Doe");
    user4Test.setPhone("0471/89.78.55");
    user3Test.setImage("canape2.jpg");
    user4Test.setVersion(2);

    File file = new File(
        "src/test/ressources/inputImage/canape2.jpg");
    InputStream input = new FileInputStream(file);

    Mockito.when(userDAO.getUserByEmail(user3Test.getEmail())).thenReturn(user3Test);
    Mockito.when(userDAO.updateUser(user3Test)).thenReturn(user4Test);

    final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
    UUID uuid = UUID.fromString(id);
    String newFileNameArtificiel = id + ".jpg";
    try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
      mockedUUID.when(UUID::randomUUID).thenReturn(uuid);

      UserDTO resultat = userUCC.update(user3Test, input);

      assertEquals(user4Test, resultat);

      input.close();

      Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(newFileNameArtificiel)));
      Files.copy(file.toPath(),
          Path.of(Config.getProperty("ImagePath") + Paths.get("canape2.jpg")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Test getFileExtension.
   */
  @Test
  @DisplayName("Test get files extension")
  public void getFilesExtension() {
    String filename1 = "file.txt";
    String filename2 = "file.docx";
    String filename3 = "file";
    String filename4 = ".file";
    String filename5 = "";

    String ext1 = UserUCC.getFileExtension(filename1);
    String ext2 = UserUCC.getFileExtension(filename2);
    String ext3 = UserUCC.getFileExtension(filename3);
    String ext4 = UserUCC.getFileExtension(filename4);
    String ext5 = UserUCC.getFileExtension(filename5);

    assertEquals("txt", ext1);
    assertEquals("docx", ext2);
    assertEquals("", ext3);
    assertEquals("", ext4);
    assertEquals("", ext5);
  }

}
