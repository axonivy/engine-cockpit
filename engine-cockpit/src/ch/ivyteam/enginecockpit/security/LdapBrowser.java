package ch.ivyteam.enginecockpit.security;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Name;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.naming.JndiConfig;

public class LdapBrowser
{

  public static final String DEFAULT_CONTEXT = "defaultContext";
  public static final String IMPORT_USERS_OF_GROUP = "importUsersOfGroup";
  
  private TreeNode root;
  private TreeNode selectedNode;
  private JndiConfig jndiConfig;
  
  public void browse(JndiConfig config)
  {
    this.jndiConfig = config;
    this.root = null;
    try(LdapBrowserContext context = new LdapBrowserContext(config))
    {
      Name name = jndiConfig.getDefaultContextName();
      if (name.isEmpty())
      {
        root = new DefaultTreeNode(name, null);
        context.browse(name).forEach(node -> addNewSubnode(root, node));
        return;
      }
      root = new DefaultTreeNode("", null);
      addNewSubnode(root, context.createLdapNode(name, evalLdapName(root)));
    }
    catch (NamingException ex)
    {
      FacesContext.getCurrentInstance().addMessage("ldapBrowserMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", ex.getMessage()));
    }
  }

  public void onNodeExpand(NodeExpandEvent event) {
    TreeNode node = event.getTreeNode();
    node.getChildren().clear();
    String name = evalLdapName(node);
    try(LdapBrowserContext context = new LdapBrowserContext(jndiConfig))
    {
      context.children(name).forEach(child -> addNewSubnode(node, child));
    }
    catch (NamingException ex)
    {
      FacesContext.getCurrentInstance().addMessage("ldapBrowserMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", ex.getMessage()));
    }
  }
  
  @SuppressWarnings("unused")
  private void addNewSubnode(TreeNode treeNode, LdapBrowserNode ldapNode)
  {
    TreeNode node = new DefaultTreeNode(ldapNode, treeNode);
    if (ldapNode.isExpandable())
    {
      new DefaultTreeNode("loading...", node);
    }
  }
  
  public TreeNode getTree()
  {
    return root;
  }
  
  public TreeNode getSelectedNode() 
  {
    return selectedNode;
  }
  
  public void setSelectedNode(TreeNode selectedNode) 
  {
    this.selectedNode = selectedNode;
  }
  
  public String getSelectedLdapName()
  {
    return evalLdapName(selectedNode);
  }
  
  private String evalLdapName(TreeNode node)
  {
    if (node == null)
    {
      return "";
    }
    return node.toString() + evalParentName(node.getParent());
  }
  
  private String evalParentName(TreeNode parent)
  {
    if (parent != null && !StringUtils.isBlank(parent.toString()))
    {
      return "," + parent.toString() + evalParentName(parent.getParent());
    }
    return "";
  }
  
}
