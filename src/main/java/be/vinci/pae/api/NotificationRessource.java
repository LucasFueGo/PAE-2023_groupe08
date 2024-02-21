package be.vinci.pae.api;

import be.vinci.pae.domain.NotificationDTO;
import be.vinci.pae.domain.NotificationUCC;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;


/**
 * NotificationRessource class.
 */
@Singleton
@Path("notification")
public class NotificationRessource {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private NotificationUCC notificationUCC;


  /**
   * get all notification's objects of an user.
   *
   * @param token ID of the user
   * @return a list of notification
   * @throws JWTVerificationException if the token is not valid
   */

  @GET
  @Path("/getAllNotification")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<NotificationDTO> getAllNotification(@QueryParam("token") String token) {
    if (token.equals("")) {
      throw new WebApplicationException("A token is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).build();
    DecodedJWT jwt = jwtVerifier.verify(token);
    Date expirationDate = jwt.getExpiresAt();
    int idUser = jwt.getClaim("id").asInt();
    return expirationDate.before(new Date()) ? null
        : notificationUCC.getNotificationsByIdUser(idUser);

  }


  /**
   * mark a notification as read.
   *
   * @param idNotification the ID of the notification
   * @param token          the token of the user
   * @throws JWTVerificationException if the token is not valid
   * @throws WebApplicationException  if idNotification is 0
   * @throws WebApplicationException  if the token is not valid
   */
  @POST
  @Path("/readNotification/{idNotification}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public void readNotification(@PathParam("idNotification") int idNotification,
      @QueryParam("token") String token) {
    if (idNotification == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    if (token.equals("")) {
      throw new WebApplicationException("A token is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).build();
    DecodedJWT jwt = jwtVerifier.verify(token);
    Date expirationDate = jwt.getExpiresAt();
    int idUser = jwt.getClaim("id").asInt();
    if (expirationDate.before(new Date())) {
      throw new JWTVerificationException("Your session is expired.");
    }
    notificationUCC.readNotification(idNotification, idUser);
  }


}
