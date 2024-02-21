package be.vinci.pae.services;

import be.vinci.pae.exception.DALException;
import be.vinci.pae.utils.Config;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Implementation of the DALServices interface. Used to create a connection and query the database.
 */
public class DALBackendServicesImpl implements DALServices, DALBackendServices {

  private final ThreadLocal<Connection> threadLocalConn = new ThreadLocal<>();
  private final DataSource dataSource;

  /**
   * UserDataServiceImpl's public constructor.
   */
  public DALBackendServicesImpl() {
    //test driver
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.out.println("Missing PostgreSQL driver!");
      System.exit(1);
    }

    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl(Config.getProperty("DatabasePath"));
    this.dataSource = dataSource;

    try {
      // test the connection
      dataSource.getConnection().close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Unable to connect to the database!");
      System.exit(1);
    }
  }

  /**
   * Returns the PreparedStatement to execute the given SQL query.
   *
   * @param sql the SQL query to execute
   * @return a PreparedStatement to execute the given SQL query
   */
  public PreparedStatement getPreparedStatement(String sql) {
    try {
      Connection connectionPreparedStatement = threadLocalConn.get();
      return connectionPreparedStatement.prepareStatement(sql);
    } catch (SQLException e) {
      throw new DALException("get PreparedStatement : error in DAL with the sql : " + sql + "\n",
          e);
    }
  }

  /**
   * Starts a new transaction by obtaining a connection from the data source and setting its
   * auto-commit mode to false. If a connection is already associated with the current thread, it
   * will be reused instead of obtaining a new one.
   *
   * @throws RuntimeException if an error occurs while obtaining a connection or setting its
   *                          auto-commit mode.
   */
  @Override
  public void startTransaction() {
    Connection conn = threadLocalConn.get();
    try {
      if (conn == null) {
        conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        threadLocalConn.set(conn);
      }
    } catch (SQLException e) {
      throw new DALException("startTransaction : error in DAL\n", e);
    }
  }

  /**
   * Commits the current transaction by committing the associated connection and releasing it from
   * the current thread. If no connection is associated with the current thread, this method does
   * nothing.
   *
   * @throws RuntimeException if an error occurs while committing the connection or closing it.
   */
  @Override
  public void commitTransaction() {
    try (Connection conn = threadLocalConn.get()) {
      if (conn != null) {
        threadLocalConn.remove();
        conn.commit();
      }
    } catch (SQLException e) {
      throw new DALException("commitTransaction : error in DAL\n", e);
    }
  }


  /**
   * Rolls back the current transaction by rolling back the associated connection and releasing it
   * from the current thread. If no connection is associated with the current thread, this method
   * does nothing.
   *
   * @throws RuntimeException if an error occurs while rolling back the connection or closing it.
   */
  @Override
  public void rollbackTransaction() {
    try (Connection conn = threadLocalConn.get()) {
      if (conn != null) {
        threadLocalConn.remove();
        conn.rollback();
      }
    } catch (SQLException e) {
      throw new DALException("rollbackTransaction : error in DAL\n", e);
    }
  }
}
