/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.util;

import java.util.HashMap;
import java.util.LinkedList;

import javax.media.opengl.GL2;

import jgl.scene.geometry.Geometry;
import roboviz.util.GeometryCache.Generator;

/**
 * FIFO cache for display list rendered geometry. This will attempt to reuse previously generated
 * geometry, and new geometry will be created only when there is no suitable display list in the
 * cache. Any class that wants to use this should override to the hashCode and equals methods, which
 * will used to determine if it matches existing entries in the cache; it should also implement the
 * Generator interface, which will call newGeometry when there is no match in the cache.
 * 
 * @author justin
 */
public class GeometryCache<T extends Generator> {

  /** When new geometry needs to be created for the cache, the task is delegated. */
  public interface Generator {
    Geometry<?> newGeometry();
  }

  private final int                 capacity;
  private final HashMap<T, Integer> map     = new HashMap<T, Integer>();
  private final LinkedList<T>       history = new LinkedList<T>();

  public GeometryCache(int capacity) {
    this.capacity = capacity;
  }

  public void render(GL2 gl, T generator) {

    // check if a display list has been created that matches the geometry description
    Integer displayList = map.get(generator);
    if (displayList == null) {
      // none found, so create a new display list and geometry
      displayList = gl.glGenLists(1);
      gl.glNewList(displayList, GL2.GL_COMPILE);
      generator.newGeometry().drawArrays(gl);
      gl.glEndList();

      // add to the cache
      map.put(generator, displayList);

      // dispose of oldest if capacity is exceeded
      history.add(generator);
      if (history.size() > capacity) {
        Integer oldList = map.remove(history.removeFirst());
        gl.glDeleteLists(oldList, 1);
      }
    }

    // render using the display list
    gl.glCallList(displayList);
  }

  public void dispose(GL2 gl) {
    for (int i : map.values()) {
      gl.glDeleteLists(i, 1);
    }
    map.clear();
    history.clear();
  }
}
