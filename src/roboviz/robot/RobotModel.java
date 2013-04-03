package roboviz.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgl.math.vector.Vec3f;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Generic robot model.
 * 
 * @author justin
 */
public class RobotModel {

  private RobotPart              root;
  private List<RobotPart>        parts   = new ArrayList<RobotPart>();
  private List<RobotJoint>       joints  = new ArrayList<RobotJoint>();
  private Map<String, RobotPart> partMap = new HashMap<String, RobotPart>();

  public RobotModel() {
    root = new RobotPart("root", null, new Vec3f(0));
    partMap.put(root.name, root);
  }

  public RobotPart getRoot() {
    return root;
  }

  public List<RobotPart> getParts() {
    return parts;
  }

  public List<RobotJoint> getJoints() {
    return joints;
  }

  public RobotPart getPart(String name) {
    return partMap.get(name);
  }

  public void update() {
    root.update();
  }
  
  public void setParts(List<RobotPart> parts) {
    this.parts = parts;

    partMap.clear();
    joints.clear();
    
    for (RobotPart part : parts) {
      if (part.parent != null)
        part.parent.children.add(part);
      else
        root.children.add(part);

      if (part instanceof RobotJoint) {
        joints.add((RobotJoint) part);
      }
    }
    root.update();
  }

  public static RobotModel loadFromYAML(File file) {
    try {
      Constructor constructor = new Constructor(RobotModel.class);
      constructor.addTypeDescription(new TypeDescription(RobotJoint.class, "!joint"));
      constructor.addTypeDescription(new TypeDescription(RobotGeometry.class, "!geometry"));
      Yaml yaml = new Yaml(constructor);
      RobotModel model = (RobotModel) yaml.load(new FileInputStream(file));
      return model;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }
}
