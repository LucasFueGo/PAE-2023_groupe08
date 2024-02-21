package be.vinci.pae.api;


import be.vinci.pae.domain.FilterDTO;
import be.vinci.pae.domain.FilterUCC;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * ObjectRessource class.
 */

@Singleton
@Path("filter")
public class FilterResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private FilterUCC filterUCC;

  /**
   * Refuses an object with the specified ID.
   *
   * @return an ObjectNode containing the refused object as a JSON object
   */
  @GET
  @Path("/getAll")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<FilterDTO> getObjectsByType() {
    return filterUCC.getAllFilters();
  }

}
