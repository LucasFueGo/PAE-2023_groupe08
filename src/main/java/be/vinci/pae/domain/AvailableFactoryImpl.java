package be.vinci.pae.domain;

/**
 * AvailableFactoryImpl class.
 */
public class AvailableFactoryImpl implements AvailableFactory {


  /**
   * Returns a new AvailableImpl object.
   *
   * @return a new AvailableImpl object
   */
  @Override
  public AvailableDTO getAvailable() {
    return new AvailableImpl();
  }
}
