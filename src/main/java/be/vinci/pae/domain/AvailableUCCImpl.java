package be.vinci.pae.domain;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.AvailableDAO;
import be.vinci.pae.services.DALServices;
import jakarta.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * AvailableUCCImpl class.
 */
public class AvailableUCCImpl implements AvailableUCC {

  @Inject
  private DALServices dalServices;
  @Inject
  private AvailableDAO availableDAO;


  /**
   * Returns all the available where the date is not past.
   *
   * @return a List with all the available.
   */

  public List<AvailableDTO> getAllAvailable() {
    try {
      dalServices.startTransaction();
      List<AvailableDTO> list = availableDAO.getAllAvaibality();
      if (list == null) {
        throw new BizException("OBJECT (no objects found) : Could not return a list "
            + "of all the Availables.");
      }
      dalServices.commitTransaction();
      return list;

    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * Returns the available with the specified id.
   *
   * @param idAvailable The id of the available to return.
   * @return An AvailableDTO representing the available.
   */

  public AvailableDTO getAvailableById(int idAvailable) {
    if (idAvailable < 0) {
      throw new BizException("The id must be positive");
    }
    try {
      dalServices.startTransaction();
      AvailableDTO element = availableDAO.getAvaibalityById(idAvailable);
      if (element == null) {
        throw new BizException("OBJECT (no objects found) : Could not return "
            + "the Available matching given parameters.");
      }
      dalServices.commitTransaction();
      return element;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }


  /**
   * add a new available.
   *
   * @param day       the Date
   * @param isMorning a boolean ton say if is on the mornig
   * @return true if the availble as been add.
   */
  public boolean putNewAvailable(Date day, boolean isMorning) {
    try {
      dalServices.startTransaction();

      java.sql.Date sqlDate = new java.sql.Date(day.getTime());
      System.out.println(sqlDate);
      if (!sqlDate.after(Timestamp.valueOf(LocalDateTime.now()))) {
        throw new BizException("the day has already passed or is in progress.");
      }
      int morning = 2;
      if (isMorning) {
        morning = 1;
      }
      availableDAO.addAvailabality(sqlDate, morning);
      dalServices.commitTransaction();
      return true;

    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

}
