package be.vinci.pae.api.filters;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * TokenDecodingException class.
 */
public class TokenDecodingException extends WebApplicationException {

  /**
   * Exception indicating that there was an issue decoding the user's token. Returns a 401
   * Unauthorized response.
   */
  public TokenDecodingException() {
    super(Response.status(Response.Status.UNAUTHORIZED)
        .build());
  }

  /**
   * Creates a new TokenDecodingException with a custom error message.
   *
   * @param message the custom error message
   */
  public TokenDecodingException(String message) {
    super(Response.status(Response.Status.UNAUTHORIZED)
        .entity(message)
        .type("text/plain")
        .build());
  }

  /**
   * Creates a new TokenDecodingException with an error message derived from the cause of the
   * exception.
   *
   * @param cause the cause of the exception
   */
  public TokenDecodingException(Throwable cause) {
    super(Response.status(Response.Status.UNAUTHORIZED)
        .entity(cause.getMessage())
        .type("text/plain")
        .build());
  }
}
