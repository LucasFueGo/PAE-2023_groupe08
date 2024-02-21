package be.vinci.pae.domain;

import java.util.List;

/**
 * NotificationUCC interface.
 */
public interface NotificationUCC {

  /**
   * get all notification's objects of an user.
   *
   * @param idUser the ID of the user
   * @return a list of notification
   */
  List<NotificationDTO> getNotificationsByIdUser(int idUser);

  /**
   * get all notification's objects for admin and helper.
   *
   * @return a list of notification
   */
  List<NotificationDTO> getNotificationByRole();

  /**
   * mark a notification as read.
   *
   * @param idNotification the id of the notification
   * @param idUser         the id of the user
   */
  void readNotification(int idNotification, int idUser);

}
