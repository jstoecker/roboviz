package roboviz.robot;

import jgl.math.Maths;
import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec3f;

/**
 * Joint with one degree of freedom around an axis.
 * 
 * @author justin
 */
public class RobotJoint extends RobotPart {

  protected Vec3f axis       = new Vec3f(0, 0, 1);
  protected float radians    = 0;
  protected float minRadians = 0;
  protected float maxRadians = 0;

  public RobotJoint() {
  }

  public RobotJoint(String name, RobotPart parent, Vec3f offset, Vec3f axis, float minRadians,
      float maxRadians) {
    super(name, parent, offset);
    this.axis = axis;
    this.minRadians = minRadians;
    this.maxRadians = maxRadians;
    setRadians(radians);
    updateLocalTransform();
  }

  public void setAxis(Vec3f axis) {
    this.axis = axis;
  }

  public void setMinRadians(float minRadians) {
    this.minRadians = minRadians;
    setRadians(radians);
  }
  
  public void setMaxRadians(float maxRadians) {
    this.maxRadians = maxRadians;
    setRadians(radians);
  }
  
  public void setMinDegrees(float minDegrees) {
    setMinRadians(Maths.toRadians(minDegrees));
  }
  
  public void setMaxDegrees(float maxDegrees) {
    setMaxRadians(Maths.toRadians(maxDegrees));
  }

  public void setRadians(float radians) {
    this.radians = Maths.clamp(radians, minRadians, maxRadians);
    updateLocalTransform();
  }
  
  public void setDegrees(float degrees) {
    setRadians(Maths.toRadians(degrees));
  }

  public Vec3f getAxis() {
    return axis;
  }

  public float getRadians() {
    return radians;
  }

  public float getMinRadians() {
    return minRadians;
  }
  
  public float getMaxRadians() {
    return maxRadians;
  }
  
  public float getDegrees() {
    return Maths.toDegrees(radians);
  }

  @Override
  protected void updateLocalTransform() {
    Mat4f t = Transform.translation(offset);
    Mat4f r = Transform.rotation(axis, radians);
    localTransform = t.times(r);
  }
}
