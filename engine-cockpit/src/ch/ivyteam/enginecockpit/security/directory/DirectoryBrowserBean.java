package ch.ivyteam.enginecockpit.security.directory;

import java.util.List;
import java.util.Objects;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.security.directory.ldap.LdapBrowser;
import ch.ivyteam.naming.JndiConfig;

public class DirectoryBrowserBean {

  public static final String DEFAULT_CONTEXT = "defaultContext";
  public static final String IMPORT_USERS_OF_GROUP = "importUsersOfGroup";
  private TreeNode<DirectoryNode> root;
  private TreeNode<DirectoryNode> selectedNode;
  private List<Property> selectedNodeAttributes;
  private DirectoryBrowser directory;

  public void browse(JndiConfig config, boolean enableInsecureSsl, String initialValue) {
    this.root = null;
    this.directory = new LdapBrowser(config, enableInsecureSsl);
    Object selectValue = directory.selectValue(initialValue);
    if (selectValue != null) {
      this.root = new DefaultTreeNode<DirectoryNode>(null, null);
      directory.select(selectValue).forEach(node -> addNewSubnode(root, node, selectValue));
    }
  }

  @SuppressWarnings("unchecked")
  public void onNodeExpand(NodeExpandEvent event) {
    var node = event.getTreeNode();
    node.getChildren().clear();
    loadChildren(node, null);
  }

  private void loadChildren(TreeNode<DirectoryNode> node, Object initialValue) {
    directory.loadChildren(node.getData(), initialValue)
            .forEach(child -> addNewSubnode(node, child, initialValue));
  }

  @SuppressWarnings("unused")
  private void addNewSubnode(TreeNode<DirectoryNode> parent, DirectoryNode ldapNode, Object initialValue) {
    var node = new DefaultTreeNode<>(ldapNode, parent);
    if (ldapNode.startsWith(initialValue)) {
      if (Objects.equals(initialValue, ldapNode.getValue())) {
        node.setSelected(true);
        setSelectedNode(node);
      } else {
        node.setExpanded(true);
        loadChildren(node, initialValue);
      }
    }
    if (ldapNode.isExpandable() && !node.isExpanded()) {
      new DefaultTreeNode<>("loading...", node);
    }
  }

  public TreeNode<DirectoryNode> getTree() {
    return root;
  }

  public TreeNode<DirectoryNode> getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(TreeNode<DirectoryNode> selectedNode) {
    this.selectedNode = selectedNode;
    if (selectedNode != null) {
      selectedNodeAttributes = directory.getNodeAttributes(selectedNode.getData());
    }
  }

  public String getSelectedNameString() {
    var name = getSelectedName();
    if (name == null) {
      return null;
    }
    return name.toString();
  }

  private Object getSelectedName() {
    if (selectedNode == null) {
      return null;
    }
    return selectedNode.getData().getValue();
  }

  public List<Property> getSelectedNodeProperties() {
    return selectedNodeAttributes;
  }
}
