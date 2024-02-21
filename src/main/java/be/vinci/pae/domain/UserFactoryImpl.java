package be.vinci.pae.domain;

/**
 * DomainFactoryImpl class.
 */
public class UserFactoryImpl implements UserFactory {

  /**
   * Returns a new UserImpl object.
   *
   * @return a new UserImpl object
   */
  @Override
  public UserDTO getUser() {
    return new UserImpl();
  }
}
