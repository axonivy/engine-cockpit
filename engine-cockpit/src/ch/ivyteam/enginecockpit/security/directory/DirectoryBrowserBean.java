package ch.ivyteam.enginecockpit.security.directory;

import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.security.directory.ldap.LdapBrowser;
import ch.ivyteam.log.Logger;
import ch.ivyteam.naming.JndiConfig;

public class DirectoryBrowserBean {

  private final static Logger LOGGER = Logger.getLogger(DirectoryBrowserBean.class);
  public static final String DEFAULT_CONTEXT = "defaultContext";
  public static final String IMPORT_USERS_OF_GROUP = "importUsersOfGroup";
  private TreeNode<DirectoryNode> root;
  private TreeNode<DirectoryNode> selectedNode;
  private List<Property> selectedNodeAttributes;
  private DirectoryBrowser directory;

  public void browse(JndiConfig config, boolean enableInsecureSsl, String initialValue) {
    this.root = new DefaultTreeNode<DirectoryNode>(null, null);
    this.directory = new LdapBrowser(config, enableInsecureSsl);
    try {
      Object selectValue = directory.selectValue(initialValue);
      directory.root().forEach(node -> addNewSubnode(root, node, selectValue));
    } catch (Exception ex) {
      errorMessage(ex);
    }
  }

  @SuppressWarnings("unchecked")
  public void onNodeExpand(NodeExpandEvent event) {
    var node = event.getTreeNode();
    node.getChildren().clear();
    loadChildren(node, null);
  }

  private void loadChildren(TreeNode<DirectoryNode> node, Object initialValue) {
    try {
      directory.loadChildren(node.getData(), initialValue)
              .forEach(child -> addNewSubnode(node, child, initialValue));
    } catch (Exception ex) {
      errorMessage(ex);
    }
  }

  @SuppressWarnings("unused")
  private void addNewSubnode(TreeNode<DirectoryNode> parent, DirectoryNode ldapNode, Object initialValue) {
    var node = new DefaultTreeNode<>(ldapNode, parent);
    if (initialValue != null && ldapNode.startsWith(initialValue)) {
      if (Objects.equals(initialValue, ldapNode.getValue())) {
        node.setSelected(true);
        node.setExpanded(true);
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
    try {
      if (selectedNode != null) {
        selectedNodeAttributes = directory.getNodeAttributes(selectedNode.getData());
      }
    } catch (Exception ex) {
      errorMessage(ex);
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

  private void errorMessage(Exception ex) {
    LOGGER.error("Error in LDAP call", ex);
    var message = ex.getMessage();
    if (StringUtils.contains(message, "AcceptSecurityContext")) {
      message = "There seems to be a problem with your credentials.";
    }
    FacesContext.getCurrentInstance().addMessage("ldapBrowserMessage",
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
  }
}
