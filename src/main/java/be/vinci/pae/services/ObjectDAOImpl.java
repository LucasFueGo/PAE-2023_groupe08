package be.vinci.pae.services;

import be.vinci.pae.domain.ObjectDTO;
import be.vinci.pae.domain.ObjectFactory;
import be.vinci.pae.exception.FatalException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ObjectDataServiceImpl class.
 */
public class ObjectDAOImpl implements ObjectDAO {


  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private ObjectFactory myObjectFactory;


  /**
   * Retrieves a Object with the specified ID.
   *
   * @param id the ID of the Object to retrieve
   * @return the Object with the specified ID, or null if no Object was found
   */
  @Override
  public ObjectDTO getObjectById(int id) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT o.*, t.arrival_time, t.departure_time, d.deposit_date "
            + "FROM riezrecup.objects o, riezrecup.deposit_days d, riezrecup.time_slots t "
            + "WHERE d.id_deposit_day = o.id_deposit_day AND d.id_time_slot = t.id_time_slot "
            + "AND o.id_object = ?")) {
      dalServicesPreparedStatement.setInt(1, id);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        ObjectDTO object = myObjectFactory.getObject();
        rs.next();
        fromResultSetToObjectDTO(rs, object);
        dalServicesPreparedStatement.close();
        return object;
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getObjectById()", e);
    }
  }

  /**
   * To change the state of an object to 'in-workShop', use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  @Override
  public ObjectDTO dropObjectInTheWorkShop(int idObject) {
    return updateObjectState(idObject, "DW");
  }

  /**
   * To change the state of an object to 'in the store', use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  @Override
  public ObjectDTO dropObjectInTheStore(int idObject) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "UPDATE riezrecup.objects o SET state ='AS', visible = TRUE"
            + ", version = version + 1 where o.id_object = ?")) {
      dalServicesPreparedStatement.setInt(1, idObject);
      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getObjectById(idObject);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode dropObjectInTheStore()", e);
    }
  }

  /**
   * To change the state of an object to 'in the store', use its ID .
   *
   * @param startingDate the starting date
   * @param endingDate   the ending date
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  @Override
  public Map<String, String> getStatistics(String startingDate, String endingDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    Date startDate = Date.valueOf(LocalDate.parse(startingDate, formatter));
    Date endDate = Date.valueOf(LocalDate.parse(endingDate, formatter));

    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT COUNT(item_sold_date) as sold_objects, "
            + "COUNT(accepted_offer_date) as accepted_objects, "
            + "COUNT(withdrawal_date) as withdrawn_objects, "
            + "COUNT(store_drop_off_date) as dropped_objects "
            + "FROM riezrecup.objects o "
            + "WHERE accepted_offer_date between ? and ?")) {
      dalServicesPreparedStatement.setDate(1, startDate);
      dalServicesPreparedStatement.setDate(2, endDate);
      ResultSet rs = dalServicesPreparedStatement.executeQuery();
      Map<String, String> arrayOfStatistics = new HashMap<>();
      while (rs.next()) {
        arrayOfStatistics.put("sold-objects", rs.getString(1));
        arrayOfStatistics.put("accepted-objects", rs.getString(2));
        arrayOfStatistics.put("withdrawn-objects", rs.getString(3));
        arrayOfStatistics.put("dropped-objects", rs.getString(4));

      }
      return arrayOfStatistics;

    } catch (SQLException e) {
      throw new FatalException("A SQL error with the method getStatistics()", e);
    }
  }

  /**
   * To change the state of an object to 'PS', use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @param price    the price of the Object
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  @Override
  public ObjectDTO dropObjectForSale(int idObject, double price) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "UPDATE riezrecup.objects o SET state ='PS', visible = TRUE, "
            + "selling_price = ?, version = version + 1 where o.id_object = ?")) {
      dalServicesPreparedStatement.setDouble(1, price);
      dalServicesPreparedStatement.setInt(2, idObject);
      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getObjectById(idObject);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode dropObjectForSale()", e);
    }
  }

  /**
   * get all objects.
   *
   * @param typeOfVisibility true if want to get visible true/false (propose)
   * @param proposeObject    true if want to get all type of Object
   * @param homePage         true if want to get all type of Object that can be on homePage true =>
   *                         (state = put on sale (PS) OR vendu - sold (SO)), false => all state
   * @return list of objects
   */
  public List<ObjectDTO> getAllObjects(boolean typeOfVisibility, boolean proposeObject,
      boolean homePage) {
    List<ObjectDTO> objectList = new ArrayList<>();
    PreparedStatement dalServicesPreparedStatement;
    if (proposeObject) {
      dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
          "SELECT o.*, t.arrival_time, t.departure_time, d.deposit_date "
              + "FROM riezrecup.objects o, riezrecup.deposit_days d, riezrecup.time_slots t "
              + "WHERE d.id_deposit_day = o.id_deposit_day AND d.id_time_slot = "
              + "t.id_time_slot AND o.visible = ? "
              + "AND o.state = 'PR'");
    } else {
      if (homePage) {
        dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
            "SELECT o.*, t.arrival_time, t.departure_time, d.deposit_date "
                + "FROM riezrecup.objects o, riezrecup.deposit_days d, riezrecup.time_slots t "
                + "WHERE d.id_deposit_day = o.id_deposit_day AND d.id_time_slot = t.id_time_slot "
                + "AND (o.state = 'PS' OR o.state = 'SO' OR o.state = 'AS') "
                + "AND o.visible = ?");
      } else {
        dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
            "SELECT o.*, t.arrival_time, t.departure_time, d.deposit_date "
                + "FROM riezrecup.objects o, riezrecup.deposit_days d, riezrecup.time_slots t "
                + "WHERE d.id_deposit_day = o.id_deposit_day AND d.id_time_slot = t.id_time_slot "
                + "AND (o.state = 'PS' OR o.state = 'WS' OR o.state = 'SO' OR "
                + "o.state='AC' OR o.state ='AS' OR o.state='DW') "
                + "AND o.visible = ?");
      }

    }
    try (dalServicesPreparedStatement) {
      dalServicesPreparedStatement.setBoolean(1, typeOfVisibility);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          ObjectDTO object = myObjectFactory.getObject();
          fromResultSetToObjectDTO(rs, object);
          objectList.add(object);
        }
        dalServicesPreparedStatement.close();
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAllObjects(3 params)", e);
    }
    return objectList;
  }

  /**
   * get all objects.
   *
   * @return list of objects
   */
  public List<ObjectDTO> getAllObjects() {
    List<ObjectDTO> objectList = new ArrayList<>();
    PreparedStatement dalServicesPreparedStatement;

    dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT o.*, t.arrival_time, t.departure_time, d.deposit_date "
            + "FROM riezrecup.objects o, riezrecup.deposit_days d, riezrecup.time_slots t "
            + "WHERE d.id_deposit_day = o.id_deposit_day AND d.id_time_slot = "
            + "t.id_time_slot AND (o.state = 'PS' OR o.state = 'WS' OR o.state = 'SO' "
            + "OR o.state='AC' OR o.state ='AS' OR o.state='DW' OR o.state='RE') ");

    try (dalServicesPreparedStatement) {
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          ObjectDTO object = myObjectFactory.getObject();
          fromResultSetToObjectDTO(rs, object);
          objectList.add(object);
        }
        dalServicesPreparedStatement.close();
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAllObjects(0 params)", e);
    }
    return objectList;
  }

  /**
   * get all objects.
   *
   * @return list of objects
   */
  public List<ObjectDTO> getAbsolutelyAllObjects() {
    List<ObjectDTO> objectList = new ArrayList<>();
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT o.*, t.arrival_time, t.departure_time, d.deposit_date "
            + "FROM riezrecup.objects o, riezrecup.deposit_days d, riezrecup.time_slots t "
            + "WHERE d.id_deposit_day = o.id_deposit_day AND d.id_time_slot = "
            + "t.id_time_slot")) {
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          ObjectDTO object = myObjectFactory.getObject();
          fromResultSetToObjectDTO(rs, object);
          objectList.add(object);
        }
        dalServicesPreparedStatement.close();
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAbsolutelyAllObject()", e);
    }
    return objectList;
  }

  private void fromResultSetToObjectDTO(ResultSet rs, ObjectDTO object) throws SQLException {
    object.setId(rs.getInt(1));
    object.setIdUser(rs.getInt(2));
    object.setOfferingMemberPhone(rs.getString(3));
    object.setIdType(rs.getInt(4));
    object.setNameType(getNameType(rs.getInt(4)));
    object.setIdPassage(rs.getInt(5));
    object.setIdTimeSlot(rs.getInt(6));
    object.setDescription(rs.getString(7));
    String state = rs.getString(8);
    String completeState = "null";
    if (state.equals("AS")) {
      completeState = "Arrivé en magasin";
    } else if (state.equals("PS")) {
      completeState = "Mis en vente";
    } else if (state.equals("SO")) {
      completeState = "Vendu";
    } else if (state.equals("PR")) {
      completeState = "Proposé";
    } else if (state.equals("RE")) {
      completeState = "Refusé";
    } else if (state.equals("AC")) {
      completeState = "Accepté";
    } else if (state.equals("DW")) {
      completeState = "Déposé en atelier";
    } else if (state.equals("WS")) {
      completeState = "Retiré de la vente";
    }
    object.setState(state);
    object.setStateComplete(completeState);
    object.setDenialNotification(rs.getString(9));
    object.setSellingPrice(rs.getDouble(10));
    object.setCreatePropositionDate(rs.getDate(11));
    object.setAcceptedDate(rs.getDate(12));
    object.setStoreDropOffDate(rs.getDate(13));
    object.setDateItemSold(rs.getDate(14));
    object.setWithdrawalDate(rs.getDate(15));
    object.setUrl(rs.getString(16));
    object.setVisibility(rs.getBoolean(17));
    object.setVersion(rs.getInt(18));
    object.setArrivalTime(rs.getDouble(19));
    object.setDepartureTime(rs.getDouble(20));
    object.setDatePassage(rs.getDate(21));
  }

  /**
   * get the name of the type of an object.
   *
   * @param idType the ID of the type of the object.
   * @return the name of the type of the object.
   */
  private String getNameType(int idType) {
    String nameType = "";
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT t.name FROM riezrecup.object_types t WHERE t.id_object_type = ?")) {
      dalServicesPreparedStatement.setInt(1, idType);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          nameType = rs.getString(1);
        }
        dalServicesPreparedStatement.close();
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getNameType()", e);
    }
    return nameType;
  }


  /**
   * To change the state of an object to 'RE' refused, use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  public ObjectDTO refuseObject(int idObject, String refusalNotification) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "UPDATE riezrecup.objects o SET state = 'RE', refusal_notification = "
            + "?, version = version + 1 where o.id_object = ?")) {
      dalServicesPreparedStatement.setString(1, refusalNotification);
      dalServicesPreparedStatement.setInt(2, idObject);
      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();

      // create a notification for the object
      ObjectDTO objectDTO = getObjectById(idObject);
      int idUser = objectDTO.getIdUser();
      if (idUser != 0) {
        String description = objectDTO.getDescription();
        String message = description + " a été refusé. La cause est : " + refusalNotification;
        int role = 0;
        createNotificationForAnObject(idObject, message, idUser, role);
      }

      return getObjectById(idObject);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode refuseObject()", e);
    }
  }

  /**
   * get all objects by user.
   *
   * @param id the ID of the object.
   * @return the objects changed
   */
  public ObjectDTO acceptObject(int id) {
    return updateObjectState(id, "AC");
  }

  /**
   * To change the state of an object to 'SO'(sold), use its ID .
   *
   * @param idObject the ID of the Object to retrieve
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  @Override
  public ObjectDTO dropObjectSold(int idObject) {
    return updateObjectState(idObject, "SO");
  }

  private ObjectDTO updateObjectState(int id, String newState) {
    String textRequest = "UPDATE riezrecup.objects o SET state = ? where o.id_object = ?";
    if (newState.equals("AC")) {
      textRequest = "UPDATE riezrecup.objects o SET state = ? , "
          + "accepted_offer_date = ?, version = version + 1 where o.id_object = ?";
    } else if (newState.equals("AS")) {
      textRequest = "UPDATE riezrecup.objects o SET state = ? , "
          + "store_drop_off_date = ?, version = version + 1  where o.id_object = ?";
    } else if (newState.equals("SO")) {
      textRequest = "UPDATE riezrecup.objects o SET state = ? , "
          + "item_sold_date = ?, version = version + 1  where o.id_object = ?";
    } else if (newState.equals("WS")) {
      textRequest = "UPDATE riezrecup.objects o SET state = ? , "
          + "withdrawal_date = ?, version = version + 1  where o.id_object = ?";
    } else if (newState.equals("DW")) {
      textRequest = "UPDATE riezrecup.objects o SET state = ? , "
          + "version = version + 1  where o.id_object = ?";
    }

    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        textRequest)) {
      dalServicesPreparedStatement.setString(1, newState);

      if (newState.equals("AC") || newState.equals("AS") || newState.equals("SO")
          || newState.equals("WS")) {
        Date sqlDate = Date.valueOf(LocalDate.now());
        dalServicesPreparedStatement.setDate(2, sqlDate);
        dalServicesPreparedStatement.setInt(3, id);
      } else {
        dalServicesPreparedStatement.setInt(2, id);
      }
      dalServicesPreparedStatement.executeUpdate();

      if (newState.equals("AC")) {
        // create a notification for the object
        ObjectDTO objectDTO = getObjectById(id);
        int idUser = objectDTO.getIdUser();
        String description = objectDTO.getDescription();
        String message = description + " a été accepté.";
        int role;
        if (idUser == 0) {
          role = 1;
        } else {
          role = 0;
        }
        createNotificationForAnObject(id, message, idUser, role);
      }

      dalServicesPreparedStatement.close();
      return getObjectById(id);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode updateObjectState()", e);
    }
  }

  /**
   * get all objects by type.
   *
   * @param idType is the type of objects
   * @return list of objects
   */

  public List<ObjectDTO> getAllObjectsByType(int idType) {
    List<ObjectDTO> objectList = new ArrayList<>();
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT * FROM riezrecup.objects o WHERE o.visible = true AND o.id_object_type = ?")) {
      dalServicesPreparedStatement.setInt(1, idType);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          ObjectDTO object = myObjectFactory.getObject();
          fromResultSetToObjectDTO(rs, object);
          objectList.add(object);
        }
        dalServicesPreparedStatement.close();
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAllObjectsByType()", e);
    }
    return objectList;
  }

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
  public List<ObjectDTO> getAllObjectsOfAUser(int id) {
    List<ObjectDTO> objectList = new ArrayList<>();
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT o.*, t.arrival_time, t.departure_time, d.deposit_date "
            + "FROM riezrecup.objects o, riezrecup.deposit_days d, riezrecup.time_slots t "
            + "WHERE d.id_deposit_day = o.id_deposit_day AND d.id_time_slot = "
            + "t.id_time_slot AND o.id_user = ?")) {
      dalServicesPreparedStatement.setInt(1, id);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          ObjectDTO object = myObjectFactory.getObject();
          fromResultSetToObjectDTO(rs, object);
          objectList.add(object);
        }
      }
      return objectList;
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAllObjectsPicturesOfUser()", e);
    }
  }

  /**
   * To update the fields "description", "url", and "idType" of an object with its ID.
   *
   * @param idObject   the ID of the Object to update
   * @param desciption the desciption of the Object to update
   * @param url        the url of the Object to update
   * @param idType     the id of type of the Object to update
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  public ObjectDTO updateObjectById(int idObject, String desciption, String url, int idType) {
    try (
        PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
            "UPDATE riezrecup.objects "
                + "SET description = ?, url = ?, id_object_type = ?"
                + ", version = version + 1 WHERE id_object = ?")) {
      dalServicesPreparedStatement.setString(1, desciption);
      dalServicesPreparedStatement.setString(2, url);
      dalServicesPreparedStatement.setInt(3, idType);
      dalServicesPreparedStatement.setInt(4, idObject);

      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getObjectById(idObject);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode updateObjectState()", e);
    }

  }

  /**
   * Inserts an object proposal into the riezrecup.objects table.
   *
   * @param objectDTO the object to insert
   * @return true if the proposal was successfully added to the table, false otherwise
   * @throws FatalException if a SQL error occurs with the method proposeObject()
   */
  public int proposeObject(ObjectDTO objectDTO) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "INSERT INTO riezrecup.objects VALUES "
            + "(DEFAULT,?,?,?,?,?,?,'PR',NULL,NULL,?,NULL,NULL,NULL,NULL,NULL,FALSE, 1) RETURNING "
            + "id_object;")) {
      if (objectDTO.getIdUser() == 0) {
        dalServicesPreparedStatement.setNull(1, objectDTO.getIdUser());
      } else {
        dalServicesPreparedStatement.setInt(1, objectDTO.getIdUser());
      }

      dalServicesPreparedStatement.setString(2, objectDTO.getOfferingMemberPhone());
      dalServicesPreparedStatement.setInt(3, objectDTO.getIdType());
      dalServicesPreparedStatement.setInt(4, objectDTO.getIdPassage());
      dalServicesPreparedStatement.setInt(5, objectDTO.getIdTimeSlot());
      dalServicesPreparedStatement.setString(6, objectDTO.getDescription());
      Date sqlDate = Date.valueOf(LocalDate.now());
      dalServicesPreparedStatement.setDate(7, sqlDate);
      ResultSet rs = dalServicesPreparedStatement.executeQuery();
      int idObject;
      if (rs.next()) {
        idObject = rs.getInt("id_object");
      } else {
        throw new SQLException("problem with propose object Query SQL");
      }

      // create a notification for the object
      ObjectDTO object = getObjectById(idObject);
      int idUser = object.getIdUser();
      String description = object.getDescription();
      String message = description + " a été proposé.";
      int role = 1;
      createNotificationForAnObject(idObject, message, idUser, role);

      dalServicesPreparedStatement.close();

      return idObject;
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode proposeObject()", e);
    }
  }

  @Override
  public ObjectDTO upload(int id, String newFileName) {
    try (
        PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
            "UPDATE riezrecup.objects SET url = ?"
                + ", version = version + 1 WHERE id_object = ?")) {
      dalServicesPreparedStatement.setString(1, newFileName);
      dalServicesPreparedStatement.setInt(2, id);

      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getObjectById(id);

    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode updateObjectState()", e);
    }
  }

  /**
   * To update the field "state" of an object with its ID.
   *
   * @param idObject the ID of the Object to update
   * @return The modified Object with the specified ID, or null if no Object was found
   */
  public ObjectDTO withdrawObject(int idObject) {
    try (
        PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
            "UPDATE riezrecup.objects SET state = 'WS', withdrawal_date = ?, visible = FALSE "
                + ", version = version + 1 WHERE id_object = ?")) {
      Date sqlDate = Date.valueOf(LocalDate.now());
      dalServicesPreparedStatement.setDate(1, sqlDate);

      dalServicesPreparedStatement.setInt(2, idObject);

      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getObjectById(idObject);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode withdrawObject()", e);
    }
  }

  /**
   * create a notification for an object.
   *
   * @param idObject the id of the object
   * @param message  the message of the notification
   * @param idUser   the id of the user
   */
  public void createNotificationForAnObject(int idObject, String message, int idUser, int role) {
    try (PreparedStatement notificationPreparedStatement = dalBackendServices.getPreparedStatement(
        "INSERT INTO riezrecup.notifications "
            + "VALUES (DEFAULT, ?, NOW(), FALSE, ?, ?, ?)")) {
      notificationPreparedStatement.setInt(1, idObject);
      notificationPreparedStatement.setString(2, message);
      notificationPreparedStatement.setInt(3, idUser);
      notificationPreparedStatement.setInt(4, role);
      notificationPreparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the method createNotificationForAnObject()", e);
    }
  }

  /**
   * To get the version of the oject.
   *
   * @param idObject the ID of the Object to update
   * @return The actual version of the object
   */
  public int getVersionObject(int idObject) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT o.version FROM riezrecup.objects o WHERE o.id_object = ?")) {
      dalServicesPreparedStatement.setInt(1, idObject);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        rs.next();
        return rs.getInt("version");
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode updateObjectState()", e);
    }
  }


  /**
   * Update the state and the price of the oject.
   *
   * @param idObject the ID of the Object to update
   * @param price    the selling price of the object
   * @return The actual version of the object
   */
  @Override
  public ObjectDTO dropObjectSoldDirectly(int idObject, double price) {
    try (
        PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
            "UPDATE riezrecup.objects "
                + "SET state = 'SO', selling_price = ?"
                + ", version = version + 1 WHERE id_object = ?")) {
      dalServicesPreparedStatement.setDouble(1, price);
      dalServicesPreparedStatement.setInt(2, idObject);

      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
      return getObjectById(idObject);
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode updateObjectState()", e);
    }

  }

}
