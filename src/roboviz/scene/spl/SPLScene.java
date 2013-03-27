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
import jgl.scene.geometry.CircleGeometry;
import jgl.scene.geometry.CylinderGeometry;
import jgl.scene.geometry.Geometry;
import jgl.scene.geometry.PlaneGeometry;
import jgl.scene.lights.DirectionalLight;
import jgl.scene.lights.LightModel;
import jgl.scene.materials.Material;
import roboviz.scene.Scene;

public class SPLScene extends Scene {

  Config cfg = new Config(new File("/Users/justin/Desktop/splscene.cfg"));

  OrbitController controller = new OrbitController();
  int displayList = -1;
  
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
    
    displayList = gl.glGenLists(1);
    gl.glNewList(displayList, GL2.GL_COMPILE);
    {
      drawStaticGeometry(gl);
    }
    gl.glEndList();
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

    gl.glCallList(displayList);
  }

  @Override
  public void dispose(GL2 gl) {
//    cfg.save();
    gl.glDeleteLists(displayList, 1);
  }

  @Override
  public void resize(GL2 gl, Viewport viewport) {
    camera.setProjection(Transform.perspective(60, viewport.aspect(), 0.1f, 100));
  }
  
  private void drawStaticGeometry(GL2 gl) {
    
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glDisable(GL.GL_DEPTH_TEST);

    // green field
    Material m = new Material();
    m.setDiffAmbient(0.1f, 0.35f, 0.1f, 1);
    m.apply(gl);
    planeGeom(gl, 0, 0, cfg.fieldLength + 2 * cfg.borderStripWidth, cfg.fieldWidth + 2 * cfg.borderStripWidth);

    
    gl.glDisable(GL2.GL_LIGHTING);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glColor4f(0, 0, 0, 0.1f);
    for (float i = -cfg.fieldLength/2; i < cfg.fieldLength/2; i += 1) {
      planeGeom(gl, i + .25f, 0, 0.5f, cfg.fieldWidth);
    }
    gl.glDisable(GL.GL_BLEND);
    gl.glEnable(GL2.GL_LIGHTING);

    // white field lines
    m = new Material();
    m.setDiffAmbient(1, 1, 1, 1);
    m.apply(gl);
    planeGeom(gl, 0, 0, cfg.lineWidth, cfg.fieldWidth);
    planeGeom(gl, -cfg.fieldLength / 2, 0, cfg.lineWidth, cfg.fieldWidth + cfg.lineWidth);
    planeGeom(gl, cfg.fieldLength / 2, 0, cfg.lineWidth, cfg.fieldWidth  + cfg.lineWidth);
    planeGeom(gl, 0, -cfg.fieldWidth / 2, cfg.fieldLength, cfg.lineWidth);
    planeGeom(gl, 0, cfg.fieldWidth / 2, cfg.fieldLength, cfg.lineWidth);
    planeGeom(gl, -cfg.fieldLength / 2 + cfg.penaltyAreaLength, 0, cfg.lineWidth, cfg.penaltyAreaWidth + cfg.lineWidth);
    planeGeom(gl, cfg.fieldLength / 2 - cfg.penaltyAreaLength, 0, cfg.lineWidth, cfg.penaltyAreaWidth + cfg.lineWidth);
    planeGeom(gl, -cfg.fieldLength/2 + cfg.penaltyMarkDistance, 0, cfg.lineWidth, cfg.penaltyMarkSize);
    planeGeom(gl, -cfg.fieldLength/2 + cfg.penaltyMarkDistance, 0, cfg.penaltyMarkSize, cfg.lineWidth);
    planeGeom(gl, cfg.fieldLength/2 - cfg.penaltyMarkDistance, 0, cfg.lineWidth, cfg.penaltyMarkSize);
    planeGeom(gl, cfg.fieldLength/2 - cfg.penaltyMarkDistance, 0, cfg.penaltyMarkSize, cfg.lineWidth);
    planeGeom(gl, -cfg.fieldLength/2+cfg.penaltyAreaLength/2, cfg.penaltyAreaWidth/2, cfg.penaltyAreaLength, cfg.lineWidth);
    planeGeom(gl, -cfg.fieldLength/2+cfg.penaltyAreaLength/2, -cfg.penaltyAreaWidth/2, cfg.penaltyAreaLength, cfg.lineWidth);
    planeGeom(gl, cfg.fieldLength/2-cfg.penaltyAreaLength/2, cfg.penaltyAreaWidth/2, cfg.penaltyAreaLength, cfg.lineWidth);
    planeGeom(gl, cfg.fieldLength/2-cfg.penaltyAreaLength/2, -cfg.penaltyAreaWidth/2, cfg.penaltyAreaLength, cfg.lineWidth);
    planeGeom(gl, 0, 0, cfg.penaltyMarkSize, cfg.lineWidth);

    // center circle
    float innerRadius = cfg.centerCircleDiameter / 2 - cfg.lineWidth / 2;
    CircleGeometry.aroundZ(innerRadius, innerRadius + cfg.lineWidth, 64).drawImmediate(gl);
    
    gl.glEnable(GL.GL_DEPTH_TEST);
    
    // goal left
    drawGoal(gl);
    
    // goal right
    gl.glPushMatrix();
    gl.glRotated(180, 0, 0, 1);
    drawGoal(gl);
    gl.glPopMatrix();
  }
  
  private void drawGoal(GL2 gl) {
    float goalBarRadius = cfg.goalBarDiameter/2;

    Material m = new Material();
    m.setDiffAmbient(0.8f, 0.8f, 0.2f, 1);
    m.apply(gl);
    
    gl.glPushMatrix();
    gl.glTranslatef(cfg.fieldLength/2 + goalBarRadius/2, 0, 0);
    
    Geometry post = CylinderGeometry.aroundZ(goalBarRadius, cfg.goalHeight + goalBarRadius, false, 16);
    Geometry post2 = CylinderGeometry.aroundY(goalBarRadius, cfg.goalWidth + cfg.goalBarDiameter * 2, true, 16);
    drawGeom(gl, post, 0, cfg.goalWidth/2 + goalBarRadius, (cfg.goalHeight + goalBarRadius) / 2, 1, 1, 1);
    drawGeom(gl, post, 0, -(cfg.goalWidth/2 + goalBarRadius), (cfg.goalHeight + goalBarRadius) / 2, 1, 1, 1);
    drawGeom(gl, post2, 0, 0, cfg.goalHeight + goalBarRadius, 1, 1, 1);
    
    m = new Material();
    m.setDiffAmbient(0.8f, 0.8f, 0.8f, 1);
    m.apply(gl);
    
    Geometry post3 = CylinderGeometry.aroundZ(goalBarRadius/3, cfg.goalHeight, true, 8);
    drawGeom(gl, post3, cfg.goalLength, goalBarRadius+cfg.goalWidth/2, cfg.goalHeight/2, 1, 1, 1);
    drawGeom(gl, post3, cfg.goalLength, -goalBarRadius-cfg.goalWidth/2, cfg.goalHeight/2, 1, 1, 1);
    
    Geometry post4 = CylinderGeometry.aroundX(goalBarRadius/3, cfg.goalLength, true, 8);
    drawGeom(gl, post4, cfg.goalLength/2, goalBarRadius+cfg.goalWidth/2, 0, 1, 1, 1);
    drawGeom(gl, post4, cfg.goalLength/2, goalBarRadius+cfg.goalWidth/2, cfg.goalHeight, 1, 1, 1);
    drawGeom(gl, post4, cfg.goalLength/2, -goalBarRadius-cfg.goalWidth/2, 0, 1, 1, 1);
    drawGeom(gl, post4, cfg.goalLength/2, -goalBarRadius-cfg.goalWidth/2, cfg.goalHeight, 1, 1, 1);
    
    Geometry post5 = CylinderGeometry.aroundY(goalBarRadius/3, cfg.goalWidth + goalBarRadius * 2, true, 8);
    drawGeom(gl, post5, cfg.goalLength, 0, 0, 1, 1, 1);
    drawGeom(gl, post5, cfg.goalLength, 0, cfg.goalHeight, 1, 1, 1);

    gl.glPopMatrix();
  }

  private void drawGeom(GL2 gl, Geometry g, float x, float y, float z, float scaleX, float scaleY, float scaleZ) {
    gl.glPushMatrix();
    gl.glTranslatef(x, y, z);
    gl.glScalef(scaleX, scaleY, scaleZ);
    g.drawImmediate(gl);
    gl.glPopMatrix();
  }
  
  private void planeGeom(GL2 gl, float x, float y, float scaleX, float scaleY) {
    drawGeom(gl, PlaneGeometry.aroundZ(1, 1), x, y, 0, scaleX, scaleY, 1);
  }

}
