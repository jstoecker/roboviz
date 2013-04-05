package roboviz.scene.spl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import jgl.math.vector.Vec3f;
import roboviz.draw.DrawManager;
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
  DrawManager              manager;
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

  private void parse(String message) {
    String[] tokens = message.split("\\s++");
    int id = Integer.parseInt(tokens[0]);
    int joint = Integer.parseInt(tokens[1]);
    
    if (joint == 255) {
      float x = Float.parseFloat(tokens[2]);
      float y = Float.parseFloat(tokens[3]);
      float z = Float.parseFloat(tokens[4]);
      models[id].getRoot().setOffset(new Vec3f(x,y,z));
    } else {
      float radians = Float.parseFloat(tokens[2]);
      models[id].getJoints().get(joint).setRadians(radians);
    }
  }
  
  private void parse(ByteBuffer buf) {
    while (buf.remaining() >= 8) {
      int id = getUByte(buf);
      int joint = getUByte(buf);
      
      if (joint == 255) {
        // joint 255 means translate x,y,z
        float x = getFloat(buf);
        float y = getFloat(buf);
        float z = getFloat(buf);
        if (id < models.length)
          models[id].getRoot().setOffset(new Vec3f(x, y, z));
      } else {
        // set joint radians
        float radians = getFloat(buf);
        if (id < models.length && joint < models[id].getJoints().size())
          models[id].getJoints().get(joint).setRadians(radians);
      }
    }
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
          parse(new String(packet.getData(), 0, packet.getLength()).trim());
//          parse(ByteBuffer.wrap(packet.getData(), 0, packet.getLength()));
        } catch (IOException e) {
        }
      }
      if (socket != null)
        socket.close();
    }
  }
}
