package be.vinci.pae.domain;

/**
 * FilterFactory.
 */
public class FilterFactoryImpl implements FilterFactory {

  /**
   * Returns a new FilterImpl object.
   *
   * @return a new FilterImpl object
   */
  @Override
  public FilterDTO getFilter() {
    return new FilterImpl();
  }
}
