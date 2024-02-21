package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * UserRessource class.
 */
@Singleton
@Path("users")
public class UserResource {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private UserFactory userFactory;
  @Inject
  private UserUCC userUCC;

  /**
   * Authenticates a user with the given credentials and returns user information if successful.
   *
   * @param json the JSON object containing the user's login and password
   * @return the user information as a JSON object if authentication is successful
   * @throws WebApplicationException with 400 Bad Request status if either the login or password is
   *                                 missing from the JSON object, or with 401 Unauthorized status
   *                                 if the credentials are incorrect
   */
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(JsonNode json) {

    // Checks if the received JSON contains both the email and the password
    if (!json.hasNonNull("email") || !json.hasNonNull("password")) {
      throw new WebApplicationException("Email or password required.", Response.Status.BAD_REQUEST);
    }

    String email = json.get("email").asText();
    // Checks if email has the correct format
    if (!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email)) {
      throw new WebApplicationException("The email address has the wrong format",
          Response.Status.BAD_REQUEST);
    }
    String password = json.get("password").asText();

    UserDTO userToLogin = userUCC.login(email, password);
    if (userToLogin == null) {
      throw new WebApplicationException("Incorrect email or password.",
          Response.Status.UNAUTHORIZED);
    }
    return jsonMapper.createObjectNode().put("token", createToken(userToLogin))
        .putPOJO("user", userToLogin);
  }

  /**
   * Updates the user profile and returns the updated user.
   *
   * @param file            the image of user.
   * @param fileDisposition the image of user.
   * @param email           the email of user.
   * @param password        the password of user.
   * @param firstname       the firstname of user.
   * @param lastname        the lastname of user.
   * @param phone           the lastname of phone.
   * @return the user information as a JSON object if authentication is successful
   * @throws WebApplicationException with 400 Bad Request status if either the login or password is
   *                                 missing from the JSON object, or with 401 Unauthorized status
   *                                 if the credentials are incorrect
   */
  @POST
  @Path("/update")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("USER")
  public ObjectNode update(@FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @FormDataParam("email") String email, @FormDataParam("password") String password,
      @FormDataParam("firstname") String firstname, @FormDataParam("lastname") String lastname,
      @FormDataParam("phone") String phone) {

    this.checkInfoUser(email, firstname, lastname, phone);

    UserDTO userToTryRegister = userFactory.getUser();
    userToTryRegister.setPassword(password);
    userToTryRegister.setFirstName(firstname);
    userToTryRegister.setLastName(lastname);
    userToTryRegister.setPhone(phone);
    userToTryRegister.setEmail(email);
    userToTryRegister.setImage(fileDisposition.getFileName());

    UserDTO userToLogin = userUCC.update(userToTryRegister, file);

    return jsonMapper.createObjectNode().put("token", createToken(userToLogin))
        .putPOJO("user", userToLogin);
  }

  /**
   * Creates a JWT token for the given user.
   *
   * @param user the user for which to create the token
   * @return the token as a string
   */
  public String createToken(UserDTO user) {
    try {
      Date expirationTime = new Date(System.currentTimeMillis() + (60 * 60 * 10000));
      return JWT.create().withIssuer("auth0").withExpiresAt(expirationTime)
          .withClaim("id", user.getId()).sign(this.jwtAlgorithm);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Checks if the user's token is still valid on page refresh.
   *
   * @param token the JSON object containing the user's token
   * @return the user information as a JSON object if token is valid
   * @throws WebApplicationException with 400 Bad Request status if the token is missing from the
   *                                 JSON object, or with 401 Unauthorized status if the credentials
   *                                 are incorrect
   */
  @GET
  @Path("/refresh")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDTO refresh(@QueryParam("token") String token) {
    try {
      if (token.equals("")) {
        throw new WebApplicationException("A Token is required for that request.",
            Response.Status.BAD_REQUEST);
      }
      JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).build();
      DecodedJWT jwt = jwtVerifier.verify(token);
      Date expirationDate = jwt.getExpiresAt();
      return expirationDate.before(new Date()) ? null
          : userUCC.getUserById(jwt.getClaim("id").asInt());
    } catch (JWTVerificationException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Checks if the user associated with the transmitted JWT token has admin role.
   *
   * @param token The JSON node containing the token.
   * @return true if the user has admin role, false otherwise.
   * @throws WebApplicationException if the token is missing or expired.
   */
  @GET
  @Path("/isAdmin")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public int isAdmin(@QueryParam("token") String token) {
    try {
      if (token.equals("")) {
        throw new WebApplicationException("A Token is required for that request.",
            Response.Status.BAD_REQUEST);
      }
      JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).build();
      DecodedJWT jwt = jwtVerifier.verify(token);
      Date expirationDate = jwt.getExpiresAt();
      if (expirationDate.before(new Date())) {
        throw new WebApplicationException("Invalid Token.", Response.Status.UNAUTHORIZED);
      }
      UserDTO user = userUCC.getUserById(jwt.getClaim("id").asInt());
      if (user.getRole() == 2) {
        return 2;
      } else if (user.getRole() == 1) {
        return 1;
      } else {
        return 0;
      }
    } catch (JWTVerificationException | WebApplicationException e) {
      e.printStackTrace();
      return 0;
    }
  }


  /**
   * register a user with the given credentials and returns user information if successful.
   *
   * @param file            the image of user.
   * @param fileDisposition the image of user.
   * @param email           the email of user.
   * @param password        the password of user.
   * @param firstname       the firstname of user.
   * @param lastname        the lastname of user.
   * @param phone           the lastname of phone.
   * @return the user information as a JSON object if authentication is successful
   * @throws WebApplicationException with 400 Bad Request status if one field is missing from the
   *                                 JSON object, or with 401 Unauthorized status if the credentials
   *                                 are incorrect
   */
  @POST
  @Path("/register")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDTO register(@FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @FormDataParam("email") String email, @FormDataParam("password") String password,
      @FormDataParam("firstname") String firstname, @FormDataParam("lastname") String lastname,
      @FormDataParam("phone") String phone) {
    // Get and check credentials
    checkInfoUser(email, password, firstname, lastname, phone);

    if (password.length() < 6) {
      throw new WebApplicationException("The password provided is too short.",
          Response.Status.BAD_REQUEST);
    }
    // Checks if email has the correct format
    if (!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email)) {
      throw new WebApplicationException("The email address has the wrong format",
          Response.Status.BAD_REQUEST);
    }
    if (!phone.matches("^\\d{4}/\\d{2}\\.\\d{2}\\.\\d{2}$") && !phone.matches(
        "^04\\d{2}[\\s]\\d{2}[\\s]\\d{2}[\\s]\\d{2}$")) {
      throw new WebApplicationException("The phone number is not valid.",
          Response.Status.BAD_REQUEST);
    }

    UserDTO userToTryRegister = userFactory.getUser();
    userToTryRegister.setRole(0);
    userToTryRegister.setPassword(password);
    userToTryRegister.setFirstName(firstname);
    userToTryRegister.setLastName(lastname);
    userToTryRegister.setPhone(phone);
    userToTryRegister.setEmail(email);
    userToTryRegister.setImage(fileDisposition.getFileName());

    // Try to register
    UserDTO publicUser = userUCC.register(userToTryRegister, file);

    if (publicUser == null) {
      throw new WebApplicationException("Could not register the user.",
          Response.Status.UNAUTHORIZED);
    }
    return publicUser;
  }


  /**
   * Checks if the received JSON contains both the email and the password.
   *
   * @param email     the email of user.
   * @param password  the password of user.
   * @param firstname the firstname of user.
   * @param lastname  the lastname of user.
   * @param phone     the phone of user.
   * @throws WebApplicationException with 400 Bad Request status if one field is missing from the
   */
  public void checkInfoUser(String email, String password, String firstname, String lastname,
      String phone) {
    // Checks if the received JSON contains both the email and the password
    if (email.equals("") || password.equals("") || firstname.equals("")
        || lastname.equals("") || phone.equals("")) {
      throw new WebApplicationException("One or more fields are required.",
          Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Checks if the received JSON contains both the email and the password.
   *
   * @param email     the email of user.
   * @param firstname the firstname of user.
   * @param lastname  the lastname of user.
   * @param phone     the phone of user.
   * @throws WebApplicationException with 400 Bad Request status if one field is missing from the
   */
  public void checkInfoUser(String email, String firstname, String lastname,
      String phone) {
    // Checks if the received JSON contains both the email and the password
    if (email.equals("") || firstname.equals("")
        || lastname.equals("") || phone.equals("")) {
      throw new WebApplicationException("One or more fields are required.",
          Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Retrieves a list of all users from the user use case controller.
   *
   * @return a list of UserDTO objects representing all users in the system
   */
  @GET
  @Path("/getAll")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("ADMIN")
  public List<UserDTO> getAllUsers() {
    return userUCC.getAllUsers();
  }

  /**
   * confirm a user as an helper.
   *
   * @param id   the id of the user to confirm.
   * @param json the NodeObject contains "newRole": the new role of the User as an Int (0 for user,
   *             1 for helper, 2 for admin).
   * @return the user information as a JSON object if authentication is successful
   */
  @POST
  @Path("/changeRoleUser/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("ADMIN")
  public UserDTO changeRoleUser(@PathParam("id") int id, JsonNode json) {
    int role = json.get("newRole").asInt();
    if (role < 0 || role > 2) {
      throw new WebApplicationException("the newRole must be 0, 1 or 2.",
          Response.Status.BAD_REQUEST);
    }
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    UserDTO user = userUCC.changeRoleUser(id, role);
    if (user == null) {
      throw new WebApplicationException("The user you are searching may not exist",
          Response.Status.BAD_REQUEST);
    }

    return user;
  }


  /**
   * search a user by id.
   *
   * @param id the id of the user to find.
   * @return the user.
   */
  @GET
  @Path("/getUserById/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public UserDTO getUserById(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    UserDTO user = userUCC.getUserById(id);

    if (user == null) {
      throw new WebApplicationException("The user you are searching may not exist",
          Response.Status.BAD_REQUEST);
    }
    return user;
  }

}
