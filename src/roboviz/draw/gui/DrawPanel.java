/*******************************************************************************
 *  Copyright (C) 2013 Justin Stoecker
 *  The MIT License. See LICENSE in project root.
 *******************************************************************************/
package roboviz.draw.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import roboviz.draw.drawable.DrawableGraph;
import roboviz.draw.drawable.DrawableNode;

public class DrawPanel extends JPanel {
  private JTree               tree;
  private final DrawableGraph graph;

  public DrawPanel(final DrawableGraph graph) {

    this.graph = graph;

    setBorder(new EmptyBorder(5, 5, 5, 5));
    setLayout(new BorderLayout(0, 0));

    tree = new JTree(graph);
    tree.setToggleClickCount(0);
    tree.setCellRenderer(new DrawableTreeCellRenderer());
    tree.addMouseListener(new TreeMouseSelector());
    add(new JScrollPane(tree), BorderLayout.CENTER);

    JButton btnClear = new JButton("Clear");
    btnClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        graph.clear();
        revalidate();
      }
    });
    add(btnClear, BorderLayout.SOUTH);
  }

  private class TreeMouseSelector extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      TreePath path = tree.getPathForLocation(e.getX(), e.getY());
      if (path != null) {
        DrawableNode node = (DrawableNode) path.getLastPathComponent();
        node.setVisible(!node.isVisible());
        graph.fireTreeNodesChanged(new TreeModelEvent(this, path));
      }
    }
  }
}
