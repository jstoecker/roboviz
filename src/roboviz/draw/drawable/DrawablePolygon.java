/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw.drawable;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import jgl.math.vector.Vec4f;

public class DrawablePolygon extends Drawable {

  public final Vec4f color;
  public final Vec3f[] vertices;
  
  public DrawablePolygon(Vec4f color, Vec3f[] vertices, String setName) {
    super(setName);
    this.color = color;
    this.vertices = vertices;
  }
  
  @Override
  public void render(GL2 gl) {
    gl.glColor3f(color.x, color.y, color.z);
    gl.glBegin(GL2.GL_POLYGON);
    for (Vec3f v : vertices)
      gl.glVertex3f(v.x, v.y, v.z);
    gl.glEnd();
  }
}
