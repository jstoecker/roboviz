import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for creating drawing commands. This class takes care of converting commands to
 * binary format and ensuring commands are not split across two packets.
 * 
 * @author justin
 */
public class DrawBuffer {

  private final int             maxPacketSize;
  private int                   barrier;
  private int                   writtenBefore;
  private int                   written;
  private ByteArrayOutputStream binaryOut   = new ByteArrayOutputStream();
  private BufferedOutputStream  out         = new BufferedOutputStream(binaryOut);
  private List<Integer>         packetMarks = new ArrayList<Integer>();

  public DrawBuffer(int maxPacketSize) {
    this.maxPacketSize = maxPacketSize;
    barrier = maxPacketSize;
  }

  public List<DatagramPacket> getPackets(InetAddress address, int port) {
    if (written == 0)
      return Collections.emptyList();

    try {
      out.flush();
    } catch (IOException e) {
      return Collections.emptyList();
    }
    byte[] data = binaryOut.toByteArray();

    List<DatagramPacket> packets = new ArrayList<DatagramPacket>();
    int offset = 0;
    for (int i = 0; i < packetMarks.size(); i++) {
      int length = packetMarks.get(i) - offset;
      packets.add(new DatagramPacket(data, offset, length, address, port));
      offset = packetMarks.get(i);
    }
    int length = written - offset;
    packets.add(new DatagramPacket(data, offset, length, address, port));
    
    return packets;
  }

  public void reset() throws IOException {
    packetMarks.clear();
    out.flush();
    binaryOut.reset();
    barrier = maxPacketSize;
    writtenBefore = 0;
    written = 0;
  }

  private void preCommand() {
    writtenBefore = written;
  }

  private void postCommand() {
    if (written > barrier) {
      packetMarks.add(writtenBefore);
      barrier = writtenBefore + maxPacketSize;
    }
  }

  public void swap(String set) throws IOException {
    preCommand();
    writeByte(0);
    writeByte(0);
    writeString(set);
    postCommand();
  }

  public void circle(float x, float y, float radius, float thickness, Color color, String set)
      throws IOException {
    preCommand();
    writeByte(1);
    writeByte(0);
    writeFloat(x);
    writeFloat(y);
    writeFloat(radius);
    writeFloat(thickness);
    writeRGB(color);
    writeString(set);
    postCommand();
  }

  public void line(float x1, float y1, float z1, float x2, float y2, float z2, float width,
      Color color, String set) throws IOException {
    preCommand();
    writeByte(1);
    writeByte(1);
    writeFloat(x1);
    writeFloat(y1);
    writeFloat(z1);
    writeFloat(x2);
    writeFloat(y2);
    writeFloat(z2);
    writeFloat(width);
    writeRGB(color);
    writeString(set);
    postCommand();
  }

  public void point(float x, float y, float z, float size, Color color, String set)
      throws IOException {
    preCommand();
    writeByte(1);
    writeByte(2);
    writeFloat(x);
    writeFloat(y);
    writeFloat(z);
    writeFloat(size);
    writeRGB(color);
    writeString(set);
    postCommand();
  }

  public void sphere(float x, float y, float z, float radius, Color color, String set)
      throws IOException {
    preCommand();
    writeByte(1);
    writeByte(3);
    writeFloat(x);
    writeFloat(y);
    writeFloat(z);
    writeFloat(radius);
    writeRGB(color);
    writeString(set);
    postCommand();
  }

  private final void writeByte(int value) throws IOException {
    out.write(value);
    written++;
  }

  private final void writeFloat(float value) throws IOException {
    out.write(String.format("%6f", value).substring(0, 6).getBytes(), 0, 6);
    written += 6;
  }

  private final void writeRGB(Color color) throws IOException {
    out.write(color.getRed());
    out.write(color.getGreen());
    out.write(color.getBlue());
    written += 3;
  }

  private final void writeString(String s) throws IOException {
    out.write(s.getBytes(), 0, s.length());
    out.write(0);
    written += s.length() + 1;
  }

}
