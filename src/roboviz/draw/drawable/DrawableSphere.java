/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw.drawable;

import javax.media.opengl.GL2;

import jgl.geometry.extra.SphereGeometry;
import jgl.math.vector.Vec3f;
import roboviz.util.GeometryCache;

public class DrawableSphere extends Drawable implements GeometryCache.Generator {

  public static final GeometryCache<DrawableSphere> cache = new GeometryCache<DrawableSphere>(10);

  public final Vec3f                         position;
  public final float                         radius;
  public final Vec3f                         color;

  public DrawableSphere(Vec3f position, float radius, Vec3f color, String setName) {
    super(setName);
    this.position = position;
    this.radius = radius;
    this.color = color;
  }

  @Override
  public SphereGeometry newGeometry() {
    return new SphereGeometry(radius, 8);
  }
  
  @Override
  public void render(GL2 gl) {
    gl.glEnable(GL2.GL_LIGHTING);
    
    gl.glPushMatrix();
    gl.glTranslatef(position.x, position.y, 0);
    gl.glColor3f(color.x, color.y, color.z);
    cache.render(gl, this);
    gl.glPopMatrix();
  }
}