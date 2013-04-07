package roboviz.scene.sim3d.state;

import jgl.math.vector.Mat4f;

public class TransformNode extends Node {

  public TransformNode(Node parent, SExpression exp) {
     super(parent);
     // (nd TRF (SLT nx ny nz 0 ox oy oz 0 ax ay az 0 Px Py Pz 1 ))
     setMatrix(exp.getChildren().get(0).getAtoms());
  }

  private void setMatrix(String[] atoms) {
     if (atoms[0].equals("SLT")) {
        float[] a = new float[16];
        for (int i = 0; i < 16; i++)
           a[i] = Float.parseFloat(atoms[i + 1]);
        transform = new Mat4f(a);
     }
  }

  @Override
  public void update(SExpression exp) {
     if (exp.getChildren() != null) {
        setMatrix(exp.getChildren().get(0).getAtoms());
     }
     super.update(exp);
  }
}
