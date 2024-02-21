package be.vinci.pae.domain;

import java.io.InputStream;
import java.util.List;

/**
 * UserUCC Inteface.
 */
public interface UserUCC {

  /**
   * Returns the file extension of a file.
   *
   * @param fileName the name of the file
   * @return the file extension
   */
  static String getFileExtension(String fileName) {
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    } else {
      return "";
    }
  }

  /**
   * Authenticates a user based on their email and password, and returns a JSON object containing.
   * the user's ID and a token if authentication was successful, or an error message if.
   * authentication failed.
   *
   * @param email    the user's email
   * @param password the user's password
   * @return objectNode
   */
  UserDTO login(String email, String password);

  /**
   * Updates a user with the given information.
   *
   * @param userToUpdate the user To Update
   * @param file         the file of the user to register
   * @return an ObjectNode containing the JWT token, user ID, and login, or null if the
   */
  UserDTO update(UserDTO userToUpdate, InputStream file);

  /**
   * Registers a user based on their email and password, and returns a JSON object containing. the
   * user's ID and a token if authentication was successful, or an error message if. authentication
   * failed.
   *
   * @param userToTryToRegister the user
   * @param file                the user's file
   * @return an userDTO based on the fields
   */
  UserDTO register(UserDTO userToTryToRegister, InputStream file);

  /**
   * Confirm a user with the given id.
   *
   * @param i the id of the user to retrieve
   * @return a userDTO object based on the id
   */
  UserDTO getUserById(int i);

  /**
   * Returns a list of all the available users.
   *
   * @return A list of UserDTO objects
   */
  List<UserDTO> getAllUsers();

  /**
   * Confirm a user with the given id.
   *
   * @param id   the id of the user to confirm
   * @param role the role to which update the user
   * @return an userDTO based on the id
   */
  UserDTO changeRoleUser(int id, int role);

}
