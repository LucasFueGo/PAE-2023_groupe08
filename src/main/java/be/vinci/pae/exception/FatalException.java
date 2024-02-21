package be.vinci.pae.exception;

/**
 * FatalException class.
 */
public class FatalException extends RuntimeException {

  /**
   * Constructor of FatalException.
   */
  public FatalException() {
    super();
  }

  /**
   * Constructor of FatalException.
   *
   * @param message the message to write in log
   */
  public FatalException(String message) {
    super(message);
  }

  /**
   * Constructor of FatalException.
   *
   * @param message the message to write in log
   * @param exc     the exception to pass
   */
  public FatalException(String message, Exception exc) {
    super(message, exc);
  }

}
