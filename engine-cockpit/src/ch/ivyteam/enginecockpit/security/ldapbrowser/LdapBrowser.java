package ch.ivyteam.enginecockpit.security.ldapbrowser;

import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Name;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.log.Logger;
import ch.ivyteam.naming.JndiConfig;

public class LdapBrowser {

  private final static Logger LOGGER = Logger.getLogger(LdapBrowser.class);

  public static final String DEFAULT_CONTEXT = "defaultContext";
  public static final String IMPORT_USERS_OF_GROUP = "importUsersOfGroup";

  private TreeNode<LdapBrowserNode> root;
  private TreeNode<LdapBrowserNode> selectedNode;
  private List<Property> selectedNodeAttributes;
  private JndiConfig jndiConfig;
  private boolean insecureSsl;

  public void browse(JndiConfig config, boolean enableInsecureSsl, String initialValue) {
    try (var context = new LdapBrowserContext(config, enableInsecureSsl)) {
      this.jndiConfig = config;
      this.insecureSsl = enableInsecureSsl;
      this.root = new DefaultTreeNode<LdapBrowserNode>(null, null);
      Name initialName = parseInitialName(context, initialValue);
      var name = jndiConfig.getDefaultContextName();
      if (name.isEmpty()) {
        context.browse(name).forEach(node -> addNewSubnode(root, node, initialName));
        return;
      }
      var displayName = context.toDisplayName(name);
      addNewSubnode(root, context.createLdapNode(displayName, name), initialName);
    } catch (NamingException ex) {
      errorMessage(ex);
    }
  }

  private static Name parseInitialName(LdapBrowserContext context, String initialValue) throws NamingException {
    if (StringUtils.isBlank(initialValue)) {
      return null;
    }
    return context.parse(initialValue);
  }

  @SuppressWarnings("unchecked")
  public void onNodeExpand(NodeExpandEvent event) {
    var node = event.getTreeNode();
    node.getChildren().clear();
    loadChildren(node, null);
  }

  private void loadChildren(TreeNode<LdapBrowserNode> node, Name initialValue) {
    var name = node.getData().getName();
    try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
      context.children(name).forEach(child -> addNewSubnode(node, child, initialValue));
    } catch (NamingException ex) {
      errorMessage(ex);
    }
  }

  @SuppressWarnings("unused")
  private void addNewSubnode(TreeNode<LdapBrowserNode> parent, LdapBrowserNode ldapNode, Name initialValue) {
    var node = new DefaultTreeNode<>(ldapNode, parent);
    if (initialValue != null && initialValue.startsWith(ldapNode.getName())) {
      if (initialValue.equals(ldapNode.getName())) {
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

  public TreeNode<LdapBrowserNode> getTree() {
    return root;
  }

  public TreeNode<LdapBrowserNode> getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(TreeNode<LdapBrowserNode> selectedNode) {
    this.selectedNode = selectedNode;
    if (selectedNode != null) {
      selectedNodeAttributes = getNodeAttributes();
    }
  }

  public String getSelectedNameString() {
    var name = getSelectedName();
    if (name == null) {
      return null;
    }
    return name.toString();
  }

  private Name getSelectedName() {
    if (selectedNode == null) {
      return null;
    }
    return selectedNode.getData().getName();
  }


  public List<Property> getSelectedNodeAttributes() {
    return selectedNodeAttributes;
  }

  private List<Property> getNodeAttributes() {
    try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
      return context.getAttributes(getSelectedName());
    } catch (NamingException ex) {
      errorMessage(ex);
    }
    return Collections.emptyList();
  }

  private void errorMessage(Exception ex) {
    LOGGER.error("Error in LDAP call", ex);
    var message = ex.getMessage();
    if (StringUtils.contains(message, "AcceptSecurityContext")) {
      message = "There seems to be a problem with your credentials.";
    }
    FacesContext.getCurrentInstance().addMessage("ldapBrowserMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
  }
}
