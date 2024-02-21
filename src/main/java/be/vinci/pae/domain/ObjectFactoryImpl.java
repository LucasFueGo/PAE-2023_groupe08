package be.vinci.pae.domain;

/**
 * ObjectFactoryImpl class.
 */
public class ObjectFactoryImpl implements ObjectFactory {

  /**
   * Returns a new ObjectImpl object.
   *
   * @return a new ObjectImpl object
   */
  @Override
  public ObjectDTO getObject() {
    return new ObjectImpl();
  }
}
