package be.vinci.pae.domain;

/**
 * NotificationFactory interface.
 */
public interface NotificationFactory {

  /**
   * Returns a new NotificationImpl object.
   *
   * @return a new NotificationImpl object
   */
  NotificationDTO getNotification();

}
