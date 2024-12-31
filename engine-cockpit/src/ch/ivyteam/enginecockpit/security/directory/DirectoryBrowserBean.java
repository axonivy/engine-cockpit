package ch.ivyteam.enginecockpit.security.directory;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryBrowser;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryNode;
import ch.ivyteam.ivy.security.identity.spi.browser.Property;
import ch.ivyteam.log.Logger;

public class DirectoryBrowserBean {

  private final static Logger LOGGER = Logger.getLogger(DirectoryBrowserBean.class);
  private TreeNode<DirectoryNode> root;
  private TreeNode<DirectoryNode> selectedNode;
  private List<Property> selectedNodeAttributes;
  private DirectoryBrowser directoryBrowser;

  public void browse(DirectoryBrowser browser, String idToSelect) {
    this.root = null;
    this.directoryBrowser = browser;
    try {
      var directoryNodeToSelect = findDirectoryNode(browser, idToSelect);
      this.root = new DefaultTreeNode<>(null, null);
      browser.root().forEach(node -> addNewSubnode(root, node, directoryNodeToSelect));
    } catch (Exception ex) {
      errorMessage(ex);
    }
  }

  private DirectoryNode findDirectoryNode(DirectoryBrowser browser, String idToSelect) {
    if (idToSelect != null && !idToSelect.isBlank()) {
      return browser.find(idToSelect);
    }
    return null;
  }

  public String icon(DirectoryNode node) {
    return switch (node.type()) {
      case DEFAULT -> "folder-empty";
      case DOMAIN -> "buildings-1";
      case GROUP -> "multiple-neutral-1";
      case ORGANIZATION -> "folder-share";
      case USER -> "single-neutral-actions";
    };
  }

  @SuppressWarnings("unchecked")
  public void onNodeExpand(NodeExpandEvent event) {
    var node = event.getTreeNode();
    node.getChildren().clear();
    loadChildren(node, null);
  }

  @SuppressWarnings("unused")
  private void addNewSubnode(TreeNode<DirectoryNode> parent, DirectoryNode dirNode, DirectoryNode directoryNodeToSelect) {
    var node = new DefaultTreeNode<>(dirNode, parent);
    if (directoryNodeToSelect != null) {
      if (dirNode.id().equals(directoryNodeToSelect.id())) {
        node.setSelected(true);
        node.setExpanded(true);
        loadChildren(node, directoryNodeToSelect);
      } else if (dirNode.isParent(directoryNodeToSelect)) {
        node.setExpanded(true);
        loadChildren(node, directoryNodeToSelect);
      }
    }
    if (dirNode.expandable() && !node.isExpanded()) {
      new DefaultTreeNode<>("loading...", node);
    }
  }

  private void loadChildren(TreeNode<DirectoryNode> node, DirectoryNode initialValue) {
    try {
      directoryBrowser.children(node.getData())
          .forEach(child -> addNewSubnode(node, child, initialValue));
    } catch (Exception ex) {
      errorMessage(ex);
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
        selectedNodeAttributes = directoryBrowser.properties(selectedNode.getData());
      }
    } catch (Exception ex) {
      errorMessage(ex);
    }
  }

  public String getSelectedNameString() {
    if (selectedNode == null) {
      return null;
    }
    return selectedNode.getData().id();
  }

  public List<Property> getSelectedNodeProperties() {
    return selectedNodeAttributes;
  }

  private void errorMessage(Exception ex) {
    LOGGER.error("Error in directory browser", ex);
    FacesContext.getCurrentInstance().addMessage("directoryBrowserMessage",
        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", getEndUserMessage(ex)));
  }

  private static String getEndUserMessage(Exception ex) {
    var message = ex.getMessage();
    if (ex.getCause() != null) {
      message = ex.getCause().getMessage();
    }
    if (StringUtils.contains(message, "AcceptSecurityContext")) {
      return "There seems to be a problem with your credentials.";
    }
    return message;
  }
}
