package be.vinci.pae.utils;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.exception.FatalException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * WebExceptionMapper class.
 */
@Provider
public class WebExceptionMapper implements ExceptionMapper<Throwable> {


  /**
   * mapper, response a exception.
   *
   * @param exception exception of Reponse
   * @return Response
   */
  private static Response mapper(Throwable exception) {
    exception.printStackTrace();

    if (exception instanceof BizException e) {
      write(exception.getMessage(), "src/logs/Biz.log", e);
      return Response.status(Status.fromStatusCode(412))
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof FatalException f) {
      write(exception.getMessage(), "src/logs/Fatal.log", f);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof WebApplicationException w) {
      write(exception.getMessage(), "src/logs/WebException.log", w);
      return Response.status(((WebApplicationException) exception).getResponse().getStatus())
          .entity(exception.getMessage())
          .build();
    }
    return Response.status(Status.NOT_FOUND)
        .entity(exception.getMessage())
        .build();
  }

  /**
   * The methode to write the message in file log.
   *
   * @param message   the message to write in log
   * @param fileName  the file name of log file
   * @param exception the exception
   */
  public static void write(String message, String fileName, Exception exception) {
    Logger logger = Logger.getLogger(exception.getClass().getName());
    FileHandler fileLog;

    try {
      int limit = 10000000; // 10 Mo en bytes
      int numFiles = 10; // nombre maximal de fichiers de log à conserver
      boolean append = true; // pour ajouter les logs au fichier existant
      fileLog = new FileHandler(fileName, limit, numFiles, append);
      SimpleFormatter formatter = new SimpleFormatter();
      fileLog.setFormatter(formatter);
      logger.addHandler(fileLog);
      logger.warning(new Date() + " | " + message);
      fileLog.close();
    } catch (IOException e) {
      System.out.println("Error with the logger");
      throw new RuntimeException(e);
    }
  }

  /**
   * toResponse, response a exception.
   *
   * @param throwable exception of Reponse
   * @return Response
   */
  @Override
  public Response toResponse(Throwable throwable) {
    return mapper(throwable);
  }
}
