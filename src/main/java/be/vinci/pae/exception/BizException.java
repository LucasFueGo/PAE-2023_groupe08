package be.vinci.pae.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

/**
 * BizException class.
 */
public class BizException extends WebApplicationException {

  /**
   * Constructor of BizException.
   *
   * @param errorCode the error message to generate the exception
   */
  public BizException(Status errorCode) {
    super(errorCode);
  }

  /**
   * Constructor of BizException.
   *
   * @param message the message to write in log
   */
  public BizException(String message) {
    super(message);
  }


}