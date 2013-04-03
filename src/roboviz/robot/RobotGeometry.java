package roboviz.robot;

import jgl.math.vector.Vec3f;

public class RobotGeometry extends RobotPart {

  public String file;
  public Vec3f  translation = new Vec3f(0);
  public Vec3f  rotation    = new Vec3f(0);
  public Vec3f  scale       = new Vec3f(1);

  public RobotGeometry() {
  }
}
