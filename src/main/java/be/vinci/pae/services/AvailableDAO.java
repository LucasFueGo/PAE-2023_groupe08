package be.vinci.pae.services;

import be.vinci.pae.domain.AvailableDTO;
import java.sql.Date;
import java.util.List;

/**
 * AvailableDAO Interface.
 */
public interface AvailableDAO {

  /**
   * add a new available.
   *
   * @param day        with the date
   * @param idTimeSlot with the id of the timeSlot
   * @return true if the availble as been add.
   */
  boolean addAvailabality(Date day, int idTimeSlot);

  /**
   * get all available.
   *
   * @return an ObjectNode containing the available as a JSON object
   */
  List<AvailableDTO> getAllAvaibality();

  /**
   * Returns the available with the specified id.
   *
   * @param availableID The id of the available to return.
   * @return An ObjectNode representing the available.
   */
  AvailableDTO getAvaibalityById(int availableID);


}
