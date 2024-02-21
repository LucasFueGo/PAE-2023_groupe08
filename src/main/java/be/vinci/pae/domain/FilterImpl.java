package be.vinci.pae.domain;

class FilterImpl implements Filter {

  private String name;
  private int id;

  /**
   * Returns the name of filter.
   *
   * @return the name of filter
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Set the name of filter.
   *
   * @param name the name of filter.
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the id of filter.
   *
   * @return the id of filter
   */
  @Override
  public int getId() {
    return this.id;
  }

  /**
   * Set the id of filter.
   *
   * @param id the id of filter.
   */
  @Override
  public void setId(int id) {
    this.id = id;
  }
}
