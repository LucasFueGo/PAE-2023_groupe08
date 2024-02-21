package be.vinci.pae.domain;

import java.util.Date;

/**
 * AvailableImpl class.
 */

public class AvailableImpl implements Available {

  private int id;
  private Date day;
  private boolean mornig;

  /**
   * Returns the id of the Available.
   *
   * @return the id of the Available
   */

  public int getId() {
    return id;
  }

  /**
   * Setter method for the ID of the Object Available.
   *
   * @param id the integer value to set as the ID
   */

  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the Day of the Available.
   *
   * @return the Day of the Available
   */

  public Date getDay() {
    return day;
  }

  /**
   * Setter method for the day of the Object Available.
   *
   * @param day the Date value to set as the day
   */

  public void setDay(Date day) {
    this.day = day;
  }

  /**
   * Returns the true if it's in the mornig of the Available.
   *
   * @return the true if it's in the mornig of the Available
   */

  public boolean isMornig() {
    return mornig;
  }

  /**
   * Setter method for the mornig of the Object Available.
   *
   * @param mornig the boolean value to set as the mornig
   */

  public void setMornig(boolean mornig) {
    this.mornig = mornig;
  }


}
