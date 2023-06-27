package ch.ivyteam.enginecockpit.security.directory;

import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.ivy.security.identity.jndi.browser.LdapBrowser;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryBrowser;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryNode;
import ch.ivyteam.ivy.security.identity.spi.browser.Property;
import ch.ivyteam.log.Logger;
import ch.ivyteam.naming.JndiConfig;

public class DirectoryBrowserBean {

  private final static Logger LOGGER = Logger.getLogger(DirectoryBrowserBean.class);
  private TreeNode<DirectoryNode> root;
  private TreeNode<DirectoryNode> selectedNode;
  private List<Property> selectedNodeAttributes;
  private DirectoryBrowser directory;

  public void browse(JndiConfig config, boolean enableInsecureSsl, String initialValue) {
    browse(new LdapBrowser(config, enableInsecureSsl), initialValue);
  }

  public void browse(DirectoryBrowser browser, String initialValue) {
    this.root = null;
    this.directory = browser;
    try {
      Object selectValue = browser.selectValue(initialValue);
      this.root = new DefaultTreeNode<DirectoryNode>(null, null);
      browser.root().forEach(node -> addNewSubnode(root, node, selectValue));
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
      directory.children(node.getData())
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
        selectedNodeAttributes = directory.properties(selectedNode.getData());
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
    FacesContext.getCurrentInstance().addMessage("ldapBrowserMessage",
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
