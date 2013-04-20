package roboviz.draw;

import java.nio.ByteBuffer;

/**
 * A class that handles parsing of non-standard drawing commands sent to RoboViz.
 * 
 * @author justin
 */
public interface SceneCommandParser {

  void parse(ByteBuffer buf);
}
