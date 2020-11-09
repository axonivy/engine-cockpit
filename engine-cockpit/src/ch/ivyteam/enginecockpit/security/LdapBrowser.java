package ch.ivyteam.enginecockpit.security;

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

import ch.ivyteam.enginecockpit.model.LdapProperty;
import ch.ivyteam.naming.JndiConfig;

public class LdapBrowser
{

  public static final String DEFAULT_CONTEXT = "defaultContext";
  public static final String IMPORT_USERS_OF_GROUP = "importUsersOfGroup";
  
  private TreeNode root;
  private TreeNode selectedNode;
  private List<LdapProperty> selectedNodeAttributes;
  private JndiConfig jndiConfig;
  private boolean enableInsecureSsl;
  
  public void browse(JndiConfig config, boolean enableInsecureSsl)
  {
    this.jndiConfig = config;
    this.enableInsecureSsl = enableInsecureSsl;
    this.root = null;
    try(LdapBrowserContext context = new LdapBrowserContext(config, enableInsecureSsl))
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
      errorMessage(ex.getMessage());
    }
  }

  public void onNodeExpand(NodeExpandEvent event) {
    TreeNode node = event.getTreeNode();
    node.getChildren().clear();
    String name = evalLdapName(node);
    try(LdapBrowserContext context = new LdapBrowserContext(jndiConfig, enableInsecureSsl))
    {
      context.children(name).forEach(child -> addNewSubnode(node, child));
    }
    catch (NamingException ex)
    {
      errorMessage(ex.getMessage());
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
    if (selectedNode != null)
    {
      selectedNodeAttributes = getNodeArguments();
    }
  }
  
  public List<LdapProperty> getSelectedNodeAttributes()
  {
    return selectedNodeAttributes;
  }
  
  private List<LdapProperty> getNodeArguments()
  {
    try(LdapBrowserContext context = new LdapBrowserContext(jndiConfig, enableInsecureSsl))
    {
      return context.getAttributes(getSelectedLdapName());
    }
    catch (NamingException ex)
    {
      errorMessage(ex.getMessage());
    }
    return Collections.emptyList();
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
  

  private void errorMessage(String ex)
  {
    String message = ex;
    if (ex.contains("AcceptSecurityContext"))
    {
      message = "There seems to be a problem with your credentials.";
    }
    FacesContext.getCurrentInstance().addMessage("ldapBrowserMessage", 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    
  }
}
