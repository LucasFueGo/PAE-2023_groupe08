package be.vinci.pae.domain;

import java.util.Date;

/**
 * NotificationDTO class.
 */
public interface NotificationDTO {

  /**
   * Return the ID of the notification.
   *
   * @return the ID of the notification
   */
  int getId();


  /**
   * Setter method for the ID of the Notification object.
   *
   * @param id the integer value to set as the ID
   */
  void setId(int id);


  /**
   * return the id of the object concerned by the notification.
   *
   * @return the id of the object concerned by the notification.
   */
  int getIdObject();


  /**
   * set the id of the object concerned by the notification.
   *
   * @param idObject the id of the object concerned by the notification.
   */
  void setIdObject(int idObject);


  /**
   * return the date of the creation of the notification.
   *
   * @return the date of the creation of the notification.
   */
  Date getNotificationDate();


  /**
   * set the date of the creation of the notification.
   *
   * @param notificationDate the date of the creation of the notification.
   */
  void setNotificationDate(Date notificationDate);


  /**
   * return the message of the notification.
   *
   * @return the message of the notification.
   */
  String getNotificationMessage();


  /**
   * set the message of the notification.
   *
   * @param notificationMessage the message of the notification.
   */
  void setNotificationMessage(String notificationMessage);


  /**
   * return a bolean to know if the notification is read or not.
   *
   * @return true if the notification is read, false otherwise.
   */
  boolean isRead();

  /**
   * set the notification as read.
   *
   * @param read true if the notification is read, false otherwise.
   */
  void setRead(boolean read);


  /**
   * return the id of the user who receive the notification.
   *
   * @return the id of the user who receive the notification.
   */
  int getIdUser();

  /**
   * set the id of the user who receive the notification.
   *
   * @param idUser the id of the user who receive the notification.
   */
  void setIdUser(int idUser);

  /**
   * return the role.
   *
   * @return the role of the notification.
   */
  int getRole();

  /**
   * set the role.
   *
   * @param role the role of the notification.
   */
  void setRole(int role);

}
