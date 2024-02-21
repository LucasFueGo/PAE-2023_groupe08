package be.vinci.pae.services;

import be.vinci.pae.domain.FilterDTO;
import be.vinci.pae.domain.FilterFactory;
import be.vinci.pae.exception.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FilterDAOImpl class.
 */
public class FilterDAOImpl implements FilterDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private FilterFactory myFilterFactory;

  /**
   * get all filters.
   *
   * @return list of filters
   */
  @Override
  public List<FilterDTO> getAllFilters() {
    List<FilterDTO> filterList = new ArrayList<>();
    try (PreparedStatement dalServicesPreparedStatement = dalBackendServices.getPreparedStatement(
        "SELECT t.* FROM riezrecup.object_types t")) {
      try (ResultSet rs = dalServicesPreparedStatement.executeQuery()) {
        while (rs.next()) {
          FilterDTO filter = myFilterFactory.getFilter();
          fromResultSetToFilterDTO(rs, filter);
          filterList.add(filter);
        }
      }
    } catch (SQLException e) {
      throw new FatalException("A SQL error with the methode getAllFilters()", e);
    }
    return filterList;
  }


  private void fromResultSetToFilterDTO(ResultSet rs, FilterDTO filter) throws SQLException {
    filter.setId(rs.getInt(1));
    filter.setName(rs.getString(2));
  }

}
