package be.vinci.pae.services;

import be.vinci.pae.domain.AvailableDTO;
import be.vinci.pae.domain.AvailableFactory;
import be.vinci.pae.exception.FatalException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AvailableDAOImpl class.
 */
public class AvailableDAOImpl implements AvailableDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private AvailableFactory myAvalaibleFactory;

  /**
   * add a new available.
   *
   * @param day        with the date
   * @param idTimeSlot with the id of the timeSlot
   * @return true if the availble as been add.
   */
  public boolean addAvailabality(Date day, int idTimeSlot) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "INSERT INTO riezrecup.deposit_days VALUES (DEFAULT,?,?)")) {
      dalServicesPreparedStatement.setDate(1, day);
      dalServicesPreparedStatement.setInt(2, idTimeSlot);
      dalServicesPreparedStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode addAvailabality()", e);
    }
  }

  /**
   * get all available.
   *
   * @return an ObjectNode containing the available as a JSON object
   */

  public List<AvailableDTO> getAllAvaibality() {
    List<AvailableDTO> availableList = new ArrayList<>();
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT a.id_deposit_day , a.deposit_date , a.id_time_slot "
            + "FROM riezrecup.deposit_days a")) {
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          AvailableDTO available = myAvalaibleFactory.getAvailable();
          if (!rs.getDate(2).before(new java.util.Date())) {
            available.setId(rs.getInt(1));
            available.setDay(rs.getDate(2));
            int idTimeSlot = rs.getInt(3);
            boolean isMornig;
            if (idTimeSlot == 1) {
              isMornig = true;
            } else {
              isMornig = false;
            }
            available.setMornig(isMornig);
            availableList.add(available);
          }
        }
      }
      return availableList;
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAllAvaibality()", e);
    }
  }


  /**
   * Returns the available with the specified id.
   *
   * @param availableID The id of the available to return.
   * @return An ObjectNode representing the available.
   */
  public AvailableDTO getAvaibalityById(int availableID) {
    AvailableDTO available = myAvalaibleFactory.getAvailable();
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT a.id_deposit_day , a.deposit_date , a.id_time_slot "
            + "FROM riezrecup.deposit_days a WHERE a.id_deposit_day = ?")) {
      dalServicesPreparedStatement.setInt(1, availableID);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        fromResultSetToAvailableDTO(rs, available);
      }
      return available;
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAvaibalityById()", e);
    }
  }


  /**
   * set the attribute of available with the value of the resultSet.
   *
   * @param rs        the resultSet to translate.
   * @param available the available ton set with the resultSet.
   */
  private void fromResultSetToAvailableDTO(ResultSet rs, AvailableDTO available)
      throws SQLException {
    rs.next();
    available.setId(rs.getInt(1));
    available.setDay(rs.getDate(2));
    int idTimeSlot = rs.getInt(3);
    boolean isMornig;
    if (idTimeSlot == 1) {
      isMornig = true;
    } else if (idTimeSlot == 2) {
      isMornig = false;
    } else {
      throw new SQLException();
    }
    available.setMornig(isMornig);
  }

}
