/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw.drawable;

import jgl.math.vector.Vec3f;

/**
 * 2D text with a 3D position.
 * 
 * @author justin
 */
public class DrawableText extends Drawable {

  public final Vec3f  position;
  public final Vec3f  color;
  public final String text;

  public DrawableText(Vec3f position, Vec3f color, String text, String setName) {
    super(setName);
    this.position = position;
    this.color = color;
    this.text = text;
  }
}
