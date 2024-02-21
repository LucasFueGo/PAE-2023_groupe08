package be.vinci.pae.api;


import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.domain.ObjectDTO;
import be.vinci.pae.domain.ObjectFactory;
import be.vinci.pae.domain.ObjectUCC;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * ObjectRessource class.
 */

@Singleton
@Path("object")
public class ObjectResource {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private ObjectFactory objectFactory;

  @Inject
  private ObjectUCC objectUCC;

  /**
   * Drops an object in the workshop.
   *
   * @param id The id of the object to drop.
   * @return An ObjectNode representing the dropped object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @POST
  @Path("/toWorkShop/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode toWorkShop(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO objectDTO = objectUCC.dropObjectInTheWorkShop(id);
    if (objectDTO == null) {
      throw new WebApplicationException("The object's state couldn't be updated.",
          Status.PRECONDITION_FAILED);
    }

    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);
  }

  /**
   * Drops an object for sale.
   *
   * @param id   The id of the object to drop.
   * @param json The json object to work on
   * @return An ObjectNode representing the dropped object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @POST
  @Path("/toSale/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode toSale(@PathParam("id") int id, JsonNode json) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("price")) {
      throw new WebApplicationException("price required",
          Response.Status.BAD_REQUEST);
    }
    double price = json.get("price").asDouble();

    ObjectDTO objectDTO = objectUCC.dropObjectForSale(id, price);
    if (objectDTO == null) {
      throw new WebApplicationException("The object you are searching may not exist.",
          Status.PRECONDITION_FAILED);
    }

    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);
  }


  /**
   * Changes the state of an object to "AS".
   *
   * @param id The id of the object to drop.
   * @return An ObjectNode representing the updated object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @POST
  @Path("/toStore/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode toStore(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO objectDTO = objectUCC.dropObjectInTheStore(id);
    if (objectDTO == null) {
      throw new WebApplicationException("The object's state couldn't be updated.",
          Status.PRECONDITION_FAILED);
    }

    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);
  }

  /**
   * Returns the object with the specified id.
   *
   * @param id The id of the object to return.
   * @return An ObjectNode representing the object.
   * @throws WebApplicationException If the object does not exist or is not in a good state.
   */
  @GET
  @Path("/getOne/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectDTO getOneObjectById(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO objectDTO = objectUCC.getObjectById(id);
    if (objectDTO == null) {
      throw new WebApplicationException("The object you are searching may not exist.",
          Status.NOT_FOUND);
    } else {
      return objectDTO;
    }
  }


  /**
   * Refuses an object with the specified ID.
   *
   * @param id   the ID of the object to refuse
   * @param json the json object to work on
   * @return an ObjectNode containing the refused object as a JSON object
   */
  @POST
  @Path("/refuse/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode refuseObject(@PathParam("id") int id, JsonNode json) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("refusal_notification")) {
      throw new WebApplicationException("A notification message is required for that request.",
          Response.Status.BAD_REQUEST);
    }

    String refusalNotification = json.get("refusal_notification").asText();

    ObjectDTO objectDTO = objectUCC.refuseObject(id, refusalNotification);
    if (objectDTO == null) {
      throw new WebApplicationException("The object you are searching may not exist.",
          Status.NOT_FOUND);
    }
    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);
  }

  /**
   * Changes the state of an object to "SA".
   *
   * @param id The id of the object to change the state.
   * @return An ObjectNode representing the updated object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @POST
  @Path("/toAccept/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode toAccept(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO objectDTO = objectUCC.acceptObject(id);

    if (objectDTO == null) {
      throw new WebApplicationException("The object's state couldn't be updated.",
          Status.PRECONDITION_FAILED);
    }

    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);
  }

  /**
   * Drops an object for sale.
   *
   * @param id The id of the object to drop.
   * @return An ObjectNode representing the dropped object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @POST
  @Path("/toSold/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode toSold(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO objectDTO = objectUCC.dropObjectSold(id);
    if (objectDTO == null) {
      throw new WebApplicationException("The object you are searching may not exist.",
          Status.PRECONDITION_FAILED);
    }

    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);
  }

  /**
   * update the fields "description", "url", and "idType" of an object with its ID.
   *
   * @param file            The file that will be the new image.
   * @param fileDisposition The file that will be the new image.
   * @param idObject        The id of object.
   * @param description     The description of object.
   * @param idType          The idType of object.
   * @return An ObjectNode representing the new version object.
   * @throws WebApplicationException If the idObject, description , url , idType is missing or the
   *                                 object does not exist.
   */
  @POST
  @Path("/updateObject")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode updateObjectById(@FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @FormDataParam("idObject") int idObject, @FormDataParam("description") String description,
      @FormDataParam("idType") int idType) {

    if (idObject < 0 || idType < 0 || description.equals("")) {
      throw new WebApplicationException(
          "¨Params are not good, please send file, fileDisposition, idObject, "
              + "description , url , idType.",
          Response.Status.BAD_REQUEST);
    }

    ObjectDTO objectDTO = objectUCC.updateObjectById(idObject, description, file, fileDisposition,
        idType);

    if (objectDTO == null) {
      throw new WebApplicationException("The object you are searching may not exist.",
          Status.PRECONDITION_FAILED);
    }

    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);

  }


  /**
   * get all visible object.
   *
   * @param idType the ID of the type of object
   * @return an ObjectNode containing the refused object as a JSON object
   */
  @GET
  @Path("/getAll")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<ObjectDTO> getObjectsByType(@DefaultValue("0") @QueryParam("id") int idType) {
    if (idType < 0) {
      throw new WebApplicationException("The type of object you are searching may not exist.",
          Response.Status.BAD_REQUEST);
    }

    return idType == 0 ? objectUCC.getAllObjects()
        : objectUCC.getObjectsByType(idType);
  }

  /**
   * get all visible object.
   *
   * @return an ObjectNode containing the refused object as a JSON object
   */
  @GET
  @Path("/getAllObjectPropose")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public List<ObjectDTO> getAllObjectPropose() {
    return objectUCC.getAllObjects(false, true, false);
  }

  /**
   * get all visible object.
   *
   * @return an ObjectNode containing the refused object as a JSON object
   */
  @GET
  @Path("/getAllHome")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<ObjectDTO> getAllObjectHome() {
    return objectUCC.getAllObjects(true, false, true);
  }

  /**
   * Get all objects.
   *
   * @return a List of ObjectDTO
   */
  @GET
  @Path("/getAbsolutelyAllObject")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public List<ObjectDTO> getAbsolutelyAllObjects() {
    return objectUCC.getAbsolutelyAllObjects();
  }

  /**
   * This method is used to retrieve all object pictures belonging to a user with the specified ID.
   *
   * @param id The ID of the user whose object pictures are being retrieved.
   * @return A list of ObjectDTO objects representing the object pictures belonging to the user.
   * @throws WebApplicationException If no ID is provided in the request, a WebApplicationException
   *                                 with a BAD_REQUEST status is thrown.
   */
  @GET
  @Path("/getAllObjectsOfAUser/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("USER")
  public List<ObjectDTO> getAllObjectsOfAUser(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    return objectUCC.getAllObjectsOfAUser(id);
  }

  /**
   * propose an object.
   *
   * @param json the json object to work on
   * @return a boolean to know if the object was proposed
   */
  @POST
  @Path("/proposeObject")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode proposeObject(JsonNode json) {

    // Vérification des différents champs

    if (!json.hasNonNull("idDate")) {
      throw new WebApplicationException("A date is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("idTimeSlot")) {
      throw new WebApplicationException("A time slot is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("idType")) {
      throw new WebApplicationException("A type is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("description")) {
      throw new WebApplicationException("A description is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    String description = json.get("description").asText();
    if (description.isEmpty() || description.length() > 120) {
      throw new WebApplicationException(
          "The description must not be empty and must be less than 120 characters.",
          Response.Status.BAD_REQUEST);
    }
    String phoneNumber = "";
    int idUser;
    if (!json.hasNonNull("token")) {
      if (!json.hasNonNull("phoneNumber")) {
        throw new WebApplicationException("A phone number is required for that request.",
            Response.Status.BAD_REQUEST);
      }
      phoneNumber = json.get("phoneNumber").asText();
      if (phoneNumber.isEmpty()) {
        throw new WebApplicationException("The phone number must not be empty.",
            Response.Status.BAD_REQUEST);
      }
      idUser = 0;
    } else {

      // vérification du token si il existe

      JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).build();
      DecodedJWT jwt = jwtVerifier.verify(json.get("token").asText());
      Date expirationDate = jwt.getExpiresAt();
      if (expirationDate.before(new Date())) {
        throw new WebApplicationException("Invalid Token.", Response.Status.UNAUTHORIZED);
      }
      idUser = jwt.getClaim("id").asInt();
    }

    // extraction des données

    int idDate = json.get("idDate").asInt();
    int idTimeSlot = json.get("idTimeSlot").asInt();
    int idType = json.get("idType").asInt();

    // création de l'objet à renvoyer

    ObjectDTO object1 = objectFactory.getObject();
    object1.setIdPassage(idDate);
    object1.setIdTimeSlot(idTimeSlot);
    object1.setIdType(idType);
    object1.setDescription(description);
    if (!phoneNumber.isEmpty()) {
      object1.setOfferingMemberPhone(phoneNumber);
    } else {
      object1.setIdUser(idUser);
      object1.setOfferingMemberPhone("");
    }

    int objectID = objectUCC.proposeObject(object1);

    if (objectID < 0) {
      throw new WebApplicationException("Could not propose the object.",
          Response.Status.UNAUTHORIZED);
    }
    return jsonMapper.createObjectNode().putPOJO("objectID", objectID);

  }

  /**
   * get all statistics for the objects on a specific time range.
   *
   * @param json the json object to work on
   * @return a boolean to know if the object was proposed
   */
  @POST
  @Path("/getStatistics")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, String> getStatisticsOnObjects(JsonNode json) {
    String startingDate = json.get("start-date").asText();
    String endingDate = json.get("end-date").asText();
    return objectUCC.getStatistics(startingDate, endingDate);
  }

  /**
   * get image of an object.
   *
   * @param idObject        the id of the object
   * @param file            the file of the object
   * @param fileDisposition the fileDisposition of the object
   * @return An ObjectNode representing the updated object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public boolean uploadImageObject(@FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @FormDataParam("idObject") int idObject) {
    if (idObject < 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    boolean response = objectUCC.upload(idObject, file, fileDisposition);

    if (!response) {
      throw new WebApplicationException("The object's state couldn't be updated.",
          Status.PRECONDITION_FAILED);
    }
    return response;
  }


  /**
   * get image of an object.
   *
   * @param id The id of the object.
   * @return An ObjectNode representing the updated object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @GET
  @Path("/getImageObject/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode getOneImageObject(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO objectDTO = objectUCC.getObjectById(id);

    if (objectDTO == null) {
      throw new WebApplicationException("The object's state couldn't be updated.",
          Status.PRECONDITION_FAILED);
    }
    return jsonMapper.createObjectNode()
        .putPOJO("object_image", objectDTO.getUrl());
  }


  /**
   * withdraw an object from sale.
   *
   * @param id The id of the object.
   * @return an ObjectNode containing the withdraw object as a JSON object
   */
  @POST
  @Path("/withdrawObject/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public ObjectNode withdrawObject(@PathParam("id") int id) {
    if (id <= 0) {
      throw new WebApplicationException("A valid ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO object = objectUCC.withdrawObject(id);

    if (object == null) {
      throw new WebApplicationException("The object's state couldn't be updated.",
          Status.PRECONDITION_FAILED);
    }
    return jsonMapper.createObjectNode().putPOJO("object", object);
  }


  /**
   * Drops an object for sale and checking if the state is .
   *
   * @param id   The id of the object to drop.
   * @param json with the price of the object.
   * @return An ObjectNode representing the dropped object.
   * @throws WebApplicationException If the id is missing or the object does not exist or is not in
   *                                 a good state.
   */
  @POST
  @Path("/toSoldDirectly/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("ADMIN")
  public ObjectNode toSoldDirectly(@PathParam("id") int id, JsonNode json) {
    if (!json.hasNonNull("price")) {
      throw new WebApplicationException("A price is required for that request.",
          Response.Status.BAD_REQUEST);
    }

    double price = json.get("price").asDouble();
    System.out.println(price);
    if (id == 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    ObjectDTO objectDTO = objectUCC.dropObjectSoldDirectly(id, price);
    if (objectDTO == null) {
      throw new WebApplicationException("The object you are searching may not exist.",
          Status.PRECONDITION_FAILED);
    }

    return jsonMapper.createObjectNode().putPOJO("object", objectDTO);
  }
}

