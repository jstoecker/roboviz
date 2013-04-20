package roboviz.scene.sim3d;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;

import jgl.math.vector.ConstVec3f;
import roboviz.scene.sim3d.state.GameState;
import roboviz.scene.sim3d.state.SExpression;
import roboviz.scene.sim3d.state.SceneGraph;

/**
 * Used to establish a network connection with rcssserver3d.
 * 
 * @see http://simspark.sourceforge.net/wiki/index.php/Network_Protocol
 * @author justin
 */
public class Monitor {

  private GameState         gameState   = new GameState();
  private SceneGraph        sceneGraph  = new SceneGraph();
  private AutoConnectThread autoConnectThread;
  private MessageReceiver   inThread;
  private Socket            socket;
  private PrintWriter       out         = null;
  private DataInputStream   in;
  private boolean           connected   = false;
  private String            serverHost;
  private int               serverPort;
  private boolean           autoConnect = false;
  private int               autoConnectDelay;

  public Monitor(String host, int port, boolean autoConnect, int delay) {
    serverHost = host;
    serverPort = port;
    this.autoConnectDelay = delay;
    setAutoConnect(autoConnect);
  }

  private void setConnected(boolean connected) {
    this.connected = connected;
    if (connected)
      killAutoConnectThread();
    else if (autoConnect)
      launchAutoConnectThread();
  }

  public boolean isConnected() {
    return connected;
  }

  public boolean isAutoConnect() {
    return autoConnect;
  }

  public void connect() {
    connect(serverHost, serverPort);
  }

  public void connect(String host, int port) {
    try {
      socket = new Socket(host, port);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new DataInputStream(socket.getInputStream());
      inThread = new MessageReceiver();
      inThread.start();
      if (autoConnectThread != null)
        autoConnectThread.running = false;
      setConnected(true);
    } catch (IOException e) {
    }
  }

  public void disconnect() {
    setConnected(false);
    if (socket != null) {

      try {
        socket.close();
      } catch (IOException e) {
        System.err.println("Error: closing input stream with server" + e.getMessage());
      }

      try {
        in.close();
      } catch (IOException e) {
        System.err.println("Error: closing input stream with server" + e.getMessage());
        e.printStackTrace();
      }
      out.close();

      socket = null;
    }
  }

  private char[] intToBytes(int i) {
    char[] buf = new char[4];
    buf[0] = (char) ((i >> 24) & 255);
    buf[1] = (char) ((i >> 16) & 255);
    buf[2] = (char) ((i >> 8) & 255);
    buf[3] = (char) (i & 255);
    return buf;
  }

  public void sendMessage(String msg) {
    if (out == null) {
      System.out.printf("Cannot send message (not connected): \"%s\"\n", msg);
      return;
    }

    char[] buf = new char[4 + msg.length()];
    char[] msgSize = intToBytes(msg.length());
    System.arraycopy(msgSize, 0, buf, 0, 4);
    for (int i = 0; i < msg.length(); i++)
      buf[i + 4] = msg.charAt(i);

    out.write(buf);
    out.flush();
  }

  public void sendInit() {
    sendMessage("(init)");
  }

  public void kickOff(boolean left) {
    sendMessage(left ? "(kickOff Left)" : "(kickOff Right)");
  }

  public void dropBall() {
    sendMessage("(dropBall)");
  }

  public void setPlayMode(String mode) {
    sendMessage(String.format("(playMode %s)", mode));
  }

  public void freeKick(boolean left) {
    setPlayMode(left ? "free_kick_left" : "free_kick_right");
  }

  public void killServer() {
    sendMessage("(killsim)");
  }

  public String getHost() {
    return serverHost;
  }

  public int getPort() {
    return serverPort;
  }

  public SceneGraph getSceneGraph() {
    return sceneGraph;
  }
  
  public GameState getGameState() {
    return gameState;
  }

  public void setAutoConnectDelay(int delay) {
    autoConnectDelay = delay;
  }

  public void setAutoConnect(boolean autoConnect) {
    if (this.autoConnect == autoConnect)
      return;
    this.autoConnect = autoConnect;

    if (autoConnect) {
      launchAutoConnectThread();
    } else {
      killAutoConnectThread();
    }
  }

  private void killAutoConnectThread() {
    if (autoConnectThread != null) {
      autoConnectThread.running = false;
      autoConnectThread.interrupt();
      autoConnectThread = null;
    }
  }

  private void launchAutoConnectThread() {
    autoConnectThread = new AutoConnectThread();
    autoConnectThread.start();
  }

  public void close() {
    setAutoConnect(false);
    disconnect();
  }

  public void moveBall(ConstVec3f position, ConstVec3f velocity) {
    if (velocity == null) {
      sendMessage(String.format("(ball (pos %.2f %.2f %.2f))", position.x(), position.y(),
          position.z()));
    } else {
      sendMessage(String.format("(ball (pos %.2f %.2f %.2f) (vel %.2f, %.2f %.2f))", position.x(),
          position.y(), position.z(), velocity.x(), velocity.y(), velocity.z()));
    }
  }

  public void moveAgent(int id, boolean left, ConstVec3f position) {
    String team = left ? "Left" : "Right";
    String m = String.format("(agent (team %s)(unum %d)(pos %.2f %.2f %.2f))", team, id,
        position.x(), position.y(), position.z());
    sendMessage(m);
  }

  public void killAgent(int id, boolean left) {
    sendMessage(String.format("(kill (unum %d) (team %s))", id, (left ? "Left" : "Right")));
  }

  private class AutoConnectThread extends Thread {
    boolean running = true;

    @Override
    public void run() {
      while (running) {
        if (socket == null)
          connect(serverHost, serverPort);
        try {
          Thread.sleep(autoConnectDelay);
        } catch (InterruptedException e) {
          running = false;
        }
      }
    }
  }

  private class MessageReceiver extends Thread {

    public void run() {
      try {
        String message = null;
        do {
          message = readMessage();
          if (message != null) {
            try {
              ArrayList<SExpression> expressions = SExpression.parse(message);
              gameState.update(expressions.get(0));
              sceneGraph.update(expressions.get(1), expressions.get(2));
            } catch (ParseException e) {
              e.printStackTrace();
            }
          }
        } while (message != null);

        disconnect();
      } catch (IOException e) {
        disconnect();
      }
    }

    private String readMessage() throws IOException {
      // message is prefixed by its size in bytes
      int length = in.readInt();
      if (length <= 0)
        return null;

      // read from stream until all bytes in message are read
      byte[] buf = new byte[length];
      int bytesRead = 0;
      while (bytesRead < length)
        bytesRead += in.read(buf, bytesRead, length - bytesRead);

      return new String(buf);
    }
  }
}