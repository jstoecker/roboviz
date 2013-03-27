/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker
 *  The MIT License. See LICENSE in project root.
 *******************************************************************************/
package roboviz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads and writes configuration values from/to file for use in a RoboViz module. The configuration
 * file must be in the RoboViz config directory, which is set by the system property
 * "rv.config.dir".
 * 
 * @author justin
 */
public class ConfigFile extends Properties {

  private final String name;

  /**
   * Reads configuration properties from the given file name. Provide the name of the file as it
   * appears in the RoboViz configuration directory without the extension. For example, "rv_main"
   * for the file conf/rv_main.cfg where "conf/" is set by the system property rv.config.dir.
   */
  public ConfigFile(String name) {
    this.name = name + ".cfg";
    load();
  }
  
  /**
   * Saves values to the config object's stored file name (if not null).
   */
  public void save() {
    if (name != null) {
      try {
        File configFile = new File(System.getProperty("rv.config.dir"), name);
        store(new FileOutputStream(configFile), null);
      } catch (IOException e) {
      }
    }
  }

  /**
   * Loads values from provided file name (if not null). This is called automatically by the
   * constructor, so only call this to overwrite existing values.
   */
  public void load() {
    if (name != null) {
      try {
        File configFile = new File(System.getProperty("rv.config.dir"), name);
        load(new FileInputStream(configFile));
      } catch (IOException e) {
    	  System.err.println(e.getMessage());
      }
    }
  }
}
