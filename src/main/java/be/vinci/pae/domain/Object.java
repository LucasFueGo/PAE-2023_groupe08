package be.vinci.pae.domain;

import java.util.Date;

/**
 * Object Interface.
 */
interface Object extends ObjectDTO {

  /**
   * change the state of the object in 'DW'.
   *
   * @return true if the state of the object have been change, false otherwise
   */
  Boolean indicateDepositingObjectAtTheWorkshop();


  /**
   * change the state of the object in 'AS'.
   *
   * @return true if the state of the object have been change, false otherwise
   */
  Boolean indicateDepositingObjectAtTheStore();

  Date getDatePassage();

  void setDatePassage(Date datePassage);

  boolean equals(Object o);
}
