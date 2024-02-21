package be.vinci.pae.utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Config class.
 */
public class Config {

  private static Properties props;

  /**
   * Loads a properties file from the file system.
   *
   * @param file the path to the properties file
   * @throws WebApplicationException if an error occurs while loading the file
   */
  public static void load(String file) {
    props = new Properties();
    try (InputStream input = new FileInputStream(file)) {
      props.load(input);
    } catch (IOException e) {
      throw new WebApplicationException(
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type("text/plain")
              .build());
    }
  }

  /**
   * Get the value of the property associated with the given key.
   *
   * @param key the key of the property to retrieve
   * @return the value of the property, or null if the key is not found
   */
  public static String getProperty(String key) {
    return props.getProperty(key);
  }


  /**
   * Gets the integer value associated with the specified key from the loaded properties file.
   *
   * @param key the key to look up the integer value
   * @return the integer value associated with the specified key, or null if the key does not exist
   */
  public static Integer getIntProperty(String key) {
    return Integer.parseInt(props.getProperty(key));
  }

  /**
   * Returns a boolean value corresponding to the value of the specified boolean property key.
   *
   * @param key the key of the boolean property
   * @return the boolean value corresponding to the value of the specified boolean property key
   */
  public static boolean getBoolProperty(String key) {
    return Boolean.parseBoolean(props.getProperty(key));
  }

}
