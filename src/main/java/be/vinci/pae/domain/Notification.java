package be.vinci.pae.domain;

/**
 * Notification Interface.
 */
public interface Notification extends NotificationDTO {

  /**
   * change the state of the notification in 'V'.
   *
   * @return true if the state of the notification have been change, false otherwise
   */
  Boolean indicateNotificationAsRead();


}
