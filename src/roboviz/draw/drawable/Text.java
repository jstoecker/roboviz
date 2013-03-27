/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker
 *  The MIT License. See LICENSE in project root.
 *******************************************************************************/
package roboviz.draw.drawable;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;

/**
 * 2D text with a 3D position.
 * 
 * @author justin
 */
public class Text extends Drawable {

  public final Vec3f  position;
  public final Vec3f  color;
  public final String text;

  public Text(Vec3f position, Vec3f color, String text, String setName) {
    super(setName);
    this.position = position;
    this.color = color;
    this.text = text;
  }
}
