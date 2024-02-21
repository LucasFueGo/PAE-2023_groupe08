package be.vinci.pae.services;

import java.sql.PreparedStatement;

/**
 * DALBackendServices interface.
 */
public interface DALBackendServices {

  /**
   * Returns the PreparedStatement.
   *
   * @param sql the sql's Query
   * @return PreparedStatement
   */
  PreparedStatement getPreparedStatement(String sql);
}
