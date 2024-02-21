package be.vinci.pae.domain;

import be.vinci.pae.exception.BizException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * ObjectUCC Inteface.
 */
public interface ObjectUCC {

  /**
   * Returns the file extension of a file.
   *
   * @param fileName the name of the file
   * @return the file extension
   */
  static String getFileExtension(String fileName) {
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    } else {
      return "";
    }
  }

  /**
   * change the state of the object to put it in WK state check if the state was in accept state
   * before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @return objectNode
   */
  ObjectDTO dropObjectInTheWorkShop(int idObject);

  /**
   * change the state of the object to put it in AS state check if the state was in accept  state or
   * in worckShop state before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @return objectNode
   */
  ObjectDTO dropObjectInTheStore(int idObject);

  /**
   * change the state of the object to put it in PS state check if the state was in the store state
   * before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @param price    the price of the object
   * @return objectNode
   */
  ObjectDTO dropObjectForSale(int idObject, double price);

  /**
   * get all objects.
   *
   * @param typeOfVisibility true if want to get visible true/false (propose)
   * @param proposeObject    true if want to get all type of Object
   * @param homePage         true if want to get all type of Object that can be on homePage true =>
   *                         (state = put on sale (PS) OR vendu - sold (SO)), false => all state
   * @return List of objects
   */
  List<ObjectDTO> getAllObjects(boolean typeOfVisibility, boolean proposeObject, boolean homePage);

  /**
   * get all objects.
   *
   * @return List of objects
   */
  List<ObjectDTO> getAllObjects();

  /**
   * get all objects.
   *
   * @return List of objects
   */
  List<ObjectDTO> getAbsolutelyAllObjects();

  /**
   * Retrieves an ObjectDTO by its ID.
   *
   * @param idObject The ID of the ObjectDTO to retrieve.
   * @return An ObjectDTO representing the Object retrieved, null if doesn't exist.
   */
  ObjectDTO getObjectById(int idObject);

  /**
   * Refuses an object with the specified ID.
   *
   * @param idObject            the ID of the object to refuse
   * @param refusalNotification the message of refusal
   * @return an ObjectDTO representing the refused object, or null if the object cannot be refused
   */
  ObjectDTO refuseObject(int idObject, String refusalNotification);

  /**
   * change the state of the object to put it in accept state check if the state was in the workShop
   * state before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @return objectNode
   */
  ObjectDTO acceptObject(int idObject);

  /**
   * change the state of the object to put it in SO state check if the state was put on sale (PS)
   * state before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @return ObjectDTO
   */
  ObjectDTO dropObjectSold(int idObject);

  /**
   * get all objects by id.
   *
   * @param idType the id of the type of object
   * @return List of objects
   */
  List<ObjectDTO> getObjectsByType(int idType);

  /**
   * This method retrieves a list of all object pictures belonging to the user with the specified
   * ID.
   *
   * @param id The ID of the user whose object pictures are being retrieved.
   * @return A list of ObjectDTO objects representing the object pictures belonging to the user.
   * @throws BizException If no objects matching the specified parameters are found in the
   *                      database.
   */
  List<ObjectDTO> getAllObjectsOfAUser(int id);

  /**
   * To update the fields "description", "url", and "idType" of an object with its ID.
   *
   * @param idObject        the ID of the Object to update
   * @param desciption      the desciption of the Object to update
   * @param file            the file of the Object to update
   * @param fileDisposition the fileDisposition of the Object to update
   * @param idType          the idType of the Object to update
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO updateObjectById(int idObject, String desciption, InputStream file,
      FormDataContentDisposition fileDisposition, int idType);

  /**
   * Proposes an object for donation or recovery by starting a transaction and calling the
   * proposeObject() method in the objectDAO.
   *
   * @param object the object to propose
   * @return id of object if the object proposal was successfully added to the database
   * @throws BizException if the object is null (proposeObjectWithPhoneNumber())
   */
  int proposeObject(ObjectDTO object);

  /**
   * Gets all the statistics for the objects on a given time frame.
   *
   * @param startingDate date from which to start searching
   * @param endingDate   date from which to stop searching
   * @return true if the object proposal was successfully added to the database, false otherwise
   * @throws BizException if the object is null (proposeObjectWithPhoneNumber())
   */
  Map<String, String> getStatistics(String startingDate, String endingDate);

  /**
   * change the state of the object to put it in AS state check if the state was in accept  state or
   * in worckShop state before if not, don't change the state.
   *
   * @param idObject        the id of the object
   * @param file            the file of the object
   * @param fileDisposition the fileDisposition of the object
   * @return objectNode
   */
  boolean upload(int idObject, InputStream file, FormDataContentDisposition fileDisposition);

  /**
   * To withdraw an object with its ID.
   *
   * @param idObject the ID of the object to withdraw
   * @return the object withdrawn
   */
  ObjectDTO withdrawObject(int idObject);


  /**
   * change the state of the object to put it in SO state check if the state was put on sale (DW)
   * state before if not, don't change the state. And put the price.
   *
   * @param idObject the id of the object
   * @param price    the price of the object of which the state is being updated
   * @return ObjectDTO
   */
  ObjectDTO dropObjectSoldDirectly(int idObject, double price);
}
