package be.vinci.pae.domain;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.DALServices;
import be.vinci.pae.services.FilterDAO;
import be.vinci.pae.services.ObjectDAO;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * ObjectUCC class.
 */
public class ObjectUCCImpl implements ObjectUCC {

  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private DALServices dalServices;
  @Inject
  private ObjectFactory objectFactory;

  @Inject
  private FilterDAO filterDAO;

  /**
   * Returns the file extension of a file.
   *
   * @param fileName the name of the file
   * @return the file extension
   */
  public static String getFileExtension(String fileName) {
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
   * @return ObjectDTO
   */
  @Override
  public ObjectDTO dropObjectInTheWorkShop(int idObject) {
    try {
      dalServices.startTransaction();
      Object object = (Object) objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no object found) : No object with the ID " + idObject + " has been found.");
      } else if (!object.getState().equals("AC")) {
        throw new BizException(
            "OBJECT (invalid state update) : The object has a state of " + object.getState()
                + ". Cannot updated it to the DW state.");
      }
      int oldVersion = object.getVersion();
      ObjectDTO object2 = objectDAO.dropObjectInTheWorkShop(object.getId());
      if (object2 == null) {
        throw new BizException("The Object is null (dropObjectInTheWorkShop)");
      }
      int newVersion = object2.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException("The version of the object is not valid (dropObjectInTheWorkShop)");
      }
      dalServices.commitTransaction();
      return object2;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * change the state of the object to put it in WK state check if the state was in accept  state or
   * in worckShop state before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @return objectNode
   */
  @Override
  public ObjectDTO dropObjectInTheStore(int idObject) {
    try {
      dalServices.startTransaction();
      Object object = (Object) objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no object found) : No object with the ID \"+ idObject + \" has been found.");
      } else if (!object.getState().equals("AC") && !object.getState().equals("DW")) {
        throw new BizException(
            "OBJECT (invalid state update) : The object has a state of " + object.getState()
                + ". Cannot updated it to the DS state.");
      }
      int oldVersion = object.getVersion();
      ObjectDTO object2 = objectDAO.dropObjectInTheStore(object.getId());
      if (object2 == null) {
        throw new BizException("The Object is null (dropObjectInTheStore)");
      }
      int newVersion = object2.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException("The version of the object is not valid (dropObjectInTheStore)");
      }
      dalServices.commitTransaction();
      return object2;

    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * change the state of the object to put it in SO state check if the state was put on sale (PS)
   * state before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @param price    the price of the object
   * @return ObjectDTO
   */
  @Override
  public ObjectDTO dropObjectForSale(int idObject, double price) {
    try {
      dalServices.startTransaction();
      Object object = (Object) objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no object found) : No object with the ID " + idObject + " has been found.");
      } else if (!object.getState().equals("AS")) {
        throw new BizException(
            "OBJECT (invalid state update) : The object has a state of " + object.getState()
                + ". Cannot updated it to the AS state.");
      }
      int oldVersion = object.getVersion();
      ObjectDTO object2 = objectDAO.dropObjectForSale(object.getId(), price);
      if (object2 == null) {
        throw new BizException("The Object is null (dropObjectForSale)");
      }
      int newVersion = object2.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException("The version of the object is not valid (dropObjectForSale)");
      }
      dalServices.commitTransaction();
      return object2;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * get all objects.
   *
   * @param typeOfVisibility true if want to get visible true/false (propose)
   * @param proposeObject    true if want to get all type of Object
   * @param homePage         true if want to get all type of Object that can be on homePage true =>
   *                         (state = put on sale (PS) OR vendu - sold (SO)), false => all state
   * @return List of objects
   */
  public List<ObjectDTO> getAllObjects(boolean typeOfVisibility, boolean proposeObject,
      boolean homePage) {
    try {
      dalServices.startTransaction();
      List<ObjectDTO> objects = objectDAO.getAllObjects(typeOfVisibility, proposeObject, homePage);
      if (objects == null) {
        throw new BizException("OBJECT (no objects found) : Could not return a list "
            + "of all the objects matching given parameters.");
      }
      dalServices.commitTransaction();
      return objects;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * get all objects.
   *
   * @return List of objects
   */
  public List<ObjectDTO> getAllObjects() {
    try {
      dalServices.startTransaction();
      List<ObjectDTO> objects = objectDAO.getAllObjects();
      if (objects == null) {
        throw new BizException("OBJECT (no objects found) : Could not return a list "
            + "of all the objects matching given parameters.");
      }
      dalServices.commitTransaction();
      return objects;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * get all objects.
   *
   * @return List of objects
   */
  public List<ObjectDTO> getAbsolutelyAllObjects() {
    try {
      dalServices.startTransaction();
      List<ObjectDTO> objects = objectDAO.getAbsolutelyAllObjects();
      if (objects == null) {
        throw new BizException("OBJECT (no objects found) : Could not return a list "
            + "of all the objects matching given parameters.");
      }
      dalServices.commitTransaction();
      return objects;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * Retrieves an ObjectDTO by its ID.
   *
   * @param idObject The ID of the ObjectDTO to retrieve.
   * @return An ObjectDTO representing the Object retrieved, null if doesn't exist.
   */
  public ObjectDTO getObjectById(int idObject) {
    dalServices.startTransaction();
    ObjectDTO object = objectDAO.getObjectById(idObject);
    if (object == null) {
      dalServices.rollbackTransaction();
      throw new BizException(
          "OBJECT (no object found) : Could not find an object matching with id " + idObject);
    }
    dalServices.commitTransaction();
    return object;
  }

  /**
   * Refuses an object with the specified ID.
   *
   * @param idObject the ID of the object to refuse
   * @return an ObjectDTO representing the refused object, or null if the object cannot be refused
   */
  public ObjectDTO refuseObject(int idObject, String refusalNotification) {
    try {
      dalServices.startTransaction();
      Object object = (Object) objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no object found) : No object with the ID " + idObject + " has been found.");
      } else if (!object.getState().equals("PR")) {
        throw new BizException(
            "OBJECT (invalid state update) : The object has a state of " + object.getState()
                + ". Cannot updated it to the RF state.");
      }
      int oldVersion = object.getVersion();
      ObjectDTO object2 = objectDAO.refuseObject(idObject, refusalNotification);
      if (object2 == null) {
        throw new BizException("The Object don't exist (getObjectById)");
      }
      int newVersion = object2.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException("The version of the object is not valid (refuseObject)");
      }
      dalServices.commitTransaction();
      return object2;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * change the state of the object to put it in accept state check if the state was in the workShop
   * state before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @return objectNode
   */
  public ObjectDTO acceptObject(int idObject) {
    try {
      dalServices.startTransaction();
      Object object = (Object) objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no object found) : No object with the ID " + idObject + " has been found.");
      } else if (!object.getState().equals("PR")) {
        throw new BizException(
            "OBJECT (invalid state update) : The object has a state of " + object.getState()
                + ". Cannot updated it to the AC state.");
      }
      int oldVersion = object.getVersion();
      ObjectDTO object2 = objectDAO.acceptObject(idObject);
      if (object2 == null) {
        throw new BizException("The Object is null (acceptObject)");
      }
      int newVersion = object2.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException("The version of the object is not updated (acceptObject)");
      }
      dalServices.commitTransaction();
      return object2;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * get all objects by id.
   *
   * @param idType the id of the type of object
   * @return List of objects
   */
  public List<ObjectDTO> getObjectsByType(int idType) {
    try {
      dalServices.startTransaction();
      List<ObjectDTO> objects = objectDAO.getAllObjectsByType(idType);
      if (objects == null) {
        throw new BizException(
            "OBJECT (no objects found) : Could not find a list of objects with id type " + idType
                + ".");
      }
      dalServices.commitTransaction();
      return objects;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * change the state of the object to put it in SO state check if the state was put on sale (PS)
   * state before if not, don't change the state.
   *
   * @param idObject the id of the object
   * @return ObjectDTO
   */
  @Override
  public ObjectDTO dropObjectSold(int idObject) {
    try {
      dalServices.startTransaction();
      Object object = (Object) objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no object found) : No object with the ID " + idObject + " has been found.");
      } else if (!object.getState().equals("PS")) {
        throw new BizException(
            "OBJECT (invalid state update) : The object has a state of " + object.getState()
                + ". Cannot updated it to the SO state.");
      }
      int oldVersion = object.getVersion();
      ObjectDTO object2 = objectDAO.dropObjectSold(object.getId());

      if (object2 == null) {
        throw new BizException("The Object is null (dropObjectSold)");
      }
      int newVersion = object2.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException(
            "OBJECT (version error) : the object has been modified by another user.");
      }
      dalServices.commitTransaction();
      return object2;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * This method retrieves a list of all object pictures belonging to the user with the specified
   * ID.
   *
   * @param id The ID of the user whose object pictures are being retrieved.
   * @return A list of ObjectDTO objects representing the object pictures belonging to the user.
   * @throws BizException If no objects matching the specified parameters are found in the
   *                      database.
   */
  public List<ObjectDTO> getAllObjectsOfAUser(int id) {
    try {
      dalServices.startTransaction();
      List<ObjectDTO> objects = objectDAO.getAllObjectsOfAUser(id);
      if (objects == null) {
        throw new BizException("OBJECT (no objects found) : Could not return a list "
            + "of all the objects matching given parameters.");
      }
      dalServices.commitTransaction();
      return objects;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

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
  public ObjectDTO updateObjectById(int idObject, String desciption, InputStream file,
      FormDataContentDisposition fileDisposition, int idType) {
    try {
      dalServices.startTransaction();
      String url;

      ObjectDTO object = objectDAO.getObjectById(idObject);

      if (object == null) {
        throw new BizException(
            "OBJECT (no objects found) : no object with this id : " + idObject + ".");
      }

      if (!(fileDisposition.getFileName() == null)) {
        UUID uuid = UUID.randomUUID();
        String extension = getFileExtension(fileDisposition.getFileName());

        String newFileName = uuid + "." + extension;

        Files.copy(file,
            Path.of(Config.getProperty("ImagePath") + Paths.get(newFileName)));

        url = newFileName;
        if (object.getUrl() != null) {
          Files.delete(Path.of(Config.getProperty("ImagePath") + Paths.get(object.getUrl())));
        }

      } else {
        url = object.getUrl();
      }

      List<FilterDTO> idTypes = filterDAO.getAllFilters();
      boolean contains = false;
      for (FilterDTO filter : idTypes) {
        if (filter.getId() == idType) {
          contains = true;
        }
      }
      if (!contains) {
        throw new BizException(
            "OBJECT (no type found) : no type with this id : " + idType + ".");
      }
      if (desciption.length() > 120 || desciption.length() < 1) {
        throw new BizException(
            "OBJECT The description can be a maximum of 120 characters and cannot be empty."
        );
      }

      int oldVersion = object.getVersion();
      object = objectDAO.updateObjectById(idObject, desciption, url, idType);
      int newVersion = object.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException(
            "OBJECT (version error) : the object has been modified by another user.");
      }
      dalServices.commitTransaction();
      return object;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      try {
        throw e;
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * Proposes an object for donation or recovery by starting a transaction and calling the
   * proposeObject() method in the objectDAO.
   *
   * @param object1 the object to be proposed
   * @return id of object if the object proposal was successfully added to the database otherwise
   * @throws BizException if the object is null (proposeObjectWithPhoneNumber())
   */
  public int proposeObject(ObjectDTO object1) {
    try {
      dalServices.startTransaction();

      if (!object1.getOfferingMemberPhone().isEmpty() && !object1.getOfferingMemberPhone()
          .matches("^\\d{4}/\\d{2}\\.\\d{2}\\.\\d{2}$")
          && !object1.getOfferingMemberPhone()
          .matches("\"^04\\\\d{2}[\\\\s]\\\\d{2}[\\\\s]"
              + "\\\\d{2}[\\\\s]\\\\d{2}$\"")) {
        throw new BizException("The phone number is not valid.");
      }

      int idObject = objectDAO.proposeObject(object1);

      if (idObject == 0) {
        throw new BizException("The object is null (proposeObject())");
      }

      dalServices.commitTransaction();
      return idObject;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }


  @Override
  public Map<String, String> getStatistics(String startingDate, String endingDate) {
    dalServices.startTransaction();
    Map<String, String> response = objectDAO.getStatistics(startingDate, endingDate);
    if (response == null) {
      dalServices.rollbackTransaction();
      throw new BizException(
          "OBJECT (no object found) : Could not find an object matching with id ");
    }
    dalServices.commitTransaction();
    return response;
  }

  @Override
  public boolean upload(int idObject, InputStream file,
      FormDataContentDisposition fileDisposition) {
    try {
      dalServices.startTransaction();
      if (!(fileDisposition.getFileName() == null)) {
        UUID uuid = UUID.randomUUID();
        String extension = getFileExtension(fileDisposition.getFileName());
        String newFileName = uuid + "." + extension;
        try {
          Files.copy(file, Path.of(Config.getProperty("ImagePath") + Paths.get(newFileName)));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        int oldVersion = objectDAO.getVersionObject(idObject);
        ObjectDTO objectDTO = objectDAO.upload(idObject, newFileName);
        int newVersion = objectDTO.getVersion();
        if (newVersion - 1 != oldVersion) {
          throw new BizException(
              "OBJECT (version error) : the object has been modified by another user.");
        }
      }
      dalServices.commitTransaction();
      return true;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * To withdraw an object with its ID.
   *
   * @param idObject the ID of the object to withdraw
   * @return the object withdrawn
   */
  public ObjectDTO withdrawObject(int idObject) {
    try {
      dalServices.startTransaction();
      ObjectDTO object = objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no objects found) : no object with this id : " + idObject + ".");
      }
      int oldVersion = object.getVersion();
      object = objectDAO.withdrawObject(idObject);
      int newVersion = object.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException(
            "OBJECT (version error) : the object has been modified by another user.");
      }
      dalServices.commitTransaction();
      return object;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * change the state of the object to put it in SO state check if the state was put on sale (DW)
   * state before if not, don't change the state. And put the price.
   *
   * @param idObject the id of the object
   * @return ObjectDTO
   */
  @Override
  public ObjectDTO dropObjectSoldDirectly(int idObject, double price) {
    try {
      if (price < 0 || price > 10) {
        throw new BizException("the price must be between 0 and 10");
      }
      dalServices.startTransaction();
      Object object = (Object) objectDAO.getObjectById(idObject);
      if (object == null) {
        throw new BizException(
            "OBJECT (no object found) : No object with the ID " + idObject + " has been found.");
      } else if (!object.getState().equals("AS")) {
        throw new BizException(
            "OBJECT (invalid state update) : The object has a state of " + object.getState()
                + ". Cannot updated it to the SO state.");
      }
      int oldVersion = object.getVersion();
      ObjectDTO object2 = objectDAO.dropObjectSoldDirectly(object.getId(), price);

      if (object2 == null) {
        throw new BizException("The Object is null (dropObjectSold)");
      }
      int newVersion = object2.getVersion();
      if (newVersion - 1 != oldVersion) {
        throw new BizException(
            "OBJECT (version error) : the object has been modified by another user.");
      }
      dalServices.commitTransaction();
      return object2;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

}


