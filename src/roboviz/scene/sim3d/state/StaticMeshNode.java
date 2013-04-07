package roboviz.scene.sim3d.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import jgl.math.vector.Transform;

public class StaticMeshNode extends GeometryNode {

  String      name;
  Set<String> materials;

  public StaticMeshNode(Node parent, SExpression exp) {
    super(parent, exp);
    // (nd StaticMesh (load <model>) (sSc <x> <y> <z>) (setVisible 1)
    // (setTransparent) (resetMaterials <material-list>))
  }

  public Set<String> getMaterials() {
    return materials;
  }

  public String getName() {
    return name;
  }

  /**
   * Checks if a static mesh node contains all of the materials in mats.
   */
  public boolean containsAllMats(String[] mats) {
    // if the list of materials the node must contain is longer than the list
    // this node actually contains, it can't contain all of them
    if (mats.length > materials.size())
      return false;

    for (int i = 0; i < mats.length; i++)
      if (!materials.contains(mats[i]))
        return false;

    return true;
  }

  @Override
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
