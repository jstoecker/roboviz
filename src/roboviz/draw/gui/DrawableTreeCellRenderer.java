/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker. The MIT License.
 *******************************************************************************/
package roboviz.draw.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import roboviz.draw.drawable.DrawableNode;

/**
 * Renders cells of a DrawableTree with a checkbox.
 * 
 * @author justin
 */
public class DrawableTreeCellRenderer implements TreeCellRenderer {

  JPanel    panel    = new JPanel();
  JCheckBox checkbox = new JCheckBox();

  public DrawableTreeCellRenderer() {
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.add(checkbox);
    
    panel.setBackground(UIManager.getColor("Tree.textBackground"));
    checkbox.setBackground(UIManager.getColor("Tree.textBackground"));
    
    panel.setPreferredSize(new Dimension(200,25));
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
      boolean expanded, boolean leaf, int row, boolean hasFocus) {

    DrawableNode drawingNode = (DrawableNode) value;
    checkbox.setText(drawingNode.toString());
    checkbox.setSelected(drawingNode.isVisible());
    
    if (drawingNode.isVisible()) {
      checkbox.setForeground(Color.black);
      checkbox.setSelected(true);
    } else {
      checkbox.setForeground(Color.lightGray);
      checkbox.setSelected(false);
    }

    
    return panel;
  }
}
