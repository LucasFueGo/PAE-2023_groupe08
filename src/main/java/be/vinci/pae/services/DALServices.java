package be.vinci.pae.services;

/**
 * DALServices interface.
 */
public interface DALServices {

  /**
   * start a transaction.
   */
  void startTransaction();

  /**
   * commit a transaction.
   */
  void commitTransaction();

  /**
   * rollback a transaction.
   */
  void rollbackTransaction();

}
