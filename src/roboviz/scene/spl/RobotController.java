package roboviz.scene.spl;

import java.nio.ByteBuffer;
import java.util.List;

import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec3f;
import roboviz.draw.SceneCommandParser;
import roboviz.robot.RobotJoint;
import roboviz.robot.RobotModel;

/**
 * Network class that updates the robot model.
 * 
 * @author justin
 */
public class RobotController implements SceneCommandParser {

  private RobotModel[] models;

  public RobotController(RobotModel[] models) {
    this.models = models;
  }

  @Override
  public void parse(ByteBuffer buf) {
    int modelIndex = getUByte(buf);
    RobotModel model = models[modelIndex];
    Vec3f center = new Vec3f(getFloat(buf), getFloat(buf), getFloat(buf));
    Vec3f forward = new Vec3f(getFloat(buf), getFloat(buf), getFloat(buf)).normalize();
    Vec3f up = new Vec3f(getFloat(buf), getFloat(buf), getFloat(buf)).normalize();
    
    List<RobotJoint> joints = model.getJoints();
    for (int i = 0; i < 22; i++)
      joints.get(i).setRadians(getFloat(buf));

    Vec3f left = up.cross(forward).normalized();
    Mat4f m = new Mat4f(forward.x, left.x, up.x, 0, forward.y, left.y, up.y, 0, forward.z, left.z,
        up.z, 0, 0, 0, 0, 1).inverse();

    System.out.println("center = " + center);
    System.out.println("forward = " + forward);
    System.out.println("up = " + up);
    System.out.println();
    
    model.getRoot().setLocalTransform(Transform.translation(center).times(m));
  }

  static int getUByte(ByteBuffer buf) {
    byte byteVal = buf.get();
    return byteVal < 0 ? 256 + byteVal : byteVal;
  }

  static float getFloat(ByteBuffer buf) {
    byte[] chars = new byte[6];
    buf.get(chars);
    return Float.parseFloat(new String(chars));
  }
}
