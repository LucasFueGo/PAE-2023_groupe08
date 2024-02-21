package be.vinci.pae.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.NotificationDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

@TestInstance(Lifecycle.PER_CLASS)
class NotificationUCCImplTest extends TestBinder {

  private NotificationFactory notificationFactory;
  private NotificationUCC notificationUCC;

  private NotificationDAO notificationDAO;

  private Notification notification1;


  @BeforeAll
  void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.notificationFactory = locator.getService(NotificationFactory.class);
    this.notificationUCC = locator.getService(NotificationUCC.class);
    this.notificationDAO = locator.getService(NotificationDAO.class);
  }

  /**
   * Initialize the tests.
   */
  @BeforeEach
  void init() {
    Mockito.reset(notificationDAO);
    Notification notification1 = (Notification) notificationFactory.getNotification();
    notification1.setId(1);
    notification1.setIdUser(1);
    notification1.setIdObject(1);
    notification1.setRead(false);
    notification1.setNotificationMessage("message pour l'objet 1");
    notification1.setNotificationDate(new Date());
  }

  // Tests for getNotificationsByIdUser

  /**
   * Test getNotificationsByIdUser successfull.
   */
  @Test
  @DisplayName("Test getNotificationsByIdUser successfull")
  void testGetNotificationsByIdUser() {
    List<NotificationDTO> notifications = new ArrayList<NotificationDTO>();
    notifications.add(notification1);
    when(notificationDAO.getNotificationsByIdUser(1)).thenReturn(notifications);
    notificationUCC.getNotificationsByIdUser(1);
  }

  /**
   * Test getNotificationsByIdUser fail because of any notification.
   */
  @Test
  @DisplayName("Test getNotificationsByIdUser fail because of any notification")
  void testGetNotificationsByIdUserFail() {
    when(notificationDAO.getNotificationsByIdUser(1)).thenReturn(null);
    assertThrows(BizException.class, () -> notificationUCC.getNotificationsByIdUser(1));
  }

  // Tests for readNotification

  /**
   * Test readNotification successfull.
   */
  @Test
  @DisplayName("Test readNotification successfull")
  void testReadNotification() {

    Notification notification2 = (Notification) notificationFactory.getNotification();
    notification2.setId(2);
    notification2.setIdUser(1);
    notification2.setIdObject(1);
    notification2.setRead(false);
    notification2.setNotificationMessage("message pour l'objet 2");
    notification2.setNotificationDate(new Date());
    when(notificationDAO.getidUserOfNotification(notification2.getId())).thenReturn(1);
    notificationUCC.readNotification(notification2.getId(), 1);

    // Vérifier que les méthodes appropriées ont été appelées et que la transaction a été commitée
    verify(notificationDAO).getidUserOfNotification(notification2.getId());
    verify(notificationDAO).readNotification(notification2.getId());

  }

  /**
   * Test readNotification fail because of bad id.
   */
  @Test
  @DisplayName("Test readNotification fail because of bad id")
  void testReadNotificationFail() {
    when(notificationDAO.getidUserOfNotification(1)).thenReturn(1);
    assertThrows(BizException.class, () -> notificationUCC.readNotification(1, 2));
  }

}