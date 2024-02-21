package be.vinci.pae.domain;

/**
 * DomainFactory Interface.
 */
public interface UserFactory {

  /**
   * Returns a new UserImpl object.
   *
   * @return a new UserImpl object
   */
  UserDTO getUser();
}
