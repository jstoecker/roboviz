package roboviz.scene.spl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import roboviz.util.ConfigFile;

public class Config extends ConfigFile {

  // units in meters
  public float fieldLength          = 9.0f;
  public float fieldWidth           = 6.0f;
  public float lineWidth            = 0.05f;
  public float penaltyMarkSize      = 0.1f;
  public float penaltyAreaLength    = 0.6f;
  public float penaltyAreaWidth     = 2.2f;
  public float penaltyMarkDistance  = 1.8f;
  public float centerCircleDiameter = 1.5f;
  public float borderStripWidth     = 0.7f;

  public float goalBarDiameter      = 0.1f;
  public float goalWidth            = 1.5f;
  public float goalHeight           = 0.8f;
  public float goalLength           = 0.5f;

  public Config(File file) {
    super(file);
  }

  @Override
  public void save() {
    try {
      DumperOptions opts = new DumperOptions();
      opts.setPrettyFlow(true);
      Yaml yaml = new Yaml(opts);
      FileOutputStream out = new FileOutputStream(file);
      out.write(yaml.dump(Config.this).getBytes());
      out.close();
    } catch (IOException e) {
      System.err.println("ERROR writing file: " + file);
    }
  }
}
