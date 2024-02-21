package be.vinci.pae.domain;

import java.util.Date;
import java.util.List;

/**
 * AvailableUCC class.
 */

public interface AvailableUCC {

  /**
   * Returns all the available where the date is not past.
   *
   * @return a List with all the available.
   */
  List<AvailableDTO> getAllAvailable();


  /**
   * Returns the available with the specified id.
   *
   * @param idAvailable The id of the available to return.
   * @return An AvailableDTO representing the available.
   */
  AvailableDTO getAvailableById(int idAvailable);


  /**
   * add a new available.
   *
   * @param day       the Date
   * @param isMorning a boolean ton say if is on the mornig
   * @return true if the availble as been add.
   */
  boolean putNewAvailable(Date day, boolean isMorning);

}
