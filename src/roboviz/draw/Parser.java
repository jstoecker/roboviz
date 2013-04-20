/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw;

import java.nio.ByteBuffer;

import jgl.math.vector.Vec2f;
import jgl.math.vector.Vec3f;
import jgl.math.vector.Vec4f;
import roboviz.draw.drawable.Drawable;
import roboviz.draw.drawable.DrawableCircle;
import roboviz.draw.drawable.DrawableLine;
import roboviz.draw.drawable.DrawablePoint;
import roboviz.draw.drawable.DrawablePolygon;
import roboviz.draw.drawable.DrawableSphere;
import roboviz.draw.drawable.DrawableText;

/**
 * Parses binary input into commands, then executes them.
 * 
 * @author justin
 */
public class Parser {

  private static final int   CONTROL               = 0;
  private static final int   CONTROL_SWAP_BUFFERS  = 0;
  private static final int   CONTROL_SET_FRAME     = 1;

  private static final int   DRAW_SHAPE            = 1;
  private static final int   DRAW_SHAPE_CIRCLE     = 0;
  private static final int   DRAW_SHAPE_LINE       = 1;
  private static final int   DRAW_SHAPE_POINT      = 2;
  private static final int   DRAW_SHAPE_SPHERE     = 3;
  private static final int   DRAW_SHAPE_POLYGON    = 4;

  private static final int   DRAW_TEXT             = 2;
  private static final int   DRAW_TEXT_STATIC      = 0;
  private static final int   DRAW_TEXT_FLOAT       = 1;
  private static final int   DRAW_TEXT_FLOAT_CLEAR = 2;

  private static final int   SCENE_COMMAND         = 3;

  private DrawManager        manager;
  private SceneCommandParser sceneCmdParser;

  public Parser(DrawManager manager) {
    this.manager = manager;
  }

  public void parse(ByteBuffer buf) {
    int pos = 0;
    try {
      while (buf.hasRemaining()) {
        switch (getUnsignedByte(buf)) {
        case CONTROL:
          parseControl(buf);
          break;
        case DRAW_SHAPE:
          parseDrawShape(buf);
          break;
        case DRAW_TEXT:
          parseDrawText(buf);
          break;
        case SCENE_COMMAND:
          if (sceneCmdParser != null) {
            sceneCmdParser.parse(buf);
          }
          break;
        default:
          break;
        }
        pos = buf.position();
      }
    } catch (Exception e) {
      int end = buf.position();
      System.out.printf("Bad parse: [%d, %d] : %s\n", pos, end, e.getMessage());
    }
  }
  
  public void setSceneCmdParser(SceneCommandParser sceneCmdParser) {
    this.sceneCmdParser = sceneCmdParser;
  }

  private void parseControl(ByteBuffer buf) {
    switch (getUnsignedByte(buf)) {
    case CONTROL_SWAP_BUFFERS:
      parseSwapBuffers(buf);
      break;
    case CONTROL_SET_FRAME:
      parseSetFrame(buf);
      break;
    }
  }

  private void parseSwapBuffers(ByteBuffer buf) {
    String setName = getString(buf);
    manager.graph.swapBuffers(setName);
  }

  private void parseSetFrame(ByteBuffer buf) {
    Vec3f position = getXYZ(buf);
    String setName = getString(buf);
  }

  private void parseDrawShape(ByteBuffer buf) {
    Drawable drawable = null;
    switch (getUnsignedByte(buf)) {
    case DRAW_SHAPE_CIRCLE:
      drawable = parseCircle(buf);
      break;
    case DRAW_SHAPE_LINE:
      drawable = parseLine(buf);
      break;
    case DRAW_SHAPE_POINT:
      drawable = parsePoint(buf);
      break;
    case DRAW_SHAPE_SPHERE:
      drawable = parseSphere(buf);
      break;
    case DRAW_SHAPE_POLYGON:
      drawable = parsePolygon(buf);
      break;
    }

    if (drawable != null) {
      manager.graph.add(drawable);
    }
  }

  private DrawableCircle parseCircle(ByteBuffer buf) {
    Vec2f position = getXY(buf);
    float radius = getFloat(buf);
    float thickness = getFloat(buf);
    Vec3f color = getRGB(buf);
    String set = getString(buf);
    return new DrawableCircle(position, radius, thickness, color, set);
  }

  private DrawableLine parseLine(ByteBuffer buf) {
    Vec3f start = getXYZ(buf);
    Vec3f end = getXYZ(buf);
    float thickness = getFloat(buf);
    Vec3f color = getRGB(buf);
    String set = getString(buf);
    return new DrawableLine(start, end, thickness, color, set);
  }

  private DrawablePoint parsePoint(ByteBuffer buf) {
    Vec3f position = getXYZ(buf);
    float size = getFloat(buf);
    Vec3f color = getRGB(buf);
    String set = getString(buf);
    return new DrawablePoint(position, size, color, set);
  }

  private DrawableSphere parseSphere(ByteBuffer buf) {
    Vec3f position = getXYZ(buf);
    float radius = getFloat(buf);
    Vec3f color = getRGB(buf);
    String set = getString(buf);
    return new DrawableSphere(position, radius, color, set);
  }

  private DrawablePolygon parsePolygon(ByteBuffer buf) {
    int numVerts = getUnsignedByte(buf);
    Vec3f[] vertices = new Vec3f[numVerts];
    Vec4f color = getRGBA(buf);
    for (int i = 0; i < numVerts; i++)
      vertices[i] = getXYZ(buf);
    String set = getString(buf);
    return new DrawablePolygon(color, vertices, set);
  }

  private void parseDrawText(ByteBuffer buf) {
    switch (getUnsignedByte(buf)) {
    case DRAW_TEXT_STATIC:
      parseStaticText(buf);
      break;
    case DRAW_TEXT_FLOAT:
      parseFloatText(buf);
      break;
    case DRAW_TEXT_FLOAT_CLEAR:
      parseFloatTextClear(buf);
      break;
    }
  }

  private void parseStaticText(ByteBuffer buf) {
    Vec3f position = getXYZ(buf);
    Vec3f color = getRGB(buf);
    String text = getString(buf);
    String set = getString(buf);
    manager.graph.add(new DrawableText(position, color, text, set));
  }

  private void parseFloatText(ByteBuffer buf) {
    int id = getUnsignedByte(buf);
    Vec3f color = getRGB(buf);
    String text = getString(buf);
    // generate unique string for set name based on agent/team id
  }

  private void parseFloatTextClear(ByteBuffer buf) {
    int id = getUnsignedByte(buf);
    // remove from model
  }

  protected static int getUnsignedByte(ByteBuffer buf) {
    return buf.get() & 0xff;
  }

  protected static String getString(ByteBuffer buf) {
    StringBuilder sb = new StringBuilder();
    char c;
    while ((c = (char) getUnsignedByte(buf)) != 0)
      sb.append(c);
    return sb.toString();
  }

  public static float getFloat(ByteBuffer buf) {
    byte[] chars = new byte[6];
    buf.get(chars);
    return Float.parseFloat(new String(chars));
  }

  private static Vec2f getXY(ByteBuffer buf) {
    return new Vec2f(getFloat(buf), getFloat(buf));
  }

  private static Vec3f getXYZ(ByteBuffer buf) {
    return new Vec3f(getFloat(buf), getFloat(buf), getFloat(buf));
  }

  private static Vec3f getRGB(ByteBuffer buf) {
    return new Vec3f(getUnsignedByte(buf) / 255f, getUnsignedByte(buf) / 255f,
        getUnsignedByte(buf) / 255f);
  }

  private static Vec4f getRGBA(ByteBuffer buf) {
    return new Vec4f(getUnsignedByte(buf) / 255f, getUnsignedByte(buf) / 255f,
        getUnsignedByte(buf) / 255f, getUnsignedByte(buf) / 255f);
  }
}
