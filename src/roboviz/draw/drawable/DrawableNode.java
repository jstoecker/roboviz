/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker
 *  The MIT License. See LICENSE in project root.
 *******************************************************************************/
package roboviz.draw.drawable;

import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.Vec3f;

/**
 * Contains drawables with the same set name.
 * 
 * @author justin
 */
public class DrawableNode implements Comparable<DrawableNode> {

  private final String        name;
  DrawableNode                parent;
  List<DrawableNode>          children;

  private Vec3f               frame       = new Vec3f(0, 0, 0);
  private boolean             visible     = true;
  private ArrayList<Drawable> frontBuffer = new ArrayList<Drawable>();
  private ArrayList<Drawable> backBuffer  = new ArrayList<Drawable>();

  public DrawableNode(String name, DrawableNode parent) {
    this.name = name;
    this.parent = parent;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;

    DrawableNode parent = this.parent;
    if (visible && parent != null) {
      do {
        parent.visible = visible;
        parent = parent.parent;
      } while (parent != null);
    }
    recursiveVisible(visible);
  }

  private void recursiveVisible(boolean visible) {
    if (children != null) {
      for (DrawableNode child : children) {
        child.visible = visible;
        child.recursiveVisible(visible);
      }
    }
  }

  /**
   * Returns the list of shapes that are intended for rendering. This list should not be modified.
   */
  public synchronized List<Drawable> getFront() {
    return frontBuffer;
  }

  /**
   * Adds data to the back buffer set. This method is not synchronized as the only thread that
   * should be using it is the one that is also responsible for swapping the buffers.
   */
  public void put(Drawable data) {
    backBuffer.add(data);
  }

  /**
   * Swaps the front and back buffers and clears the new back buffer.
   */
  public synchronized void swapBuffers() {
    ArrayList<Drawable> temp = backBuffer;
    backBuffer = frontBuffer;
    frontBuffer = temp;
    backBuffer.clear();
  }

  public String getName() {
    return name;
  }

  public List<DrawableNode> getChildren() {
    return children;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DrawableNode other = (DrawableNode) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public int compareTo(DrawableNode that) {
    return this.name.compareTo(that.name);
  }

  @Override
  public String toString() {
    return name;
  }
}
