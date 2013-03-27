/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker
 *  The MIT License. See LICENSE in project root.
 *******************************************************************************/
package roboviz.scene.basic;

import javax.media.opengl.GL2;

import jgl.core.Viewport;
import jgl.math.vector.Transform;
import jgl.scene.geometry.GridGeometry;
import roboviz.scene.Scene;

/**
 * Default scene that loads on startup.
 * 
 * @author justin
 */
public class BasicScene extends Scene {

  GridGeometry grid = GridGeometry.aroundZ(10, 1);

  public BasicScene() {
  }

  @Override
  public void init(GL2 gl) {
  }

  @Override
  public void update(GL2 gl, double elapsedMS) {
  }

  @Override
  public void render(GL2 gl, Viewport viewport) {
    camera.apply(gl);
    gl.glColor3f(0.3f, 0.3f, 0.3f);
    grid.drawImmediate(gl);
  }

  @Override
  public void dispose(GL2 gl) {
  }

  @Override
  public void resize(GL2 gl, Viewport viewport) {
    camera.setProjection(Transform.perspective(60, viewport.aspect(), 0.1f, 1000.0f));
  }
}
