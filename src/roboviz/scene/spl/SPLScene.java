package roboviz.scene.spl;

import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Viewport;
import jgl.math.vector.Transform;
import jgl.scene.geometry.CircleGeometry;
import jgl.scene.geometry.PlaneGeometry;
import roboviz.scene.Scene;

public class SPLScene extends Scene {

  Config cfg = new Config(new File("/Users/justin/Desktop/splscene.cfg"));

  
  
  public SPLScene() {
  }

  @Override
  public void init(GL2 gl) {
    gl.glDisable(GL.GL_DEPTH_TEST);
    
    camera.setView(Transform.lookAt(0, -5, 5, 0, 0, 0, 0, 0, 1));
  }

  @Override
  public void update(GL2 gl, double elapsedMS) {
  }

  @Override
  public void render(GL2 gl, Viewport viewport) {
    camera.setView(Transform.identity());
    camera.setProjection(Transform.orthographic(
        -cfg.fieldLength/2-cfg.borderStripWidth, 
        cfg.fieldLength/2+cfg.borderStripWidth, 
        -cfg.fieldWidth/2-cfg.borderStripWidth, 
        cfg.fieldWidth/2+cfg.borderStripWidth, 
        -1, 1));
    
    camera.apply(gl);

    createField(gl);
  }

  @Override
  public void dispose(GL2 gl) {
    cfg.save();
  }

  @Override
  public void resize(GL2 gl, Viewport viewport) {

  }
  
  private void createField(GL2 gl) {
    // green field
    gl.glColor3f(0, .5f, 0);
    planeGeom(gl, 0, 0, cfg.fieldLength + 2 * cfg.borderStripWidth, cfg.fieldWidth + 2 * cfg.borderStripWidth);
    
    // white field lines
    gl.glColor3f(1, 1, 1);
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
    
    // goal left
  }

  private void planeGeom(GL2 gl, float x, float y, float scaleX, float scaleY) {
    gl.glPushMatrix();
    gl.glTranslatef(x, y, 0);
    gl.glScalef(scaleX, scaleY, 1);
    PlaneGeometry.aroundZ(1, 1).drawImmediate(gl);
    gl.glPopMatrix();
  }

}
