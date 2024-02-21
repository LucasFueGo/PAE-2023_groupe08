package be.vinci.pae.domain;

/**
 * User Interface.
 */
interface User extends UserDTO {

  /**
   * Checks if the given password matches the user's password.
   *
   * @param password the password to check
   * @return true if the password matches, false otherwise
   */
  boolean checkPassword(String password);

  /**
   * Hashes the given password using BCrypt and returns the result.
   *
   * @param password the password to hash
   * @return the hashed password
   */
  String hashPassword(String password);

  boolean equals(Object o);
}
