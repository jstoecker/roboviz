package roboviz.scene.sim3d.state;


/**
 * Describes an object and its material. There are two types: static meshes and standard mesh
 * objects (box, cylinder, etc).
 * 
 * @author Justin Stoecker
 */
public abstract class GeometryNode extends Node {
  protected boolean transparent = false;
  protected boolean visible     = false;
  protected float[] scaleXYZ;

  public GeometryNode(Node parent, SExpression exp) {
    super(parent);
    parse(exp);
  }

  public boolean isVisible() {
    return visible;
  }

  public boolean isTransparent() {
    return transparent;
  }

  protected abstract void parse(SExpression sexpr);

  @Override
  public void update(SExpression exp) {
    if (exp.getChildren() != null)
      parse(exp);
    super.update(exp);
  }
}