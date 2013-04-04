/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.scene;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL2;

import jgl.cameras.Camera;
import jgl.core.Viewport;
import jgl.math.vector.Transform;

/**
 * Scene template that provides default implementations for the user input.
 * 
 * @author justin
 */
public abstract class Scene implements KeyListener, MouseListener, MouseMotionListener,
    MouseWheelListener {

  protected Camera camera = new Camera();

  public Scene() {
    camera.setView(Transform.lookAt(10, 0, 10, 0, 0, 0, 0, 0, 1));
    camera.setProjection(Transform.perspective(60, 1, 0.1f, 100));
  }

  public Camera getCamera() {
    return camera;
  }

  @Override
  public void keyPressed(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
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
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
  }

  public abstract void init(GL2 gl);

  public abstract void update(GL2 gl, double elapsedMS);

  public abstract void render(GL2 gl, Viewport viewport);

  public abstract void dispose(GL2 gl);

  public abstract void resize(GL2 gl, Viewport viewport);
}
