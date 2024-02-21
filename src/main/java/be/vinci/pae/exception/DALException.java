package be.vinci.pae.exception;

/**
 * DALException class.
 */
public class DALException extends RuntimeException {

  /**
   * Constructor of DALException.
   */
  public DALException() {
    super();
  }

  /**
   * Constructor of DALException.
   *
   * @param message the message to write in log
   */
  public DALException(String message) {
    super(message);
  }

  /**
   * Constructor of DALException.
   *
   * @param message the message to write in log
   * @param exc     the exception to pass
   */
  public DALException(String message, Exception exc) {
    super(message, exc);
  }

}
