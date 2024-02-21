package be.vinci.pae.domain;

/**
 * FilterFactory Interface.
 */
public interface FilterFactory {

  /**
   * Returns a new FilterImpl object.
   *
   * @return a new FilterImpl object
   */
  FilterDTO getFilter();
}
