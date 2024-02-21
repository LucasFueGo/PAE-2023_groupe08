package be.vinci.pae.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.mindrot.jbcrypt.BCrypt;

/**
 * UserImpl class.
 */
class UserImpl implements User {

  private int id;
  private String email;
  @JsonIgnore
  private String password;
  private String firstname;
  private String lastname;
  private int role;
  private String phone;
  private String imageUrl;
  private int version;

  /**
   * Returns the role of this user.
   *
   * @return the role of this user
   */
  @Override
  public int getRole() {
    return role;
  }

  /**
   * Setter method for the ID of the User object.
   *
   * @param role the integer value to set as the ID
   */
  @Override
  public void setRole(int role) {
    this.role = role;
  }

  /**
   * Returns the login of this user.
   *
   * @return the login of this user
   */
  @Override
  public String getEmail() {
    return email;
  }

  /**
   * Set the login of the user.
   *
   * @param email the login of the user.
   */
  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns the ID of the user.
   *
   * @return the ID of the user
   */
  @Override
  public int getId() {
    return id;
  }

  /**
   * Setter method for the ID of the User object.
   *
   * @param id the integer value to set as the ID
   */
  @Override
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the password of the user.
   *
   * @return the password of the user
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Setter method for the password of the User object.
   *
   * @param password the integer value to set as the ID
   */
  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Returns the phone of this user.
   *
   * @return the phone of this user
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Set the phone of the user.
   *
   * @param phone the phone of the user.
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Override
  public String getImage() {
    return imageUrl;
  }

  @Override
  public void setImage(String url) {
    this.imageUrl = url;
  }

  /**
   * Returns the first name of this user.
   *
   * @return the first name of this user
   */
  @Override
  public String getFirstName() {
    return firstname;
  }

  /**
   * Set the first name of the user.
   *
   * @param firstname the first name of the user.
   */
  @Override
  public void setFirstName(String firstname) {
    this.firstname = firstname;
  }

  /**
   * Returns the last name of this user.
   *
   * @return the last name of this user
   */
  @Override
  public String getLastName() {
    return lastname;
  }

  /**
   * Set the last name of the user.
   *
   * @param lastname the last name of the user.
   */
  @Override
  public void setLastName(String lastname) {
    this.lastname = lastname;
  }

  /**
   * Returns the version of a user.
   *
   * @return the version of a user
   */
  @Override
  public int getVersion() {
    return version;
  }

  /**
   * Setter method for the version of a user.
   *
   * @param version the integer value to set as the version
   */
  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * Checks if the given password matches the user's password.
   *
   * @param password the password to check
   * @return true if the password matches, false otherwise
   */
  @Override
  public boolean checkPassword(String password) {
    return BCrypt.checkpw(password, this.password);
  }

  /**
   * Hashes the given password using BCrypt and returns the result.
   *
   * @param password the password to hash
   * @return the hashed password
   */
  @Override
  public String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  /**
   * Returns a string representation of this user, including their ID, login, and password.
   *
   * @return a string representation of this user
   */
  @Override
  public String toString() {
    return "{id=" + id + ", login='" + email + '\'' + ", password='" + password + '\''
        + ", firstname='" + firstname + '\'' + ", lastname='" + lastname + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserImpl user = (UserImpl) o;
    return Objects.equals(email, user.email) && Objects.equals(firstname,
        user.firstname) && Objects.equals(lastname, user.lastname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, firstname, lastname);
  }
}
