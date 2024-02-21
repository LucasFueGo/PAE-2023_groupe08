package be.vinci.pae.services;

import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.exception.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDataServiceImpl class.
 */
public class UserDAOImpl implements UserDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private UserFactory userFactory;

  /**
   * Retrieves a single User by ID from the database.
   *
   * @param id The ID of the User to retrieve
   * @return The User with the specified ID, or null if it does not exist
   */
  @Override
  public UserDTO getUserById(int id) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT id_user ,first_name, last_name, email, "
            + "password, phone_number, url, role, version FROM "
            + "riezrecup.users "
            + "WHERE id_user = ?")) {
      dalServicesPreparedStatement.setInt(1, id);
      UserDTO user;
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        if (rs.next()) {
          user = fromResultSetToUserDTO(rs);
        } else {
          user = null;
        }
        dalServicesPreparedStatement.close();
        return user;
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getUserById()", e);
    }
  }

  /**
   * Updates a user.
   *
   * @param userToUpdate userDTO of the user's updated information
   * @return the User with updated information
   */
  @Override
  public UserDTO updateUser(UserDTO userToUpdate) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "UPDATE riezrecup.users SET first_name = ?, last_name = ?, phone_number = ?, "
            + "password = ?, url = ?, version = version + 1 WHERE email = ?")) {
      dalServicesPreparedStatement.setString(1, userToUpdate.getFirstName());
      dalServicesPreparedStatement.setString(2, userToUpdate.getLastName());
      dalServicesPreparedStatement.setString(3, userToUpdate.getPhone());
      dalServicesPreparedStatement.setString(4, userToUpdate.getPassword());
      dalServicesPreparedStatement.setString(5, userToUpdate.getImage());
      dalServicesPreparedStatement.setString(6, userToUpdate.getEmail());

      try {
        dalServicesPreparedStatement.executeUpdate();
        UserDTO user = getUserByEmail(userToUpdate.getEmail());
        dalServicesPreparedStatement.close();
        return user;
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode updateUser", e);
    }
  }

  /**
   * Retrieves a user with the given login.
   *
   * @param email the user's email
   * @return the user with the given email, or null if not found
   */
  @Override
  public UserDTO getUserByEmail(String email) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT id_user ,first_name, last_name, email, "
            + "password, phone_number, url, role, version FROM "
            + "riezrecup.users WHERE email = ?")) {

      dalServicesPreparedStatement.setString(1, email);
      UserDTO user;
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        if (rs.next()) {
          user = fromResultSetToUserDTO(rs);
        } else {
          user = null;
        }
        dalServicesPreparedStatement.close();
        return user;
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getUserByEmail()", e);
    }
  }


  /**
   * Retrieves a single User by ID from the database.
   *
   * @param rs The ResultSet of DB
   * @return The User with the specified ID, or null if it does not exist
   */
  public UserDTO fromResultSetToUserDTO(ResultSet rs) {
    UserDTO user = userFactory.getUser();
    try {
      // rs.next();
      user.setId(rs.getInt(1));
      user.setFirstName(rs.getString(2));
      user.setLastName(rs.getString(3));
      user.setEmail(rs.getString(4));
      user.setPassword(rs.getString(5));
      user.setPhone(rs.getString(6));
      user.setImage(rs.getString(7));
      user.setRole(rs.getInt(8));
      user.setVersion(rs.getInt(9));
      return user;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * create a user with the given fields.
   *
   * @param userToInsert userDTO to add
   * @return true if the function is completed
   */
  @Override
  public UserDTO createOne(UserDTO userToInsert) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "INSERT INTO riezrecup.users VALUES "
            + "(DEFAULT, ?, ?, ?, ?, ?, ?, 0, 1);")) {
      dalServicesPreparedStatement.setString(1, userToInsert.getFirstName());
      dalServicesPreparedStatement.setString(2, userToInsert.getLastName());
      dalServicesPreparedStatement.setString(3, userToInsert.getEmail());
      dalServicesPreparedStatement.setString(4, userToInsert.getPassword());
      dalServicesPreparedStatement.setString(5, userToInsert.getPhone());
      dalServicesPreparedStatement.setString(6, userToInsert.getImage());
      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getUserByEmail(userToInsert.getEmail());
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode createOne()", e);
    }
  }

  /**
   * Retrieves a list of all Users from the database.
   *
   * @return A list of all Users in the database
   */
  public List<UserDTO> getAllUsers() {
    List<UserDTO> userList = new ArrayList<>();
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT id_user ,first_name, last_name, email, password, phone_number, role, url FROM "
            + "riezrecup.users");
        ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
      while (rs.next()) {
        UserDTO user = userFactory.getUser();
        user.setId(rs.getInt("id_user"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(rs.getInt("role"));
        user.setImage(rs.getString("url"));
        userList.add(user);
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAllUsers()", e);
    }
    return userList;
  }

  /**
   * Confirm a user as an helper.
   *
   * @param id the user id
   * @return the user confirmed
   */
  public UserDTO changeRoleUser(int id, int role) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "UPDATE riezrecup.users u SET role = ? , version = version + 1 WHERE u.id_user = ? ")) {
      dalServicesPreparedStatement.setInt(1, role);
      dalServicesPreparedStatement.setInt(2, id);
      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getUserById(id);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode confirmUser()", e);
    }
  }

  /**
   * To get the version of the user.
   *
   * @param idUser the ID of the user to update
   * @return The actual version of the user
   */
  public int getVersionUser(int idUser) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT u.version FROM riezrecup.users u WHERE u.id_user = ?")) {
      dalServicesPreparedStatement.setInt(1, idUser);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        rs.next();
        return rs.getInt("version");
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode updateObjectState()", e);
    }
  }

}