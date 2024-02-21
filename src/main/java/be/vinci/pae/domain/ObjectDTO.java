package be.vinci.pae.domain;

import java.util.Date;

/**
 * ObjectDTO class.
 */
public interface ObjectDTO {

  /**
   * Returns the ID of the object.
   *
   * @return the ID of the object
   */
  int getId();

  /**
   * Setter method for the ID of the Object object.
   *
   * @param id the integer value to set as the ID
   */
  void setId(int id);

  /**
   * Returns the ID of the owner of the object.
   *
   * @return the ID of the owner of the object
   */
  int getIdUser();

  /**
   * Setter method for the ID of the owner of the Object object.
   *
   * @param idUser the integer value to set as the ID of the owner
   */
  void setIdUser(int idUser);

  /**
   * Returns the type ID of the object.
   *
   * @return the type ID of the object
   */
  int getIdType();

  /**
   * Setter method for the type ID of the Object object.
   *
   * @param idType the integer value to set as the type ID
   */
  void setIdType(int idType);

  /**
   * Returns the name of the type.
   *
   * @return the name of the type.
   */
  String getNameType();

  /**
   * Setter method for the name of the type of the Object object.
   *
   * @param name the String value to set as the name of the type
   */
  void setNameType(String name);


  /**
   * Returns the description of the object.
   *
   * @return the description of the object
   */
  String getDescription();

  /**
   * Setter method for the description of the Object object.
   *
   * @param description the String value to set as the description
   */
  void setDescription(String description);


  /**
   * Returns the sellingPrice of the object.
   *
   * @return the sellingPrice of the object
   */
  double getSellingPrice();

  /**
   * Setter method for the sellingPrice of the Object object.
   *
   * @param sellingPrice the double value to set as the sellingPrice
   */
  void setSellingPrice(double sellingPrice);

  /**
   * Returns the visibility of the object.
   *
   * @return the visibility of the object
   */
  boolean getVisibility();

  /**
   * Setter method for the visibility of the Object object.
   *
   * @param visibility the boolean value to set as the visibility
   */
  void setVisibility(boolean visibility);

  /**
   * Returns the state of the object.
   *
   * @return the state of the object
   */
  String getState();

  /**
   * Setter method for the state of the Object object.
   *
   * @param state the String value to set as the state
   */
  void setState(String state);

  /**
   * Returns the stateComplete of the object.
   *
   * @return the stateComplete of the object
   */
  String getStateComplete();

  /**
   * Setter method for the stateComplete of the Object object.
   *
   * @param stateComplete the String value to set as the stateComplete
   */
  void setStateComplete(String stateComplete);

  /**
   * Returns the OfferingMemberPhone of the object.
   *
   * @return the OfferingMemberPhone of the object
   */
  String getOfferingMemberPhone();

  /**
   * Setter method for the OfferingMemberPhone of the Object object.
   *
   * @param offeringMemberPhone the String value to set as the OfferingMemberPhone
   */
  void setOfferingMemberPhone(String offeringMemberPhone);

  /**
   * Returns the ProposalDate of the object.
   *
   * @return the ProposalDate of the object
   */
  Date getAcceptedDate();

  /**
   * Setter method for the ProposalDate of the Object object.
   *
   * @param dateOfAccept the date value to set as the ProposalDate
   */
  void setAcceptedDate(Date dateOfAccept);

  /**
   * Returns the WithdrawalDate of the object.
   *
   * @return the WithdrawalDate of the object
   */
  Date getWithdrawalDate();

  /**
   * Setter method for the WithdrawalDate of the Object object.
   *
   * @param withdrawalDate the date value to set as the WithdrawalDate
   */
  void setWithdrawalDate(Date withdrawalDate);

  /**
   * Returns the storeDropOffDate of the object.
   *
   * @return the storeDropOffDate of the object
   */
  Date getStoreDropOffDate();

  /**
   * Setter method for the storeDropOffDate of the Object object.
   *
   * @param storeDropOffDate the date value to set as the storeDropOffDate
   */
  void setStoreDropOffDate(Date storeDropOffDate);

  /**
   * Returns the dateItemSold of the object.
   *
   * @return the dateItemSold of the object
   */
  Date getDateItemSold();

  /**
   * Setter method for the dateItemSold of the Object object.
   *
   * @param dateItemSold the date value to set as the dateItemSold
   */
  void setDateItemSold(Date dateItemSold);

  /**
   * Returns the notificationRefusal of the object.
   *
   * @return the notificationRefusal of the object
   */
  String getDenialNotification();

  /**
   * Setter method for the notificationRefusal of the Object object.
   *
   * @param denialNotification the notification message for the reason why the item has been
   *                           refused
   */
  void setDenialNotification(String denialNotification);

  /**
   * Returns the passage ID of the object.
   *
   * @return the passage ID of the owner of the object
   */
  int getIdPassage();

  /**
   * Setter method for the passage ID of the Object object.
   *
   * @param idPassage the integer value to set as the passage ID
   */
  void setIdPassage(int idPassage);

  /**
   * Returns the time slot ID of the object.
   *
   * @return the time slot ID of the owner of the object
   */
  int getIdTimeSlot();

  /**
   * Setter method for the time slot ID of the Object object.
   *
   * @param idTimeSlot the integer value to set as the time slot ID
   */
  void setIdTimeSlot(int idTimeSlot);

  /**
   * Returns the passage Date of the object.
   *
   * @return the date passage of the owner of the object
   */
  Date getDatePassage();

  /**
   * Setter method for the date passage Date of the Object object.
   *
   * @param datePassage the Date value to set as the date passage
   */
  void setDatePassage(Date datePassage);

  /**
   * Returns the double of the departureTime of the object.
   *
   * @return the double of the departureTime of the object
   */
  double getDepartureTime();

  /**
   * Setter method for the double of the departureTime of the Object object.
   *
   * @param departureTime the double value to set as the url of the image
   */
  void setDepartureTime(double departureTime);

  /**
   * Getter method for the date of proposition for a given object.
   *
   * @return the date value set for the object
   */
  Date getCreatePropositionDate();

  /**
   * Setter method for the date of proposition for a given object.
   *
   * @param createPropositionDate the date value to set for the object
   */
  void setCreatePropositionDate(Date createPropositionDate);

  /**
   * Returns the double of the arrivalTime of the object.
   *
   * @return the double of the arrivalTime of the object
   */
  double getArrivalTime();

  /**
   * Setter method for the double of the arrivalTime of the Object object.
   *
   * @param arrivalTime the double value to set as the arrivalTime of the image
   */
  void setArrivalTime(double arrivalTime);

  /**
   * Returns the url of the image of the object.
   *
   * @return the url of the image of the object
   */
  String getUrl();

  /**
   * Setter method for the url of the image of the Object object.
   *
   * @param url the String value to set as the url of the image
   */
  void setUrl(String url);

  /**
   * Returns the version of the object.
   *
   * @return the version of the object
   */
  int getVersion();

  /**
   * Setter method for the version of the Object object.
   *
   * @param version the integer value to set as the version
   */
  void setVersion(int version);
}
