package be.vinci.pae.services;

import be.vinci.pae.domain.NotificationDTO;
import be.vinci.pae.domain.NotificationFactory;
import be.vinci.pae.exception.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationDataServiceImpl class.
 */
public class NotificationDAOImpl implements NotificationDAO {

  @Inject
  private DALBackendServices dalBackendServices;

  @Inject
  private NotificationFactory notificationFactory;


  /**
   * get all notifications of a user.
   *
   * @param id of the user
   * @return list of notifications
   */
  @Override
  public List<NotificationDTO> getNotificationsByIdUser(int id) {
    List<NotificationDTO> notifications = new ArrayList<>();

    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT n.* FROM riezrecup.notifications n WHERE n.id_user = ? AND n.role = 0")) {
      dalServicesPreparedStatement.setInt(1, id);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          NotificationDTO notification = notificationFactory.getNotification();
          fromResultSetToNotificationDTO(rs, notification);
          notifications.add(notification);
        }
        dalServicesPreparedStatement.close();
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getNotificationsByIdUser", e);
    }
    return notifications;
  }

  public List<NotificationDTO> getNotificationByRole() {
    List<NotificationDTO> notifications = new ArrayList<>();

    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT n.* FROM riezrecup.notifications n WHERE n.role = 1")) {
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          NotificationDTO notification = notificationFactory.getNotification();
          fromResultSetToNotificationDTO(rs, notification);
          notifications.add(notification);
        }
        dalServicesPreparedStatement.close();
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getNotificationByRole", e);
    }
    return notifications;
  }

  private void fromResultSetToNotificationDTO(ResultSet rs, NotificationDTO notification)
      throws SQLException {
    notification.setId(rs.getInt(1));
    notification.setIdObject(rs.getInt(2));
    notification.setNotificationDate(rs.getDate(3));
    notification.setRead(rs.getBoolean(4));
    notification.setNotificationMessage(rs.getString(5));
    notification.setIdUser(rs.getInt(6));
    notification.setRole(rs.getInt(7));

  }


  /**
   * mark a notification as read.
   *
   * @param idNotification the id of the notification
   */
  public void readNotification(int idNotification) {
    PreparedStatement dalServicesPreparedStatement;

    dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "UPDATE riezrecup.notifications SET notification_read = true WHERE id_notification = ?");

    try (dalServicesPreparedStatement) {
      dalServicesPreparedStatement.setInt(1, idNotification);
      dalServicesPreparedStatement.executeUpdate();
      dalServicesPreparedStatement.close();
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the method readNotification()", e);
    }
  }

  /**
   * get the id of the user of a notification.
   *
   * @param idNotification the id of the notification
   * @return the id of the user
   * @throws FatalException if there is a problem with the database
   */
  public int getidUserOfNotification(int idNotification) {
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT no.id_user FROM riezrecup.notifications no WHERE no.id_notification = ?")) {
      dalServicesPreparedStatement.setInt(1, idNotification);
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        rs.next();
        int idUser = rs.getInt(1);
        dalServicesPreparedStatement.close();
        return idUser;
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getIdUserOfNotification", e);
    }
  }


}

