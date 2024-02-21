package be.vinci.pae.domain;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.DALServices;
import be.vinci.pae.services.NotificationDAO;
import jakarta.inject.Inject;
import java.util.List;

/**
 * NotificationUCCImpl class.
 */
public class NotificationUCCImpl implements NotificationUCC {

  @Inject
  private NotificationFactory notificationFactory;
  @Inject
  private DALServices dalServices;

  @Inject
  private NotificationDAO notificationDAO;


  /**
   * get all notification's objects of an user.
   *
   * @param idUser the ID of the user
   * @return a list of notification
   */
  public List<NotificationDTO> getNotificationsByIdUser(int idUser) {
    try {
      dalServices.startTransaction();
      List<NotificationDTO> notifications = notificationDAO.getNotificationsByIdUser(idUser);
      if (notifications == null) {
        throw new BizException("OBJECT (no objects found) : Could not return a list "
            + "of all the objects matching given parameters.");
      }
      dalServices.commitTransaction();
      return notifications;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * get all notification's objects for admin and helper.
   *
   * @return a list of notification
   */
  public List<NotificationDTO> getNotificationByRole() {
    try {
      dalServices.startTransaction();
      List<NotificationDTO> notifications = notificationDAO.getNotificationByRole();
      if (notifications == null) {
        throw new BizException("OBJECT (no objects found) : Could not return a list "
            + "of all the objects matching given parameters.");
      }
      dalServices.commitTransaction();
      return notifications;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }


  /**
   * mark a notification as read.
   *
   * @param idNotification the id of the notification
   * @param idUser         the id of the user
   * @throws BizException if the notification is not for the user
   */
  public void readNotification(int idNotification, int idUser) {
    try {
      dalServices.startTransaction();
      if (idUser != notificationDAO.getidUserOfNotification(idNotification)) {
        throw new BizException("This notification is not for you. (Bad id)");
      }
      notificationDAO.readNotification(idNotification);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }


}
