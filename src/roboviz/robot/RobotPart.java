package roboviz.robot;

import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec3f;

/**
 * A robot joint or geometry.
 * 
 * @author justin
 */
public class RobotPart {

  protected String          name;
  protected RobotPart       parent          = null;
  protected Vec3f           offset          = new Vec3f(0);
  protected Vec3f           position        = new Vec3f(0);
  protected List<RobotPart> children        = new ArrayList<RobotPart>();
  protected Mat4f           globalTransform = Transform.identity();
  protected Mat4f           localTransform  = Transform.identity();

  public RobotPart() {
  }

  public RobotPart(String name, RobotPart parent, Vec3f offset) {
    this.name = name;
    this.parent = parent;
    this.offset = offset;
    updateLocalTransform();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOffset(Vec3f offset) {
    this.offset = offset;
    updateLocalTransform();
  }

  public void setParent(RobotPart parent) {
    this.parent = parent;
    updateLocalTransform();
  }
  
  public String getName() {
    return name;
  }

  public Vec3f getPosition() {
    return position;
  }

  public Mat4f getLocalTransform() {
    return localTransform;
  }

  public Mat4f getGlobalTransform() {
    return globalTransform;
  }

  public List<RobotPart> getChildren() {
    return children;
  }

  protected void updateLocalTransform() {
    localTransform = Transform.translation(offset);
  }
  
  public void setLocalTransform(Mat4f localTransform) {
    this.localTransform = localTransform;
  }

  protected void update() {
    globalTransform = (parent == null) ? localTransform 
        : parent.getGlobalTransform().times(localTransform);
    position.x = globalTransform.value(0, 3);
    position.y = globalTransform.value(1, 3);
    position.z = globalTransform.value(2, 3);
    
    for (RobotPart child : children)
      child.update();
  }
}
