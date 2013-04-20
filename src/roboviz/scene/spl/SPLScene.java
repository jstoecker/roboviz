/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.scene.spl;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.cameras.OrbitController;
import jgl.core.Viewport;
import jgl.math.Maths;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec3f;
import jgl.shading.DirectionalLight;
import jgl.shading.LightModel;
import roboviz.robot.RobotModel;
import roboviz.robot.RobotRenderer;
import roboviz.scene.Scene;

public class SPLScene extends Scene {

  Config           cfg           = new Config(new File("/Users/justin/Desktop/splscene.cfg"));
  CameraController controller    = new CameraController();
  FieldRenderer    fieldRenderer = new FieldRenderer(cfg);
  RobotRenderer    robotRenderer;
  RobotModel[]     robots        = new RobotModel[5];
  RobotController  robotController;

  public SPLScene() {
    controller.setUpY(false);
    controller.setCamera(camera);

    for (int i = 0; i < robots.length; i++) {
      robots[i] = RobotModel.loadFromYAML(new File("resources/spl/nao_v4/model.yml"));
      robots[i].getRoot().setOffset(new Vec3f(-i, 3.5f, 0));
    }
    robotRenderer = new RobotRenderer("resources/spl/nao_v4/");
    robotController = new RobotController(robots);
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
  public void update(GL2 gl, float elapsedMS) {
    controller.update(elapsedMS);
  }

  @Override
  public void render(GL2 gl, Viewport viewport) {
    camera.apply(gl);

    LightModel lm = new LightModel();
    lm.addLight(new DirectionalLight(0, 0, 1));

    lm.setGlobalAmbient(0.3f, 0.3f, 0.3f, 1);
    lm.apply(gl);

    fieldRenderer.draw(gl);
    // only drawing 1 for now; need to use VBOs, rendering slow right now
    for (int i = 0; i < 1/* robots.length */; i++) {
      robotRenderer.draw(gl, robots[i]);
    }
  }

  @Override
  public void dispose(GL2 gl) {
    fieldRenderer.dispose(gl);
    robotRenderer.dispose(gl);
  }

  @Override
  public void resize(GL2 gl, Viewport viewport) {
    camera.setProjection(Transform.perspective(60, viewport.aspect(), 0.1f, 100));
  }
}
