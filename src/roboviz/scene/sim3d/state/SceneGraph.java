package roboviz.scene.sim3d.state;

import java.util.ArrayList;

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
        if (type.equals("TFN")) {
          node = new TransformNode(parent, e);
        } else if (type.equals("Light")) {
//          node = new LightNode(parent, e);
        } else if (type.equals("StaticMesh")) {
          node = new StaticMeshNode(parent, e);
        } else if (type.equals("SMN")) {
//          node = new StdMeshNode(parent, e);
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
}