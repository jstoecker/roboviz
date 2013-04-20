package roboviz.scene.sim3d;

import javax.media.opengl.GL2;

import jgl.core.Viewport;
import roboviz.scene.Scene;

public class Sim3dScene extends Scene {

  Monitor monitor;
  
  public Sim3dScene() {
    monitor = new Monitor("10.211.55.3", 3200, true, 1000);
  }
  
  @Override
  public void init(GL2 gl) {
  }

  @Override
  public void update(GL2 gl, float elapsedMS) {
  }

  @Override
  public void render(GL2 gl, Viewport viewport) {
  }

  @Override
  public void dispose(GL2 gl) {
    monitor.setAutoConnect(false);
    monitor.disconnect();
  }

  @Override
  public void resize(GL2 gl, Viewport viewport) {
  }
}
