import java.awt.Color;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * Test drawing in RoboViz.
 * 
 * @author justin
 */
public class Test {

  // careful: all exceptions are thrown out in this example
  public static void main(String[] args) throws Exception {
    
    DrawBuffer drawings = new DrawBuffer(8192);
    
    float t = 0;
    
    while (true) {
      
      t+= 0.1f;
      
      drawings.line(-4, 3, 0, 4, 3, 0, 1, Color.RED, "test.lines");
      drawings.circle((float)Math.cos(t), 0, 2, .5f, Color.GREEN, "test.circles");
      
      drawings.swap("test");

      DatagramSocket socket = new DatagramSocket();
      List<DatagramPacket> packets = drawings.getPackets(InetAddress.getLocalHost(), 32769);
      for (DatagramPacket packet : packets) {
        socket.send(packet);
      }
      drawings.reset();

      Thread.sleep(20);
    }
  }
}
