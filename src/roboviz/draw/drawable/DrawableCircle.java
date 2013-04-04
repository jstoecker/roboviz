/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw.drawable;

import javax.media.opengl.GL2;

import jgl.geometry.extra.CircleGeometry;
import jgl.math.vector.Vec2f;
import jgl.math.vector.Vec3f;
import roboviz.util.GeometryCache;

public class DrawableCircle extends Drawable implements GeometryCache.Generator {

  public static final GeometryCache<DrawableCircle> cache = new GeometryCache<DrawableCircle>(10);

  public final Vec2f                  position;
  public final float                  radius;
  public final float                  thickness;
  public final Vec3f                  color;

  public DrawableCircle(Vec2f position, float radius, float thickness, Vec3f color, String setName) {
    super(setName);
    this.position = position;
    this.radius = radius;
    this.thickness = thickness;
    this.color = color;
  }

  @Override
  public CircleGeometry newGeometry() {
    return CircleGeometry.posZ(radius - thickness, radius, 32);
  }

  @Override
  public void render(GL2 gl) {
    gl.glPushMatrix();
    gl.glTranslatef(position.x, position.y, 0);
    gl.glColor3f(color.x, color.y, color.z);
    cache.render(gl, this);
    gl.glPopMatrix();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(radius);
    result = prime * result + Float.floatToIntBits(thickness);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DrawableCircle other = (DrawableCircle) obj;
    if (Float.floatToIntBits(radius) != Float.floatToIntBits(other.radius))
      return false;
    if (Float.floatToIntBits(thickness) != Float.floatToIntBits(other.thickness))
      return false;
    return true;
  }
}
