package roboviz.scene.spl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;

import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec3f;
import roboviz.robot.RobotJoint;
import roboviz.robot.RobotModel;

/**
 * Network class that updates the robot model.
 * 
 * @author justin
 */
public class RobotController {

  private static final int BUFFER_SIZE = 256;
  byte[]                   buf         = new byte[BUFFER_SIZE];

  RobotModel[]             models;
  ReceiveThread            receiver    = new ReceiveThread();
  DatagramSocket           socket;

  public RobotController(RobotModel[] models, int port) throws SocketException, UnknownHostException {
    this.models = models;
    socket = new DatagramSocket(port);
    receiver.start();
  }

  public void shutdown() {
    receiver.running = false;
    receiver.interrupt();
    if (socket != null)
      socket.close();
  }

  private void parse(ByteBuffer buf) {
    RobotModel model = models[getUByte(buf)];
    Vec3f center = new Vec3f(getFloat(buf), getFloat(buf), getFloat(buf));
    Vec3f forward = new Vec3f(getFloat(buf), getFloat(buf), getFloat(buf));
    Vec3f up = new Vec3f(getFloat(buf), getFloat(buf), getFloat(buf));
    
    List<RobotJoint> joints = model.getJoints();
    for (int i = 0; i < 22; i++)
      joints.get(i).setRadians(getFloat(buf));
    
    Vec3f left = up.cross(forward).normalized();
    Mat4f m = new Mat4f(
        forward.x, left.x, up.x, 0,
        forward.y, left.y, up.y, 0,
        forward.z, left.z, up.z, 0,
        0, 0, 0, 1).inverse();
    
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

  private class ReceiveThread extends Thread {
    boolean running = true;

    @Override
    public void run() {
      while (running) {
        try {
          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          socket.receive(packet);
          parse(ByteBuffer.wrap(packet.getData(), 0, packet.getLength()));
        } catch (IOException e) {
        }
      }
      if (socket != null)
        socket.close();
    }
  }
}
