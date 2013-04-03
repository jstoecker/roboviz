/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/

package roboviz.draw;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Receives binary messages from clients and hands them off to the command parser.
 * 
 * @author justin
 */
class Connection {
  private static final int BUFFER_SIZE = 8192;
  byte[]                   buf         = new byte[BUFFER_SIZE];

  DrawManager              manager;
  ReceiveThread            receiver    = new ReceiveThread();
  DatagramSocket           socket;

  public Connection(DrawManager manager, int port) throws SocketException, UnknownHostException {
    this.manager = manager;
    socket = new DatagramSocket(port);
    receiver.start();
  }

  public void shutdown() {
    receiver.running = false;
    receiver.interrupt();
    if (socket != null)
      socket.close();
  }

  private class ReceiveThread extends Thread {
    boolean running = true;

    @Override
    public void run() {
      while (running) {
        try {
          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          socket.receive(packet);

          ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
          manager.parser.parse(byteBuffer);

        } catch (IOException e) {
        }
      }
      if (socket != null)
        socket.close();
    }
  }
}