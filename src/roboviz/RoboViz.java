/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
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
import roboviz.scene.sim3d.Sim3dScene;
import roboviz.scene.spl.SPLScene;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Main application.
 * 
 * @author justin
 */
public class RoboViz implements GLEventListener {

  Scene        scene;
  Scene        newScene;
  DrawManager  drawManager = new DrawManager();
  Viewport     viewport    = new Viewport(0, 0, 1, 1);
  JFrame       mainFrame;
  JDialog      drawDialog;
  AnimatorBase animator;
  GLCanvas     canvas;

  long         lastTime;

  public RoboViz(GLCanvas canvas) {
    this.canvas = canvas;
    canvas.addGLEventListener(this);
    animator = new FPSAnimator(canvas, 60);

    initGUI();
    
    setScene(new SPLScene());

    animator.start();
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();

    if (newScene != null) {
      if (scene != null)
        scene.dispose(gl);
      scene = newScene;
      scene.init(gl);
      scene.resize(gl, viewport);
      newScene = null;
    }
    
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    if (newScene == null && scene == null)
      return;

    long now = System.currentTimeMillis();
    scene.update(gl, now - lastTime);
    scene.render(gl, viewport);
    drawManager.render(gl, scene.getCamera(), viewport);
    lastTime = now;
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

    MenuBar menuBar = new MenuBar();
    mainFrame.setMenuBar(menuBar);

    Menu sceneMenu = new Menu("Scene");
    menuBar.add(sceneMenu);
    sceneMenu.add(new MenuItem("None"));
    sceneMenu.addSeparator();

    MenuItem rcs3dItem = new MenuItem("RoboCup Simulation 3D");
    rcs3dItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setScene(new Sim3dScene());
      }
    });
    sceneMenu.add(rcs3dItem);

    MenuItem rcs3dlogItem = new MenuItem("RoboCup Simulation 3D Log");
    rcs3dlogItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setScene(new BasicScene());
      }
    });
    sceneMenu.add(rcs3dlogItem);

    MenuItem rcsplItem = new MenuItem("RoboCup Standard Platform");
    rcsplItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setScene(new SPLScene());
      }
    });
    sceneMenu.add(rcsplItem);

    Menu viewMenu = new Menu("View");
    MenuItem viewDrawingsItem = new MenuItem("Drawings", new MenuShortcut(KeyEvent.VK_D));
    viewDrawingsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        drawDialog.setVisible(true);
      }
    });
    viewMenu.add(viewDrawingsItem);
    menuBar.add(viewMenu);

    mainFrame.setVisible(true);
  }

  private void setScene(Scene scene) {
    if (this.scene != null) {
      canvas.removeKeyListener(this.scene);
      canvas.removeMouseListener(this.scene);
      canvas.removeMouseMotionListener(this.scene);
      canvas.removeMouseWheelListener(this.scene);
    }
    
    if (this.newScene == null) {
      this.newScene = scene;
      canvas.addKeyListener(scene);
      canvas.addMouseListener(scene);
      canvas.addMouseMotionListener(scene);
      canvas.addMouseWheelListener(scene);
    }
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    if (scene != null)
      scene.dispose(gl);
    drawManager.dispose(gl);
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    drawManager.init();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL2 gl = drawable.getGL().getGL2();
    viewport.width = width;
    viewport.height = height;

    if (scene != null)
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
