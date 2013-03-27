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
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import roboviz.draw.drawable.DrawableGraph;
import roboviz.draw.drawable.DrawableNode;

public class DrawPanel extends JPanel {
  private JPanel              southPanel;
  private JPanel              shapePanel;
  private JTree               shapeTree;
  private final DrawableGraph graph;

  public DrawPanel(final DrawableGraph graph) {

    this.graph = graph;

    setBorder(new EmptyBorder(5, 5, 5, 5));
    setLayout(new BorderLayout(0, 0));

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    add(tabbedPane, BorderLayout.CENTER);

    shapePanel = new JPanel();
    tabbedPane.addTab("Shapes", null, shapePanel, null);
    shapePanel.setLayout(new BorderLayout(0, 0));

    southPanel = new JPanel();
    shapePanel.add(southPanel, BorderLayout.SOUTH);

    JPanel textPanel = new JPanel();
    tabbedPane.addTab("Annotations", null, textPanel, null);

    shapeTree = new JTree(graph);
    shapeTree.setToggleClickCount(0);
    shapeTree.setCellRenderer(new DrawableTreeCellRenderer());
    shapePanel.add(new JScrollPane(shapeTree), BorderLayout.CENTER);
    textPanel.setLayout(new BorderLayout(0, 0));

    shapeTree.addMouseListener(new TreeMouseSelector());

    JButton btnClear = new JButton("Clear");
    btnClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        graph.clear();
      }
    });
    southPanel.add(btnClear);
  }

  private class TreeMouseSelector extends MouseAdapter {

    public void mousePressed(MouseEvent e) {
      TreePath path = shapeTree.getPathForLocation(e.getX(), e.getY());
      if (path != null) {
        DrawableNode node = (DrawableNode) path.getLastPathComponent();
        node.setVisible(!node.isVisible());
        graph.fireTreeNodesChanged(new TreeModelEvent(this, path));
      }
    }
  }
}
