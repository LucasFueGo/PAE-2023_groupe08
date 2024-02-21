package be.vinci.pae.services;

import be.vinci.pae.domain.NotificationDTO;
import be.vinci.pae.exception.FatalException;
import java.util.List;

/**
 * notificationDataService class.
 */
public interface NotificationDAO {

  /**
   * get all notifications of a user.
   *
   * @param id of the user
   * @return list of notifications
   */
  List<NotificationDTO> getNotificationsByIdUser(int id);

  List<NotificationDTO> getNotificationByRole();

  /**
   * mark a notification as read.
   *
   * @param idNotification the id of the notification
   */
  void readNotification(int idNotification);


  /**
   * get the id of the user of a notification.
   *
   * @param idNotification the id of the notification
   * @return the id of the user
   * @throws FatalException if there is a problem with the database
   */
  int getidUserOfNotification(int idNotification);
}
