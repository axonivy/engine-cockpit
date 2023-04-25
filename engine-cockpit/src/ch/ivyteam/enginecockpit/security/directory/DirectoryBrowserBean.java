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
import ch.ivyteam.ivy.environment.Ivy;
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

  public void browse(JndiConfig config, boolean enableInsecureSsl, String idToSelect) {
	  Ivy.log().info("broser");
    this.root = null;
    this.directory = new LdapBrowser(config, enableInsecureSsl);
    try {
      this.root = new DefaultTreeNode<DirectoryNode>(null, null);
      directory.root().forEach(node -> addNewSubnode(root, node, idToSelect));
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

  private void loadChildren(TreeNode<DirectoryNode> node, String idToSelect) {
    try {
      directory.children(node.getData())
              .forEach(child -> addNewSubnode(node, child, idToSelect));
    } catch (Exception ex) {
      errorMessage(ex);
    }
  }

  private void addNewSubnode(TreeNode<DirectoryNode> parent, DirectoryNode node, String idToSelect) {
    var treeNode = new DefaultTreeNode<>(node, parent);
    if (idToSelect != null && directory.isSubNode(node, idToSelect)) {
      if (Objects.equals(idToSelect, node.getId())) {
        treeNode.setSelected(true);
        treeNode.setExpanded(true);
      } else {
        treeNode.setExpanded(true);
        loadChildren(treeNode, idToSelect);
      }
    }
    if (node.isExpandable() && !treeNode.isExpanded()) {
      new DefaultTreeNode<>("loading...", treeNode);
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
    if (selectedNode == null) {
      return null;
    }
    return selectedNode.getData().getId();
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
