package be.vinci.pae.domain;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.DALServices;
import be.vinci.pae.services.UserDAO;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * UserUCCImpl class.
 */
public class UserUCCImpl implements UserUCC {

  @Inject
  private UserDAO userDAO;

  @Inject
  private UserFactory userFactory;
  @Inject
  private DALServices dalServices;


  /**
   * Authenticates a user with the given login and password and generates a JWT token authentication
   * fails or an error occurs.
   *
   * @param email    the login of the user to authenticate
   * @param password the password of the user to authenticate
   * @return an ObjectNode containing the JWT token, user ID, and login, or null if the
   */
  @Override
  public UserDTO login(String email, String password) {
    try {
      dalServices.startTransaction();
      User user = (User) userDAO.getUserByEmail(email);
      if (user == null) {
        throw new BizException("USER (no user found) : User with the email " + email
            + " tried to login but no user matches that account.");
      }
      if (!user.checkPassword(password)) {
        throw new BizException("USER (incorrect password) : User with the email " + email
            + " tried to login with the wrong password.");
      }
      dalServices.commitTransaction();
      return user;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * Updates a user with the given information.
   *
   * @param userToUpdate the user To Update
   * @param file         the file of the user to register
   * @return an ObjectNode containing the JWT token, user ID, and login, or null if the
   */
  @Override
  public UserDTO update(UserDTO userToUpdate, InputStream file) {
    try {
      dalServices.startTransaction();
      String url;

      UserDTO userExist = userDAO.getUserByEmail(userToUpdate.getEmail());

      if (userExist == null) {
        throw new BizException(
            "USER (no user found) : no user with this email : " + userToUpdate.getEmail() + ".");
      }

      if (userToUpdate.getImage() != null) {
        UUID uuid = UUID.randomUUID();

        String extension = UserUCC.getFileExtension(userToUpdate.getImage());

        String newFileName = uuid + "." + extension;

        Files.copy(file,
            Path.of(Config.getProperty("ImagePath") + Paths.get(newFileName)));

        url = newFileName;

        if (userExist.getImage() != null) {
          Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(userExist.getImage())));
        }
        file.close();
      } else {
        url = userExist.getImage();
      }

      userToUpdate.setImage(url);

      User userNewInfo = (User) userFactory.getUser();
      if (userToUpdate.getPassword().equals("")) {
        userToUpdate.setPassword(userToUpdate.getPassword());
      } else {
        String hash = userNewInfo.hashPassword(userToUpdate.getPassword());
        userToUpdate.setPassword(hash);
      }

      int oldVersion = userExist.getVersion();
      UserDTO user = userDAO.updateUser(userToUpdate);
      int newVersion = user.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException("The version is not valid (update)");
      }
      dalServices.commitTransaction();
      return user;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      try {
        throw e;
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * Returns the user with the given ID.
   *
   * @param id the ID of the user to get
   * @return the user with the given ID
   */
  public UserDTO getUserById(int id) {
    try {
      dalServices.startTransaction();
      UserDTO user = userDAO.getUserById(id);
      if (user == null) {
        throw new BizException("The user is null (getUserById)");
      }
      dalServices.commitTransaction();
      return user;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * Register a user with the given fields and generates a UserDTO.
   *
   * @param userToTryToRegister the user
   * @param file                the user's file
   * @return an userDTO based on the fields
   */
  @Override
  public UserDTO register(UserDTO userToTryToRegister, InputStream file) {
    try {
      dalServices.startTransaction();

      UserDTO user = userDAO.getUserByEmail(userToTryToRegister.getEmail());
      if (user != null) {
        throw new BizException("Someone try to register with a email that exist");
      }

      UUID uuid = UUID.randomUUID();

      String extension = UserUCC.getFileExtension(userToTryToRegister.getImage());

      String newFileName = uuid + "." + extension;
      Files.copy(file,
          Path.of(Config.getProperty("ImagePath") + Paths.get(newFileName)));

      userToTryToRegister.setImage(newFileName);
      file.close();
      User userToDb = (User) userFactory.getUser();
      String hash = userToDb.hashPassword(userToTryToRegister.getPassword());
      userToTryToRegister.setPassword(hash);

      user = userDAO.createOne(userToTryToRegister);

      if (user == null) {
        throw new BizException("The user is null (register)");
      }
      dalServices.commitTransaction();
      return user;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      try {
        throw e;
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }


  /**
   * Get all users.
   *
   * @return a list of all users
   */
  public List<UserDTO> getAllUsers() {
    try {
      dalServices.startTransaction();
      List<UserDTO> user = userDAO.getAllUsers();
      if (user == null) {
        throw new BizException("Users are null (confirmUser)");
      }
      dalServices.commitTransaction();
      return user;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }

  }

  /**
   * Confirm a user with the given id.
   *
   * @param id the id of the user to confirm
   * @return an userDTO based on the id
   */
  public UserDTO changeRoleUser(int id, int role) {
    try {
      dalServices.startTransaction();
      UserDTO user = userDAO.getUserById(id);
      if (user == null) {
        throw new BizException("The user is null (confirmUser)");
      }
      int oldVersion = user.getVersion();
      user = userDAO.changeRoleUser(id, role);
      int newVersion = user.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException("The version is not valid (confirmUser)");
      }
      dalServices.commitTransaction();
      return user;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }


  }

}
