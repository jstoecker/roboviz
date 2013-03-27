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
  float        t    = 0;

  public BasicScene() {
  }

  @Override
  public void init(GL2 gl) {
  }

  @Override
  public void update(GL2 gl, double elapsedMS) {
    t += 0.001f;
    camera.setView(Transform.lookAt((float) Math.cos(t) * 5, (float) Math.sin(t) * 5, 5, 0, 0, 0,
        0, 0, 1));
  }

  @Override
  public void render(GL2 gl, Viewport viewport) {
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadMatrixf(camera.getProjection().a, 0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadMatrixf(camera.getView().a, 0);
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
