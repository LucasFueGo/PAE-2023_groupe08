package be.vinci.pae.services;

import be.vinci.pae.domain.ObjectDTO;
import be.vinci.pae.exception.FatalException;
import java.util.List;
import java.util.Map;

/**
 * objectDataService class.
 */

public interface ObjectDAO {

  /**
   * Retrieves a Object with the specified ID.
   *
   * @param id the ID of the Object to retrieve
   * @return the Object with the specified ID, or null if no Object was found
   */
  ObjectDTO getObjectById(int id);


  /**
   * To change the state of an object to 'in-store', use its ID .
   *
   * @param id the ID of the Object to retrieve
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO dropObjectInTheWorkShop(int id);

  /**
   * To change the state of an object to 'for-sale', use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO dropObjectInTheStore(int idObject);

  /**
   * To change the state of an object to 'PS', use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @param price    the price of the Object
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO dropObjectForSale(int idObject, double price);

  /**
   * get all objects.
   *
   * @param typeOfVisibility true if want to get visible true/false (propose)
   * @param proposeObject    true if want to get all type of Object
   * @param homePage         true if want to get all type of Object that can be on homePage true =>
   *                         (state = put on sale (PS) OR vendu - sold (SO)), false => all state
   * @return list of objects
   */
  List<ObjectDTO> getAllObjects(boolean typeOfVisibility, boolean proposeObject, boolean homePage);

  /**
   * get all objects.
   *
   * @return list of objects
   */
  List<ObjectDTO> getAllObjects();

  /**
   * get all objects.
   *
   * @return list of objects
   */
  List<ObjectDTO> getAbsolutelyAllObjects();

  /**
   * To change the state of an object to 'RE' refused, use its ID .
   *
   * @param idObject            the ID of the Object to retrieve
   * @param refusalNotification the message of refusal
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO refuseObject(int idObject, String refusalNotification);

  /**
   * get all objects by user.
   *
   * @param id the ID of the object.
   * @return the objects changed
   */
  ObjectDTO acceptObject(int id);

  /**
   * To change the state of an object to 'SO'(sold), use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO dropObjectSold(int idObject);

  /**
   * get all objects by type.
   *
   * @param idType is the type of objects
   * @return list of objects
   */
  List<ObjectDTO> getAllObjectsByType(int idType);

  /**
   * get all objects by type.
   *
   * @param startingDate the date from which to start searching
   * @param endingDate   the date from which to stop searching
   * @return list of objects
   */
  Map<String, String> getStatistics(String startingDate, String endingDate);


  /**
   * This method retrieves a list of all object pictures belonging to the user with the specified
   * ID.
   *
   * @param id The ID of the user whose object pictures are being retrieved.
   * @return A list of ObjectDTO objects representing the object pictures belonging to the user.
   * @throws FatalException If a SQL error occurs while interacting with the database, a
   *                        FatalException with a message describing the error and the original
   *                        exception is thrown.
   */
  List<ObjectDTO> getAllObjectsOfAUser(int id);

  /**
   * To update the fields "description", "url", and "idType" of an object with its ID.
   *
   * @param idObject   the ID of the Object to update
   * @param desciption the desciption of the Object to update
   * @param url        the url of the Object to update
   * @param idType     the id of type of the Object to update
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO updateObjectById(int idObject, String desciption, String url, int idType);

  /**
   * Inserts an object proposal into the riezrecup.objects table.
   *
   * @param objectDTO the object to insert
   * @return true if the proposal was successfully added to the table, false otherwise
   * @throws FatalException if a SQL error occurs with the method proposeObject()
   */
  int proposeObject(ObjectDTO objectDTO);


  /**
   * Inserts an object proposal into the riezrecup.objects table.
   *
   * @param id          the id to insert
   * @param newFileName the newFileName to insert
   * @return
   * @throws FatalException if a SQL error occurs with the method proposeObject()
   */
  ObjectDTO upload(int id, String newFileName);

  /**
   * create a notification for an object.
   *
   * @param idObject the id of the object
   * @param message  the message of the notification
   * @param idUser   the id of the user
   */
  void createNotificationForAnObject(int idObject, String message, int idUser, int role);

  /**
   * To update the field "state" of an object with its ID.
   *
   * @param idObject the ID of the Object to update
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  ObjectDTO withdrawObject(int idObject);

  /**
   * get the latest's object version.
   *
   * @param idObject the ID of the Object of which to get the version
   * @return The int value of the object's version
   */
  int getVersionObject(int idObject);

  /**
   * Update the state and the price of the oject.
   *
   * @param idObject the ID of the Object to update
   * @param price    the selling price of the object
   * @return The actual version of the object
   */
  ObjectDTO dropObjectSoldDirectly(int idObject, double price);
}
