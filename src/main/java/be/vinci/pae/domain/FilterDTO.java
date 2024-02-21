package be.vinci.pae.domain;

/**
 * FilterDTO class.
 */
public interface FilterDTO {

  /**
   * Returns the name of filter.
   *
   * @return the name of filter
   */
  String getName();

  /**
   * Set the name of filter.
   *
   * @param name the name of filter.
   */
  void setName(String name);

  /**
   * Returns the id of filter.
   *
   * @return the id of filter
   */
  int getId();

  /**
   * Set the id of filter.
   *
   * @param id the id of filter.
   */
  void setId(int id);
}
