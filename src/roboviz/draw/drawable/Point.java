/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker
 *  The MIT License. See LICENSE in project root.
 *******************************************************************************/
package roboviz.draw.drawable;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;

public class Point extends Drawable {

  public final Vec3f position;
  public final float size;
  public final Vec3f color;
  
  public Point(Vec3f position, float size, Vec3f color, String setName) {
    super(setName);
    this.position = position;
    this.size = size;
    this.color = color;
  }
  
  @Override
  public void render(GL2 gl) {
    gl.glPointSize(size);
    gl.glColor3f(color.x, color.y, color.z);
    gl.glBegin(GL2.GL_POINTS);
    gl.glVertex3f(position.x, position.y, position.z);
    gl.glEnd();
  }
}
