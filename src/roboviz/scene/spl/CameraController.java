package roboviz.scene.spl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import jgl.cameras.Camera;
import jgl.math.vector.Vec3f;

public class CameraController implements KeyListener, MouseWheelListener {

  float  horizontal = 0;
  float  forward    = 0;
  float  vertical   = 0;
  float  yaw        = 0;

  Vec3f  up;
  Vec3f  target     = new Vec3f(0);
  Camera camera;

  /** If false, Z is up; if true, Y is up. */
  public void setUpY(boolean yUp) {
    up = (yUp ? Vec3f.axisY() : Vec3f.axisZ());
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void update(float elapsedMS) {
    if (camera == null)
      return;

    boolean changed = horizontal != 0;
    changed = changed || forward != 0;
    changed = changed || vertical != 0;
    changed = changed || yaw != 0;

    if (changed)
      updateView(elapsedMS);
  }

  private void updateView(float elapsedMS) {
    float scale = elapsedMS / 256f;
    Vec3f f = camera.getForward().copy();
    f.z = 0;
    f.normalize();
    camera.translate(f.times(scale * forward));
    camera.translateRight(horizontal * scale);
    camera.translate(0, 0, vertical * scale);
    camera.rotateZ(yaw);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.isShiftDown()) {
      if (e.getKeyCode() == KeyEvent.VK_W) {
        vertical = -1;
      } else if (e.getKeyCode() == KeyEvent.VK_S) {
        vertical = 1;
      } else if (e.getKeyCode() == KeyEvent.VK_D) {
        yaw = -0.01f;
      } else if (e.getKeyCode() == KeyEvent.VK_A) {
        yaw = 0.01f;
      }
    } else {
      if (e.getKeyCode() == KeyEvent.VK_A) {
        horizontal = 1;
      } else if (e.getKeyCode() == KeyEvent.VK_D) {
        horizontal = -1;
      } else if (e.getKeyCode() == KeyEvent.VK_W) {
        forward = -1;
      } else if (e.getKeyCode() == KeyEvent.VK_S) {
        forward = 1;
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.isShiftDown()) {
      if (e.getKeyCode() == KeyEvent.VK_W && vertical != 0) {
        vertical = 0;
      } else if (e.getKeyCode() == KeyEvent.VK_S && vertical != 0) {
        vertical = 0;
      } else if (e.getKeyCode() == KeyEvent.VK_D && yaw != 0) {
        yaw = 0;
      } else if (e.getKeyCode() == KeyEvent.VK_A && yaw != 0) {
        yaw = 0;
      }
    } else {
      if (e.getKeyCode() == KeyEvent.VK_A && horizontal != 0) {
        horizontal = 0;
      } else if (e.getKeyCode() == KeyEvent.VK_D && horizontal != 0) {
        horizontal = 0;
      } else if (e.getKeyCode() == KeyEvent.VK_W && forward != 0) {
        forward = 0;
      } else if (e.getKeyCode() == KeyEvent.VK_S && forward != 0) {
        forward = 0;
      }
    }

  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
  }
}
