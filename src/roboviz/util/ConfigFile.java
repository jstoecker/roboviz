/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

public class ConfigFile extends Properties {

  protected File file;

  public ConfigFile(File file) {
    this.file = file;
  }

  public static ConfigFile load(File file, Class<? extends ConfigFile> clazz) {
    try {
      Yaml yaml = new Yaml(new Constructor(clazz));
      ConfigFile config = (ConfigFile) yaml.load(new FileInputStream(file));
      config.file = file;
      return config;
    } catch (FileNotFoundException e) {
      return new ConfigFile(file);
    } catch (YAMLException e) {
      System.err.println("Could not parse config file -- using defaults");
      return new ConfigFile(file);
    }
  }

  public void save() {
    try {
      DumperOptions opts = new DumperOptions();
      opts.setPrettyFlow(true);
      Yaml yaml = new Yaml(opts);
      FileOutputStream out = new FileOutputStream(file);
      out.write(yaml.dump(this).getBytes());
      out.close();
    } catch (IOException e) {
      System.err.println("ERROR writing file: " + file);
    }
  }
}
