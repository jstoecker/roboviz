/*
 *  Copyright 2011 RoboViz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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