package be.vinci.pae.services;

import be.vinci.pae.domain.UserDTO;
import java.util.List;

/**
 * userDataService class.
 */
public interface UserDAO {


  /**
   * Retrieves a User with the specified ID.
   *
   * @param id the ID of the User to retrieve
   * @return the User with the specified ID, or null if no User was found
   */
  UserDTO getUserById(int id);

  /**
   * Updates a user.
   *
   * @param userToUpdate userDTO of the user's updated information
   * @return the User with updated information
   */
  UserDTO updateUser(UserDTO userToUpdate);

  /**
   * Retrieves a single User from the database by their login.
   *
   * @param email the login of the user to retrieve
   * @return the User object representing the retrieved user, or null if the user does not exist
   */
  UserDTO getUserByEmail(String email);

  /**
   * Retrieves a list of all Users from the database.
   *
   * @return A list of all Users in the database
   */
  List<UserDTO> getAllUsers();


  /**
   * Retrieves a list of all Users from the database.
   *
   * @param userToDb is the UserDTO that will be complete
   * @return true if the function is completed
   */
  UserDTO createOne(UserDTO userToDb);

  /**
   * Confirm a user with the given id as an helper.
   *
   * @param id   the id of the user to confirm
   * @param role the role to give the the user
   * @return an userDTO based on the id
   */
  UserDTO changeRoleUser(int id, int role);

  /**
   * To get the version of the user.
   *
   * @param idObject the ID of the user to update
   * @return The actual version of the user
   */
  int getVersionUser(int idObject);

}
