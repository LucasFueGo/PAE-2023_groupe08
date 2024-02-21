package be.vinci.pae.exception;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * InformationLog class.
 */
public class InformationLog {

  private static final Logger logger = Logger.getLogger(BizException.class.getName());

  /**
   * fileLog to select a file.
   */
  FileHandler fileLog;

  /**
   * Constructor of InformationLog.
   *
   * @param message the message to write in log
   */
  public InformationLog(String message) {
    write(message);
  }

  /**
   * The methode to write the message in file log.
   *
   * @param message the message to write in log
   */
  public void write(String message) {
    try {
      int limit = 10000000; // 10 Mo en bytes
      int numFiles = 10; // nombre maximal de fichiers de log à conserver
      boolean append = true; // pour ajouter les logs au fichier existant

      fileLog = new FileHandler("src/logs/Info.log", limit, numFiles, append);
      SimpleFormatter formatter = new SimpleFormatter();
      fileLog.setFormatter(formatter);
      logger.addHandler(fileLog);
      logger.warning("Print du code : " + message);
      fileLog.close();

    } catch (IOException e) {
      System.out.println("Error with the Information's logger");
      throw new RuntimeException(e);
    }
  }
}
