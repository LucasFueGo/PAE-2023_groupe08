package be.vinci.pae.domain;

import java.util.Date;
import java.util.Objects;

/**
 * ObjectImpl class.
 */

class ObjectImpl implements Object {


  private int idObject;
  private int idUser;
  private int idType;
  private String nameType;

  private int idPassage;
  private Date datePassage;
  private int idTimeSlot;
  private double arrivalTime;
  private double departureTime;
  private String description;
  private String state;
  private String stateComplete;
  private double sellingPrice;
  private Boolean visible;
  private String offeringMemberPhone;
  private Date acceptedDate;

  private Date createPropositionDate;
  private Date withdrawalDate;
  private Date storeDropOffDate;
  private Date dateItemSold;
  private String url;
  private String denialNotification;

  private int version;


  /**
   * change the state of the object in 'DW'.
   *
   * @return true if the state of the object have been change, false otherwise
   */
  @Override
  public Boolean indicateDepositingObjectAtTheWorkshop() {
    if (this.state.equals("DW")) {
      return false;
    }
    this.state = "DW";
    return true;
  }


  /**
   * change the state of the object in 'AS'.
   *
   * @return true if the state of the object have been change, false otherwise
   */
  @Override
  public Boolean indicateDepositingObjectAtTheStore() {
    if (this.state.equals("AS")) {
      return false;
    }
    this.state = "AS";
    return true;
  }

  /**
   * Returns the ID of the object.
   *
   * @return the ID of the object
   */
  @Override
  public int getId() {
    return idObject;
  }

  /**
   * Setter method for the ID of the Object object.
   *
   * @param id the integer value to set as the ID
   */
  @Override
  public void setId(int id) {
    this.idObject = id;
  }

  /**
   * Returns the ID of the owner of the object.
   *
   * @return the ID of the owner of the object
   */
  @Override
  public int getIdUser() {
    return idUser;
  }

  /**
   * Setter method for the ID of the owner of the Object object.
   *
   * @param idUser the integer value to set as the ID of the owner
   */
  @Override
  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  /**
   * Returns the type ID of the object.
   *
   * @return the type ID of the object
   */
  @Override
  public int getIdType() {
    return idType;
  }

  /**
   * Setter method for the type ID of the Object object.
   *
   * @param idType the integer value to set as the type ID
   */
  @Override
  public void setIdType(int idType) {
    this.idType = idType;
  }

  /**
   * Returns the name of the type.
   *
   * @return the name of the type.
   */
  public String getNameType() {
    return nameType;
  }

  /**
   * Setter method for the name of the type of the Object object.
   *
   * @param nameType the String value to set as the name of the type
   */
  @Override
  public void setNameType(String nameType) {
    this.nameType = nameType;
  }

  /**
   * Returns the description of the object.
   *
   * @return the description of the object
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Setter method for the description of the Object object.
   *
   * @param description the String value to set as the description
   */
  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the sellingPrice of the object.
   *
   * @return the sellingPrice of the object
   */
  @Override
  public double getSellingPrice() {
    return this.sellingPrice;
  }

  /**
   * Setter method for the sellingPrice of the Object object.
   *
   * @param sellingPrice the double value to set as the sellingPrice
   */
  @Override
  public void setSellingPrice(double sellingPrice) {
    this.sellingPrice = sellingPrice;
  }

  /**
   * Returns the visibility of the object.
   *
   * @return the visibility of the object
   */
  @Override
  public boolean getVisibility() {
    return this.visible;
  }

  /**
   * Setter method for the visibility of the Object object.
   *
   * @param visibility the boolean value to set as the visibility
   */
  @Override
  public void setVisibility(boolean visibility) {
    this.visible = visibility;
  }

  /**
   * Returns the state of the object.
   *
   * @return the state of the object
   */
  @Override
  public String getState() {
    return this.state;
  }

  /**
   * Setter method for the state of the Object object.
   *
   * @param state the String value to set as the state
   */
  @Override
  public void setState(String state) {
    this.state = state;
  }

  /**
   * Returns the stateComplete of the object.
   *
   * @return the stateComplete of the object
   */
  @Override
  public String getStateComplete() {
    return this.stateComplete;
  }

  /**
   * Setter method for the stateCpomplete of the Object object.
   *
   * @param stateComplete the String value to set as the stateComplete
   */
  @Override
  public void setStateComplete(String stateComplete) {
    this.stateComplete = stateComplete;
  }


  /**
   * Returns the OfferingMemberPhone of the object.
   *
   * @return the OfferingMemberPhone of the object
   */
  @Override
  public String getOfferingMemberPhone() {
    return offeringMemberPhone;
  }

  /**
   * Setter method for the OfferingMemberPhone of the Object object.
   *
   * @param offeringMemberPhone the String value to set as the OfferingMemberPhone
   */
  @Override
  public void setOfferingMemberPhone(String offeringMemberPhone) {
    this.offeringMemberPhone = offeringMemberPhone;
  }

  /**
   * Returns the ProposalDate of the object.
   *
   * @return the ProposalDate of the object
   */
  @Override
  public Date getAcceptedDate() {
    return this.acceptedDate;
  }

  /**
   * Setter method for the proposalDate of the Object object.
   *
   * @param acceptedDate the date value to set as the ProposalDate
   */
  @Override
  public void setAcceptedDate(Date acceptedDate) {
    this.acceptedDate = acceptedDate;
  }

  /**
   * Returns the createPropositionDate of the object.
   *
   * @return the ProposalDate of the object
   */
  @Override
  public Date getCreatePropositionDate() {
    return this.createPropositionDate;
  }

  /**
   * Sets the createPropositionDate of the object.
   *
   * @return the ProposalDate of the object
   */
  @Override
  public void setCreatePropositionDate(Date createPropositionDate) {
    this.createPropositionDate = createPropositionDate;
  }

  /**
   * Returns the WithdrawalDate of the object.
   *
   * @return the WithdrawalDate of the object
   */
  @Override
  public Date getWithdrawalDate() {
    return this.withdrawalDate;
  }

  /**
   * Setter method for the WithdrawalDate of the Object object.
   *
   * @param withdrawalDate the date value to set as the WithdrawalDate
   */
  @Override
  public void setWithdrawalDate(Date withdrawalDate) {
    this.withdrawalDate = withdrawalDate;
  }

  /**
   * Returns the storeDropOffDate of the object.
   *
   * @return the storeDropOffDate of the object
   */
  @Override
  public Date getStoreDropOffDate() {
    return this.storeDropOffDate;
  }

  /**
   * Setter method for the storeDropOffDate of the Object object.
   *
   * @param storeDropOffDate the date value to set as the storeDropOffDate
   */
  @Override
  public void setStoreDropOffDate(Date storeDropOffDate) {
    this.storeDropOffDate = storeDropOffDate;
  }

  /**
   * Returns the dateItemSold of the object.
   *
   * @return the dateItemSold of the object
   */
  @Override
  public Date getDateItemSold() {
    return this.dateItemSold;
  }

  /**
   * Setter method for the dateItemSold of the Object object.
   *
   * @param dateItemSold the date value to set as the dateItemSold
   */
  @Override
  public void setDateItemSold(Date dateItemSold) {
    this.dateItemSold = dateItemSold;
  }

  /**
   * Returns the notificationRefusal of the object.
   *
   * @return the notificationRefusal of the object
   */
  @Override
  public String getDenialNotification() {
    return this.denialNotification;
  }

  /**
   * Setter method for the notificationRefusal of the Object object.
   *
   * @param notificationRefusal the String value to set as the notificationRefusal
   */
  @Override
  public void setDenialNotification(String notificationRefusal) {
    this.denialNotification = notificationRefusal;
  }

  /**
   * Returns the passage ID of the object.
   *
   * @return the passage ID of the owner of the object
   */
  @Override
  public int getIdPassage() {
    return this.idPassage;
  }

  /**
   * Setter method for the passage ID of the Object object.
   *
   * @param idPassage the integer value to set as the passage ID
   */
  @Override
  public void setIdPassage(int idPassage) {
    this.idPassage = idPassage;
  }

  /**
   * Returns the passage Date of the object.
   *
   * @return the date passage of the owner of the object
   */
  @Override
  public Date getDatePassage() {
    return this.datePassage;
  }

  /**
   * Setter method for the date passage Date of the Object object.
   *
   * @param datePassage the Date value to set as the date passage
   */
  @Override
  public void setDatePassage(Date datePassage) {
    this.datePassage = datePassage;
  }


  /**
   * Returns the time slot ID of the object.
   *
   * @return the time slot ID of the owner of the object
   */
  public int getIdTimeSlot() {
    return idTimeSlot;
  }

  /**
   * Setter method for the time slot ID of the Object object.
   *
   * @param idTimeSlot the integer value to set as the time slot ID
   */
  public void setIdTimeSlot(int idTimeSlot) {
    this.idTimeSlot = idTimeSlot;
  }

  /**
   * Returns the double of the departureTime of the object.
   *
   * @return the double of the departureTime of the object
   */
  public double getDepartureTime() {
    return this.departureTime;
  }

  /**
   * Setter method for the double of the departureTime of the Object object.
   *
   * @param departureTime the double value to set as the url of the image
   */
  public void setDepartureTime(double departureTime) {
    this.departureTime = departureTime;
  }


  /**
   * Returns the double of the arrivalTime of the object.
   *
   * @return the double of the arrivalTime of the object
   */
  public double getArrivalTime() {
    return this.arrivalTime;
  }

  /**
   * Setter method for the double of the arrivalTime of the Object object.
   *
   * @param arrivalTime the double value to set as the arrivalTime of the image
   */
  public void setArrivalTime(double arrivalTime) {
    this.arrivalTime = arrivalTime;
  }


  /**
   * Returns the url of the image of the object.
   *
   * @return the url of the image of the object
   */
  public String getUrl() {
    return url;
  }

  /**
   * Setter method for the url of the image of the Object object.
   *
   * @param url the String value to set as the url of the image
   */
  public void setUrl(String url) {
    this.url = url;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectImpl object = (ObjectImpl) o;
    return idObject == object.idObject;
  }

  @Override
  public int hashCode() {
    return Objects.hash(idObject);
  }
}
