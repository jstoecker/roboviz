package roboviz.scene.spl;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import jgl.cameras.Camera;
import jgl.math.Maths;
import jgl.math.vector.ConstVec3f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec3f;

public class CameraController implements KeyListener, MouseWheelListener, MouseMotionListener,
    MouseListener {

  private static final float PITCH_EPSILON  = 0.000001f;
  private static final float MAX_PITCH      = Maths.PI / 2 - PITCH_EPSILON;
  private static final float MIN_PITCH      = -MAX_PITCH;

  private Camera             camera;

  private Point              anchorPoint    = null;
  private float              anchorAltitude = 0;
  private float              anchorAzimuth  = 0;

  private float              horizontal     = 0;
  private float              forward        = 0;
  private float              vertical       = 0;
  private float              azimuth        = 0;
  private float              altitude       = 0;

  public void setCamera(Camera camera) {
    this.camera = camera;

    // calculate current angles
    ConstVec3f fwd = camera.getForward();
    Vec3f spherical = Maths.cartesianToSpherical(fwd.x(), fwd.y(), fwd.z(), false);
    this.altitude = spherical.y;
    this.azimuth = spherical.z;
    System.out.println(altitude + " , " + azimuth);
  }

  public void update(float elapsedMS) {
    if (camera == null)
      return;

    boolean changed = horizontal != 0;
    changed = changed || forward != 0;
    changed = changed || vertical != 0;

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
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.isShiftDown()) {
      if (e.getKeyCode() == KeyEvent.VK_W) {
        vertical = -1;
      } else if (e.getKeyCode() == KeyEvent.VK_S) {
        vertical = 1;
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

  public void setRotation(float altitude, float azimuth) {
    if (camera == null)
      return;

    this.altitude = Maths.clamp(altitude, MIN_PITCH, MAX_PITCH);
    this.azimuth = azimuth;

    Vec3f forward = Maths.sphericalToCartesian(1, this.altitude, this.azimuth, false);
    Vec3f right = forward.cross(Vec3f.axisZ()).normalize();
    Vec3f up = right.cross(forward).normalize();
    ConstVec3f eye = camera.getEye();
    Vec3f ctr = camera.getEye().plus(forward);

    camera.setView(Transform.lookAt(eye.x(), eye.y(), eye.z(), ctr.x, ctr.y, ctr.z, up.x, up.y, up.z));
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    float scale = -0.005f;
    float deltaAzi = (e.getX() - anchorPoint.x) * scale;
    float deltaAlt = (e.getY() - anchorPoint.y) * scale;
    setRotation(anchorAltitude + deltaAlt, anchorAzimuth + deltaAzi);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    anchorPoint = e.getPoint();
    anchorAltitude = altitude;
    anchorAzimuth = azimuth;
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
  }
}
