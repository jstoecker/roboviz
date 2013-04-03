package roboviz.scene.spl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.scene.geometry.Geometry;
import jgl.scene.geometry.extra.CircleGeometry;
import jgl.scene.geometry.extra.CylinderGeometry;
import jgl.scene.geometry.extra.PlaneGeometry;
import jgl.scene.materials.Material;

/**
 * Renders the static field geometry: lines, goals, etc.
 * 
 * @author justin
 */
public class FieldRenderer {

  Config cfg;
  int displayList = -1;
  
  public FieldRenderer(Config config) {
    this.cfg = config;
  }
  
  void draw(GL2 gl) {
    gl.glCallList(displayList);
  }
  
  void dispose(GL2 gl) {
    gl.glDeleteLists(displayList, 1);
  }
  
  void init(GL2 gl) {
    displayList = gl.glGenLists(1);
    gl.glNewList(displayList, GL2.GL_COMPILE);
    {
      drawStaticGeometry(gl);
    }
    gl.glEndList();
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
    CircleGeometry.posZ(innerRadius, innerRadius + cfg.lineWidth, 64).drawImmediate(gl);
    
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
    
    Geometry post = CylinderGeometry.posZ(goalBarRadius, goalBarRadius, cfg.goalHeight + goalBarRadius, 16, false);
    Geometry post2 = CylinderGeometry.posY(goalBarRadius, goalBarRadius, cfg.goalWidth + cfg.goalBarDiameter * 2, 16, true);
    drawGeom(gl, post, 0, cfg.goalWidth/2 + goalBarRadius, (cfg.goalHeight + goalBarRadius) / 2, 1, 1, 1);
    drawGeom(gl, post, 0, -(cfg.goalWidth/2 + goalBarRadius), (cfg.goalHeight + goalBarRadius) / 2, 1, 1, 1);
    drawGeom(gl, post2, 0, 0, cfg.goalHeight + goalBarRadius, 1, 1, 1);
    
    m = new Material();
    m.setDiffAmbient(0.8f, 0.8f, 0.8f, 1);
    m.apply(gl);
    
    Geometry post3 = CylinderGeometry.posZ(goalBarRadius/3, goalBarRadius/3, cfg.goalHeight, 8, true);
    drawGeom(gl, post3, cfg.goalLength, goalBarRadius+cfg.goalWidth/2, cfg.goalHeight/2, 1, 1, 1);
    drawGeom(gl, post3, cfg.goalLength, -goalBarRadius-cfg.goalWidth/2, cfg.goalHeight/2, 1, 1, 1);
    
    Geometry post4 = CylinderGeometry.posX(goalBarRadius/3, goalBarRadius/3, cfg.goalLength, 8, true);
    drawGeom(gl, post4, cfg.goalLength/2, goalBarRadius+cfg.goalWidth/2, 0, 1, 1, 1);
    drawGeom(gl, post4, cfg.goalLength/2, goalBarRadius+cfg.goalWidth/2, cfg.goalHeight, 1, 1, 1);
    drawGeom(gl, post4, cfg.goalLength/2, -goalBarRadius-cfg.goalWidth/2, 0, 1, 1, 1);
    drawGeom(gl, post4, cfg.goalLength/2, -goalBarRadius-cfg.goalWidth/2, cfg.goalHeight, 1, 1, 1);
    
    Geometry post5 = CylinderGeometry.posY(goalBarRadius/3, goalBarRadius/3, cfg.goalWidth + goalBarRadius * 2, 8, true);
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
    drawGeom(gl, PlaneGeometry.posZ(1, 1), x, y, 0, scaleX, scaleY, 1);
  }
}
