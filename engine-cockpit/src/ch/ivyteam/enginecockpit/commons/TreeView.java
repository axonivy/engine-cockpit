package ch.ivyteam.enginecockpit.commons;

import java.util.List;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public abstract class TreeView<T> {
  protected TreeNode<T> rootTreeNode;
  protected TreeNode<T> filteredTreeNode;
  protected String filter = "";

  public void reloadTree() {
    filter = "";
    rootTreeNode = new DefaultTreeNode<T>("Tree", null, null);
    buildTree();
  }

  protected abstract void buildTree();

  public TreeNode<T> getTree() {
    if (filter.isEmpty()) {
      return rootTreeNode;
    }
    return filteredTreeNode;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
    filteredTreeNode = new DefaultTreeNode<T>("Filtered tree", null, null);
    filterTree(rootTreeNode.getChildren());
  }

  private void filterTree(List<TreeNode<T>> nodes) {
    for (var node : nodes) {
      filterNode(node);
      filterTree(node.getChildren());
    }
  }

  protected abstract void filterNode(TreeNode<T> node);

  public void nodeExpand(NodeExpandEvent event) {
    event.getTreeNode().setExpanded(true);
  }

  public void nodeCollapse(NodeCollapseEvent event) {
    event.getTreeNode().setExpanded(false);
  }

  public void expandAllNodes() {
    expandAllNodes(rootTreeNode, true);
  }

  public void collapseAllNodes() {
    expandAllNodes(rootTreeNode, false);
  }

  private static <T> void expandAllNodes(TreeNode<T> treeNode, boolean expand) {
    var children = treeNode.getChildren();
    for (var child : children) {
      expandAllNodes(child, expand);
    }
    treeNode.setExpanded(expand);
  }
}
