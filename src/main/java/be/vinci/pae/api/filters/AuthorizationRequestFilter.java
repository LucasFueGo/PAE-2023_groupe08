package be.vinci.pae.api.filters;

import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

/**
 * AuthorizationRequestFilter class.
 */
@Singleton
@Provider
@Authorize
public class AuthorizationRequestFilter implements ContainerRequestFilter {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).withIssuer("auth0")
      .build();
  @Context
  private ResourceInfo resourceInfo;
  @Inject
  private UserUCC userUCC;

  /**
   * Filters the incoming request context to ensure the user is authorized to access the requested
   * resource.
   *
   * @param requestContext the container request context containing the HTTP request information
   */

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String roleToExecute = resourceInfo.getResourceMethod()
        .getAnnotation(Authorize.class)
        .value();
    String token = requestContext.getHeaderString("Authorization");
    if (token == null) {
      requestContext.abortWith(
          Response.status(Status.UNAUTHORIZED).entity("A token is needed to access this resource")
              .build());
    } else {
      DecodedJWT decodedToken;
      try {
        decodedToken = this.jwtVerifier.verify(token);
      } catch (Exception e) {
        throw new TokenDecodingException(e);
      }
      UserDTO authenticatedUser = userUCC.getUserById(decodedToken.getClaim("id").asInt());
      if (authenticatedUser == null
          || roleToExecute.equals("HELPER") && authenticatedUser.getRole() < 1
          || roleToExecute.equals("ADMIN") && authenticatedUser.getRole() < 2) {
        requestContext.abortWith(
            Response.status(Status.FORBIDDEN)
                .entity("You are forbidden from accessing this resource")
                .build());
      }

      requestContext.setProperty("user", authenticatedUser);
    }
  }

}