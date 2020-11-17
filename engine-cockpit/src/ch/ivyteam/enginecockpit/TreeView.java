package ch.ivyteam.enginecockpit;

import java.util.List;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public abstract class TreeView
{
  protected TreeNode rootTreeNode;
  protected TreeNode filteredTreeNode;
  protected String filter = "";

  public void reloadTree()
  {
    filter = "";
    rootTreeNode = new DefaultTreeNode("Tree", null);
    buildTree();
  }

  protected abstract void buildTree();

  public TreeNode getTree()
  {
    if (filter.isEmpty())
    {
      return rootTreeNode;
    }
    return filteredTreeNode;
  }

  public String getFilter()
  {
    return filter;
  }

  public void setFilter(String filter)
  {
    this.filter = filter;
    filteredTreeNode = new DefaultTreeNode("Filtered tree", null);
    filterTree(rootTreeNode.getChildren());
  }

  private void filterTree(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      filterNode(node);
      filterTree(node.getChildren());
    }
  }

  protected abstract void filterNode(TreeNode node);

  public void nodeExpand(NodeExpandEvent event)
  {
    event.getTreeNode().setExpanded(true);
  }

  public void nodeCollapse(NodeCollapseEvent event)
  {
    event.getTreeNode().setExpanded(false);
  }

  public void expandAllNodes()
  {
    expandAllNodes(rootTreeNode, true);
  }

  public void collapseAllNodes()
  {
    expandAllNodes(rootTreeNode, false);
  }

  private static void expandAllNodes(TreeNode treeNode, boolean expand)
  {
    List<TreeNode> children = treeNode.getChildren();
    for (TreeNode child : children)
    {
      expandAllNodes(child, expand);
    }
    treeNode.setExpanded(expand);
  }
}
