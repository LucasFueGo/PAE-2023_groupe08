package be.vinci.pae.services.utils;

import be.vinci.pae.utils.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Json class.
 */
public class Json<T> {

  private static final String DB_FILE_PATH = Config.getProperty("DatabaseFilePath");
  private static final ObjectMapper jsonMapper = new ObjectMapper();
  private static final Path pathToDb = Paths.get(DB_FILE_PATH);
  private final Class<T> type;

  // Java generics are mostly compile time, this means that the type information is lost at runtime.
  // To get the type information at runtime you have to add it as an argument of the constructor.

  /**
   * Json's class constructor for generic type.
   *
   * @param type The class type to be used
   */
  public Json(Class<T> type) {
    this.type = type;
  }

  /**
   * Serializes a list of objects to JSON format and writes the resulting string to a file with the
   * specified collection name.
   *
   * @param items          the list of objects to be serialized
   * @param collectionName the name of the collection to be used for the resulting file
   */
  public void serialize(List<T> items, String collectionName) {
    try {
      // if no DB file, write a new collection to a new db file
      if (!Files.exists(pathToDb)) {
        // Create an object and add a JSON array as POJO, e.g. { items:[...]}
        ObjectNode newCollection = jsonMapper.createObjectNode().putPOJO(collectionName, items);
        jsonMapper.writeValue(pathToDb.toFile(),
            newCollection); // write the JSON Object in the DB file
        return;
      }
      // get all collections : can be read as generic JsonNode, if it can be Object or Array;
      JsonNode allCollections = jsonMapper.readTree(
          pathToDb.toFile()); // e.g. { users:[...], items:[...]}
      // remove current collection, e.g. remove the array of items
      if (allCollections.has(collectionName)) {
        ((ObjectNode) allCollections).remove(collectionName); //e.g. it leaves { users:[...]}
      }
      ArrayNode updatedCollection = jsonMapper.valueToTree(items);
      // Add the JSON array in allCollections, e.g. : { users:[...], items:[...]}
      ((ObjectNode) allCollections).putArray(collectionName).addAll(updatedCollection);
      // write to the db file allCollections
      jsonMapper.writeValue(pathToDb.toFile(), allCollections);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads a JSON file at the specified path and returns the contents of the requested collection as
   * a list of POJOs. The collection does not exist or the file does not exist, an empty list is
   * returned.
   *
   * @param collectionName the name of the collection to retrieve
   * @return a list of POJOs representing the contents of the requested collection, or an empty list
   */
  public List<T> parse(String collectionName) {
    try {
      // get allCollections
      JsonNode node = jsonMapper.readTree(pathToDb.toFile());
      // accessing value of the specified field of an object node,
      // e.g. the JSON array within "items" field of { users:[...], items:[...]}
      JsonNode collection = node.get(collectionName);
      if (collection == null) {
        return new ArrayList<T>();
      }
      // convert the JsonNode to a List of POJOs & return it
      return jsonMapper.readerForListOf(type).readValue(collection);
    } catch (FileNotFoundException e) {
      return new ArrayList<T>(); // send an empty list if there is no db file
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<T>();
    }
  }


}

