/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.scene.spl;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Viewport;
import jgl.math.Maths;
import jgl.math.vector.Transform;
import jgl.scene.cameras.OrbitController;
import jgl.scene.lights.DirectionalLight;
import jgl.scene.lights.LightModel;
import roboviz.scene.Scene;

public class SPLScene extends Scene {

  Config          cfg           = new Config(new File("/Users/justin/Desktop/splscene.cfg"));
  OrbitController controller    = new OrbitController();
  FieldRenderer   fieldRenderer = new FieldRenderer(cfg);

  public SPLScene() {
    controller.setUpY(false);
    controller.setCamera(camera);
    controller.setRadius(8);
    controller.setAltitude(Maths.PI/4);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    controller.keyPressed(e);
  }
  
  @Override
  public void keyReleased(KeyEvent e) {
    controller.keyReleased(e);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    controller.mouseWheelMoved(e);
  }
  
  @Override
  public void init(GL2 gl) {
    gl.glDisable(GL.GL_DEPTH_TEST);
    fieldRenderer.init(gl);
  }

  @Override
  public void update(GL2 gl, double elapsedMS) {
  }

  @Override
  public void render(GL2 gl, Viewport viewport) {
    controller.update();
    camera.apply(gl);
    
    LightModel lm = new LightModel();
    lm.addLight(new DirectionalLight(0, 0, 1));
    
    DirectionalLight l2 = new DirectionalLight(0, .5f, 1);
    l2.setAmbient(0, 0, 0, 0);
    l2.setDiffuse(0.2f, 0.2f, 0.2f, 1);
    lm.addLight(l2);
    
    DirectionalLight l3 = new DirectionalLight(0, -.5f, 1);
    l3.setAmbient(0, 0, 0, 0);
    l3.setDiffuse(0.2f, 0.2f, 0.2f, 1);
    lm.addLight(l3);
    
    lm.setGlobalAmbient(0.5f, 0.5f, 0.5f, 1);
    lm.apply(gl);

    fieldRenderer.draw(gl);
  }

  @Override
  public void dispose(GL2 gl) {
    fieldRenderer.dispose(gl);
  }

  @Override
  public void resize(GL2 gl, Viewport viewport) {
    camera.setProjection(Transform.perspective(60, viewport.aspect(), 0.1f, 100));
  }


}
