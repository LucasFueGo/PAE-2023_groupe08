package be.vinci.pae.domain;

import be.vinci.pae.exception.BizException;
import be.vinci.pae.services.DALServices;
import be.vinci.pae.services.FilterDAO;
import jakarta.inject.Inject;
import java.util.List;

/**
 * FilterUCCImpl class.
 */
public class FilterUCCImpl implements FilterUCC {

  @Inject
  private FilterDAO filterDAO;

  @Inject
  private DALServices dalServices;

  /**
   * get all filters.
   *
   * @return List of filters
   */
  @Override
  public List<FilterDTO> getAllFilters() {
    try {
      dalServices.startTransaction();
      List<FilterDTO> filters = filterDAO.getAllFilters();
      if (filters == null) {
        throw new BizException(
            "OBJECT TYPES (no filters found) : Could not return a list "
                + "of the all object types available.");
      }
      dalServices.commitTransaction();
      return filters;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }
}
