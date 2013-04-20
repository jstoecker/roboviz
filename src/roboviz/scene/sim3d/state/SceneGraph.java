package roboviz.scene.sim3d.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;

/**
 * Contains scene information from rcssserver3d.
 * 
 * @author justin
 */
public class SceneGraph {

  private Node root;

  /**
   * Creates a new scene graph that is initially empty.
   */
  public SceneGraph() {
  }

  /**
   * The root of the graph.
   */
  public Node getRoot() {
    return root;
  }

  /**
   * Updates the scene graph.
   */
  public void update(SExpression header, SExpression graph) {
    if (header.getAtoms()[0].equals("RSG")) {
      root = new Node(null);
      readNodes(root, graph);
    } else {
      root.update(graph);
    }
  }

  /**
   * Recursive method that reads nodes from expression and adds them to parent
   */
  private void readNodes(Node parent, SExpression exp) {
    // if there are no children expressions, the parent node must be a leaf
    ArrayList<SExpression> subExpressions = exp.getChildren();
    if (subExpressions == null)
      return;

    // otherwise, there may be nodes to parse and add to the parent node
    for (SExpression e : subExpressions) {
      // each node declaration starts with "nd" followed by its type
      String[] atoms = e.getAtoms();
      if (atoms[0].equals("nd")) {
        String type = atoms[1];
        Node node = null;
        if (type.equals("TRF")) {
          node = new TransformNode(parent, e);
        } else if (type.equals("Light")) {
          node = new Node(parent); // light nodes are ignored
        } else if (type.equals("StaticMesh")) {
          node = new StaticMeshNode(parent, e);
        } else if (type.equals("SMN")) {
          node = new Node(parent); // std mesh nodes are ignored
        }

        if (node != null) {
          if (parent.children == null)
            parent.children = new ArrayList<Node>();
          parent.children.add(node);

          // keep reading child's branch of nodes recursively
          readNodes(node, e);
        }
      }
    }
  }

  public class Node {

    Mat4f           transform = Transform.identity();
    ArrayList<Node> children  = new ArrayList<Node>();
    Node            parent    = null;

    private Node(Node parent) {
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

  public class TransformNode extends Node {

    private TransformNode(Node parent, SExpression exp) {
      super(parent);
      setMatrix(exp.getChildren().get(0).getAtoms());
    }

    @Override
    public void update(SExpression exp) {
      if (exp.getChildren() != null) {
        setMatrix(exp.getChildren().get(0).getAtoms());
      }
      super.update(exp);
    }

    private void setMatrix(String[] atoms) {
      if (atoms[0].equals("SLT")) {
        float[] a = new float[16];
        for (int i = 0; i < 16; i++)
          a[i] = Float.parseFloat(atoms[i + 1]);
        transform = new Mat4f(a);
      }
    }
  }

  public class StaticMeshNode extends Node {

    boolean     transparent = false;
    boolean     visible     = false;
    float[]     scaleXYZ;
    String      name;
    Set<String> materials;

    public StaticMeshNode(Node parent, SExpression exp) {
      super(parent);
      parse(exp);
    }

    public Set<String> getMaterials() {
      return materials;
    }

    public String getName() {
      return name;
    }

    protected void parse(SExpression sexpr) {
      materials = new HashSet<String>();
      ArrayList<SExpression> exprs = sexpr.getChildren();
      for (SExpression e : exprs) {
        String property = e.getAtoms()[0];
        if (property.equals("load")) {
          name = e.getAtoms()[1];
        } else if (property.equals("sSc")) {
          float sx = Float.parseFloat(e.getAtoms()[1]);
          float sy = Float.parseFloat(e.getAtoms()[2]);
          float sz = Float.parseFloat(e.getAtoms()[3]);
          transform = Transform.scale(sx, sy, sz);
        } else if (property.equals("setVisible")) {
          visible = e.getAtoms()[1].equals("1");
        } else if (property.equals("resetMaterials")) {
          for (int i = 1; i < e.getAtoms().length; i++) {
            String mat = e.getAtoms()[i];
            if (mat != null)
              materials.add(mat);
          }
        } else if (property.equals("setTransparent")) {
          transparent = true;
        }
      }
    }
  }
}