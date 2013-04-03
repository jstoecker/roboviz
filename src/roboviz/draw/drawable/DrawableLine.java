/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw.drawable;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;

public class DrawableLine extends Drawable {

  public final Vec3f  start;
  public final Vec3f  end;
  public final float  thickness;
  public final Vec3f  color;

  public DrawableLine(Vec3f start, Vec3f end, float thickness, Vec3f color, String setName) {
    super(setName);
    this.start = start;
    this.end = end;
    this.thickness = thickness;
    this.color = color;
  }
  
  @Override
  public void render(GL2 gl) {
    gl.glLineWidth(thickness);
    gl.glColor3f(color.x, color.y, color.z);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3f(start.x, start.y, start.z);
    gl.glVertex3f(end.x, end.y, end.z);
    gl.glEnd();
  }
}
