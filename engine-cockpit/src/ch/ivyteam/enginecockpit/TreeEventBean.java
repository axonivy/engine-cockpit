package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;

@ManagedBean
@RequestScoped
public class TreeEventBean
{
  public void nodeExpand(NodeExpandEvent event) 
  {
    event.getTreeNode().setExpanded(true);
  }
  
  public void nodeCollapse(NodeCollapseEvent event) 
  {
    event.getTreeNode().setExpanded(false);
  }
}
