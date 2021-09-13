package ch.ivyteam.enginecockpit.security.ldapbrowser;

import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.naming.JndiConfig;

public class LdapBrowser {

  public static final String DEFAULT_CONTEXT = "defaultContext";
  public static final String IMPORT_USERS_OF_GROUP = "importUsersOfGroup";

  private TreeNode root;
  private TreeNode selectedNode;
  private List<Property> selectedNodeAttributes;
  private JndiConfig jndiConfig;
  private boolean insecureSsl;

  public void browse(JndiConfig config, boolean enableInsecureSsl, String initialValue) {
    this.jndiConfig = config;
    this.insecureSsl = enableInsecureSsl;
    this.root = null;
    try (var context = new LdapBrowserContext(config, enableInsecureSsl)) {
      var name = jndiConfig.getDefaultContextName();
      if (name.isEmpty()) {
        root = new DefaultTreeNode(name, null);
        context.browse(name).forEach(node -> addNewSubnode(root, node, initialValue));
        return;
      }
      root = new DefaultTreeNode("", null);
      addNewSubnode(root, context.createLdapNode(name, evalLdapName(root)), initialValue);
    } catch (NamingException ex) {
      errorMessage(ex.getMessage());
    }
  }

  public void onNodeExpand(NodeExpandEvent event) {
    var node = event.getTreeNode();
    node.getChildren().clear();
    loadChildren(node, null);
  }

  private void loadChildren(TreeNode node, String initialValue) {
    var name = evalLdapName(node);
    try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
      context.children(name).forEach(child -> addNewSubnode(node, child, initialValue));
    } catch (NamingException ex) {
      errorMessage(ex.getMessage());
    }
  }

  @SuppressWarnings("unused")
  private void addNewSubnode(TreeNode treeNode, LdapBrowserNode ldapNode, String initialValue) {
    var node = new DefaultTreeNode(ldapNode, treeNode);
    var nodeName = ldapNode.getName().toString();
    if (StringUtils.isNotBlank(initialValue) && StringUtils.endsWithIgnoreCase(initialValue, nodeName)) {
      var subInitValue = StringUtils.removeEndIgnoreCase(initialValue, nodeName);
      if (StringUtils.isBlank(subInitValue)) {
        node.setSelected(true);
        setSelectedNode(node);
      } else {
        node.setExpanded(true);
        loadChildren(node, StringUtils.removeEnd(subInitValue, ","));
      }
    }
    if (ldapNode.isExpandable() && !node.isExpanded()) {
      new DefaultTreeNode("loading...", node);
    }
  }

  public TreeNode getTree() {
    return root;
  }

  public TreeNode getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(TreeNode selectedNode) {
    this.selectedNode = selectedNode;
    if (selectedNode != null) {
      selectedNodeAttributes = getNodeArguments();
    }
  }

  public List<Property> getSelectedNodeAttributes() {
    return selectedNodeAttributes;
  }

  private List<Property> getNodeArguments() {
    try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
      return context.getAttributes(getSelectedLdapName());
    } catch (NamingException ex) {
      errorMessage(ex.getMessage());
    }
    return Collections.emptyList();
  }

  public String getSelectedLdapName() {
    return evalLdapName(selectedNode);
  }

  private String evalLdapName(TreeNode node) {
    if (node == null) {
      return "";
    }
    return node.toString() + evalParentName(node.getParent());
  }

  private String evalParentName(TreeNode parent) {
    if (parent != null && !StringUtils.isBlank(parent.toString())) {
      return "," + parent.toString() + evalParentName(parent.getParent());
    }
    return "";
  }

  private void errorMessage(String ex) {
    var message = ex;
    if (ex.contains("AcceptSecurityContext")) {
      message = "There seems to be a problem with your credentials.";
    }
    FacesContext.getCurrentInstance().addMessage("ldapBrowserMessage",
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));

  }
}
