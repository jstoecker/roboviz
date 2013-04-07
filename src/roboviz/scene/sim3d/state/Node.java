package roboviz.scene.sim3d.state;

import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;

/**
 * Element of the scene graph that may have children nodes. Used to organize the arrangement of
 * objects in the server simulation, so each node contains a local transformation matrix. Subclasses
 * may have other properties that define objects, shapes, lights, or other features relevant to the
 * scene.
 * 
 * @author justin
 */
public class Node {

  protected Mat4f           transform = Transform.identity();
  protected ArrayList<Node> children  = new ArrayList<Node>();
  protected Node            parent    = null;

  public Node(Node parent) {
    this.parent = parent;
  }

  public Node getParent() {
    return parent;
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isLeaf() {
    return children.isEmpty();
  }

  public List<Node> getChildren() {
    return children;
  }

  public Mat4f getLocalTransform() {
    return transform;
  }

  public Mat4f getGlobalTransform() {
    return isRoot() ? transform : parent.getGlobalTransform().times(transform);
  }

  protected void update(SExpression exp) {
    if (exp.getChildren() == null || children == null)
      return;

    int childIndex = 0;
    for (SExpression e : exp.getChildren()) {
      if (e.getAtoms()[0].equals("nd")) {
        Node child = children.get(childIndex++);
        child.update(e);
      }
    }
  }
}
