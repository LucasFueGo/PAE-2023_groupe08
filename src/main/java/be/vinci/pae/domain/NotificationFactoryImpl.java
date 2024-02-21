package be.vinci.pae.domain;


/**
 * NotificationFactory interface.
 */
public class NotificationFactoryImpl implements NotificationFactory {

  @Override
  public NotificationDTO getNotification() {

    return new NotificationImpl();
  }
}
