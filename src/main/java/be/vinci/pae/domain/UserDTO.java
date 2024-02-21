package be.vinci.pae.domain;

/**
 * UserDTO class.
 */
public interface UserDTO {

  /**
   * Returns the role of this user.
   *
   * @return the role of this user
   */
  int getRole();

  /**
   * Set the role of the user.
   *
   * @param role the role of the user.
   */
  void setRole(int role);

  /**
   * Returns the phone of this user.
   *
   * @return the phone of this user
   */
  String getPhone();

  /**
   * Set the phone of the user.
   *
   * @param phone the phone of the user.
   */
  void setPhone(String phone);

  /**
   * Returns the url image of this user.
   *
   * @return the url image of this user
   */
  String getImage();

  /**
   * Set the url image of the user.
   *
   * @param url the url image of the user.
   */
  void setImage(String url);

  /**
   * Returns the login of this user.
   *
   * @return the login of this user
   */
  String getEmail();

  /**
   * Set the login of the user.
   *
   * @param email the login of the user.
   */
  void setEmail(String email);

  /**
   * Returns the ID of the user.
   *
   * @return the ID of the user
   */
  int getId();

  /**
   * Setter method for the ID of the User object.
   *
   * @param id the integer value to set as the ID
   */
  void setId(int id);

  /**
   * Returns the password of the user.
   *
   * @return the password of the user
   */
  String getPassword();

  /**
   * Setter method for the password of the User object.
   *
   * @param password the integer value to set as the ID
   */
  void setPassword(String password);

  /**
   * Returns the firstname of the user.
   *
   * @return the firstname of the user
   */
  String getFirstName();

  /**
   * Returns none.
   *
   * @param firstname the firstname of user
   */
  void setFirstName(String firstname);

  /**
   * Returns the lastname of the user.
   *
   * @return the lastname of the user
   */
  String getLastName();

  /**
   * Returns none.
   *
   * @param lastname the firstname of user
   */
  void setLastName(String lastname);

  /**
   * Returns the version of a user.
   *
   * @return the version of a user
   */
  int getVersion();

  /**
   * Setter method for the version of a user.
   *
   * @param version the integer value to set as the version
   */
  void setVersion(int version);
}
