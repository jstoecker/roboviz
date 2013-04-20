import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class TestSPL {

  static void writeByte(int value, ByteBuffer buf) {
    buf.put((byte)value);
  }

  static void writeFloat(float value, ByteBuffer buf) {
    buf.put(String.format("%6f", value).substring(0, 6).getBytes());
  }
  
  public static void main(String[] args) throws Exception {

    float time = 0;
    
    while (true) {

      ByteBuffer buf = ByteBuffer.allocate(187);
      writeByte(0, buf);

      writeFloat(1, buf);
      writeFloat(0, buf);
      writeFloat(0, buf);
      
      writeFloat((float)Math.cos(time/300), buf);
      writeFloat((float)Math.sin(time/300), buf);
      writeFloat(0, buf);
      
      writeFloat(0, buf);
      writeFloat(0, buf);
      writeFloat(1, buf);
      
      for (int i = 0; i < 22; i++) {
        writeFloat((float)Math.cos(time/100), buf);
      }
      
      buf.rewind();
      
      DatagramSocket socket = new DatagramSocket();
      socket.send(new DatagramPacket(buf.array(), 187, InetAddress.getLocalHost(), 32888));
      Thread.sleep(10);
      time += 10;
    }
  }
}
