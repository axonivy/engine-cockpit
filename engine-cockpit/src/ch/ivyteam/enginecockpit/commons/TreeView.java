package ch.ivyteam.enginecockpit.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public abstract class TreeView<T> {
  protected TreeNode<T> rootTreeNode;
  protected TreeNode<T> filteredTreeNode;
  protected String filter = "";

  protected List<String> expandedTreeNodeDataIdentifiers = new ArrayList<>();

  public void reloadTree() {
    filter = "";
    rootTreeNode = new DefaultTreeNode<>("Tree", null, null);
    buildTree();
    applyToAllNodes(this::applyExpanded);
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
    filteredTreeNode = new DefaultTreeNode<>("Filtered tree", null, null);
    filterTree(rootTreeNode.getChildren());
  }

  private void filterTree(List<TreeNode<T>> nodes) {
    for (var node : nodes) {
      filterNode(node);
      filterTree(node.getChildren());
    }
  }

  protected abstract void filterNode(TreeNode<T> node);

  @SuppressWarnings("unchecked")
  public void nodeExpand(NodeExpandEvent event) {
    expandNode(event.getTreeNode());
  }

  private void expandNode(TreeNode<T> node) {
    var data = node.getData();
    if (data == null) {
      return;
    }
    expandedTreeNodeDataIdentifiers.add(dataIdentifier(data));
    node.setExpanded(true);
  }

  @SuppressWarnings("unchecked")
  public void nodeCollapse(NodeCollapseEvent event) {
    collapseNode(event.getTreeNode());
  }

  private void collapseNode(TreeNode<T> node) {
    var data = node.getData();
    if (data == null) {
      return;
    }
    expandedTreeNodeDataIdentifiers.remove(dataIdentifier(data));
    node.setExpanded(false);
  }

  public void expandAllNodes() {
    applyToAllNodes(this::expandNode);
  }

  public void collapseAllNodes() {
    applyToAllNodes(this::collapseNode);
  }

  private void applyExpanded(TreeNode<T> node) {
    var data = node.getData();
    if (data == null) {
      return;
    }
    if (expandedTreeNodeDataIdentifiers.contains(dataIdentifier(data))) {
      node.setExpanded(true);
    }
  }

  /* Implement to keep expansion state between tree reloads */
  @SuppressWarnings("unused")
  protected String dataIdentifier(T data) {
    return null;
  }

  private void applyToAllNodes(Consumer<TreeNode<T>> consumer) {
    applyToAllNodes(rootTreeNode, consumer);
  }

  private static <T> void applyToAllNodes(TreeNode<T> treeNode, Consumer<TreeNode<T>> consumer) {
    var children = treeNode.getChildren();
    for (var child : children) {
      applyToAllNodes(child, consumer);
    }
    consumer.accept(treeNode);
  }
}
