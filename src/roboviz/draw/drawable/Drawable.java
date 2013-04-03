/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw.drawable;

import javax.media.opengl.GL2;

public abstract class Drawable {

  public final String id;

  public Drawable(String id) {
    this.id = id;
  }

  public void render(GL2 gl) {
  }
}
