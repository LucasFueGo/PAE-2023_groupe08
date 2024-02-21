package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.domain.AvailableDTO;
import be.vinci.pae.domain.AvailableUCC;
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
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Date;
import java.util.List;

/**
 * AvailableRessource class.
 */
@Singleton
@Path("available")
public class AvailableResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private AvailableUCC availableUCC;


  /**
   * get all available.
   *
   * @return an ObjectNode containing the available as a JSON object
   */
  @GET
  @Path("/getAll")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<AvailableDTO> getAvailable() {
    return availableUCC.getAllAvailable();
  }


  /**
   * Returns the available with the specified id.
   *
   * @param id The id of the available to return.
   * @return An ObjectNode representing the available.
   * @throws WebApplicationException If the available does not exist.
   */
  @GET
  @Path("/getOne/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode getAvailableById(@PathParam("id") int id) {
    if (id < 0) {
      throw new WebApplicationException("An ID is required for that request.",
          Response.Status.BAD_REQUEST);
    }
    System.out.println(id);
    AvailableDTO availableDTO = availableUCC.getAvailableById(id);
    if (availableDTO == null) {
      throw new WebApplicationException("The available you are searching may not exist.",
          Status.NOT_FOUND);
    }
    return jsonMapper.createObjectNode().putPOJO("object", availableDTO);
  }

  /**
   * add a new available. need the Date as day and a boolean ton say if is on the mornig as
   * isMornig
   *
   * @param json the json object to add availability
   * @return true if the availble as been add.
   */
  @POST
  @Path("/addAvailable")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize("HELPER")
  public boolean createOne(JsonNode json) {
    // Get and check credentials
    if (!json.hasNonNull("day") || !json.hasNonNull("isMorning")) {
      throw new WebApplicationException("One or more fields are required.",
          Response.Status.BAD_REQUEST);
    }

    Date day = new Date(json.get("day").asLong());

    boolean isMorning = json.get("isMorning").asBoolean();

    if (!availableUCC.putNewAvailable(day, isMorning)) {
      throw new WebApplicationException("Could not add a new Avalaible.",
          Response.Status.UNAUTHORIZED);
    }
    return true;
  }

}
