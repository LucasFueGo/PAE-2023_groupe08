package be.vinci.pae.services;

import be.vinci.pae.domain.FilterDTO;
import java.util.List;

/**
 * filterDAO class.
 */
public interface FilterDAO {

  /**
   * get all filters.
   *
   * @return list of filters
   */
  List<FilterDTO> getAllFilters();
}
