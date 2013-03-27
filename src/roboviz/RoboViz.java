/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker
 *  The MIT License. See LICENSE in project root.
 *******************************************************************************/
package roboviz;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import jgl.core.Viewport;
import roboviz.draw.DrawManager;
import roboviz.scene.Scene;
import roboviz.scene.basic.BasicScene;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Main application.
 * 
 * @author justin
 */
public class RoboViz implements GLEventListener {

  Scene        scene       = new BasicScene();
  DrawManager  drawManager = new DrawManager();
  Viewport     viewport    = new Viewport(0, 0, 1, 1);
  JFrame       mainFrame;
  JDialog      drawDialog;
  AnimatorBase animator;
  GLCanvas     canvas;

  public RoboViz(GLCanvas canvas) {
    this.canvas = canvas;
    canvas.addGLEventListener(this);
    animator = new FPSAnimator(canvas, 60);

    initGUI();

    animator.start();
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    scene.update(gl, 16);
    scene.render(gl, viewport);
    drawManager.render(gl, scene.getCamera(), viewport);
  }

  private void initGUI() {
    mainFrame = new JFrame("RoboViz");
    mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    mainFrame.setSize(800, 600);
    mainFrame.setLocationRelativeTo(null);
    mainFrame.add(canvas);

    mainFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        dispose(canvas);
        mainFrame.dispose();
        System.exit(0);
      }
    });

    drawDialog = new JDialog(mainFrame, "Drawings");
    drawDialog.add(drawManager.getDrawPanel());
    drawDialog.pack();
    drawDialog.setLocationByPlatform(true);
    drawDialog.setVisible(true);

    MenuBar menuBar = new MenuBar();
    Menu sceneMenu = new Menu("Scene");
    sceneMenu.add(new MenuItem("None"));
    sceneMenu.addSeparator();
    sceneMenu.add(new MenuItem("RoboCup Simulation 3D"));
    sceneMenu.add(new MenuItem("RoboCup Simulation 3D Log"));
    sceneMenu.add(new MenuItem("RoboCup Standard Platform"));
    menuBar.add(sceneMenu);
    
    Menu viewMenu = new Menu("View");
    MenuItem viewDrawingsItem = new MenuItem("Drawings", new MenuShortcut(KeyEvent.VK_D));
    viewDrawingsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        drawDialog.setVisible(true);
      }
    });
    viewMenu.add(viewDrawingsItem);
    menuBar.add(viewMenu);

    mainFrame.setMenuBar(menuBar);

    mainFrame.setVisible(true);
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    scene.dispose(gl);
    drawManager.dispose(gl);
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClearColor(0, 0, 0, 0);

    drawManager.init();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL2 gl = drawable.getGL().getGL2();
    viewport.width = width;
    viewport.height = height;
    scene.resize(gl, viewport);
  }

  public static void main(String[] args) {
    // OpenGL version 2.1
    GLProfile glp = GLProfile.get(GLProfile.GL2);
    GLCapabilities glc = new GLCapabilities(glp);
    final GLCanvas canvas = new GLCanvas(glc);

    new RoboViz(canvas);
  }
}
