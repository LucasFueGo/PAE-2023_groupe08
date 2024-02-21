package be.vinci.pae.domain;

import java.util.Date;

/**
 * Available Interface.
 */
public interface Available extends AvailableDTO {

  /**
   * Returns the id of the Available.
   *
   * @return the id of the Available
   */

  int getId();

  /**
   * Setter method for the ID of the Object Available.
   *
   * @param id the integer value to set as the ID
   */
  void setId(int id);

  /**
   * Returns the Day of the Available.
   *
   * @return the Day of the Available
   */

  Date getDay();

  /**
   * Setter method for the day of the Object Available.
   *
   * @param day the Date value to set as the day
   */

  void setDay(Date day);

  /**
   * Returns the true if it's in the mornig of the Available.
   *
   * @return the true if it's in the mornig of the Available
   */

  boolean isMornig();

  /**
   * Setter method for the mornig of the Object Available.
   *
   * @param mornig the boolean value to set as the mornig
   */

  void setMornig(boolean mornig);


}
