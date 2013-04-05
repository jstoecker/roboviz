package roboviz.robot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import jgl.geometry.Mesh;
import jgl.loaders.ObjLoader;

/**
 * Renders a robot model.
 * 
 * @author justin
 */
public class RobotRenderer {

  private final String            resourceDir;
  private final Map<String, Mesh> meshes = new HashMap<String, Mesh>();

  public RobotRenderer(String resourceDir) {
    this.resourceDir = resourceDir;
  }

  public void dispose(GL2 gl) {
    for (Mesh mesh : meshes.values())
      mesh.dispose(gl);
  }
  
  public void draw(GL2 gl, RobotModel model) {
    model.update();
    gl.glEnable(GL2.GL_NORMALIZE);
    drawPart(gl, model.getRoot(), model.getGeometryScale());
  }

  private void drawPart(GL2 gl, RobotPart part, float scale) {
    if (part instanceof RobotGeometry) {
      RobotGeometry robotGeometry = (RobotGeometry) part;

      Mesh mesh = meshes.get(robotGeometry.file);
      if (mesh == null) {
        mesh = new ObjLoader().load(new File(resourceDir, robotGeometry.file));
        meshes.put(robotGeometry.file, mesh);
      }

      if (mesh != null) {
        gl.glPushMatrix();
        gl.glMultMatrixf(part.getGlobalTransform().a, 0);
        gl.glScalef(scale, scale, scale);
        mesh.drawArrays(gl);
        gl.glPopMatrix();
      }
    }

    if (!part.getChildren().isEmpty()) {
      for (RobotPart child : part.getChildren())
        drawPart(gl, child, scale);
    }
  }
}
