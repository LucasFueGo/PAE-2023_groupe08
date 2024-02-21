package be.vinci.pae.domain;

import java.util.Date;

class NotificationImpl implements Notification {

  private int id;
  private int idObject;
  private Date notificationDate;
  private String notificationMessage;
  private boolean read;
  private int idUser;
  private int role;

  @Override
  public Boolean indicateNotificationAsRead() {
    return null;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public int getIdObject() {
    return idObject;
  }

  @Override
  public void setIdObject(int idObject) {
    this.idObject = idObject;
  }

  @Override
  public Date getNotificationDate() {
    return notificationDate;
  }

  @Override
  public void setNotificationDate(Date notificationDate) {
    this.notificationDate = notificationDate;
  }

  @Override
  public String getNotificationMessage() {
    return notificationMessage;
  }

  @Override
  public void setNotificationMessage(String notificationMessage) {
    this.notificationMessage = notificationMessage;
  }

  @Override
  public boolean isRead() {
    return read;
  }

  @Override
  public void setRead(boolean read) {
    this.read = read;
  }

  @Override
  public int getIdUser() {
    return idUser;
  }

  @Override
  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  public int getRole() {
    return role;
  }

  public void setRole(int role) {
    this.role = role;
  }
}
