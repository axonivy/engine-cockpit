package ch.ivyteam.enginecockpit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.google.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.model.ClusterNode;
import ch.ivyteam.ivy.cluster.restricted.IClusterManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class ClusterBean
{

  private boolean isClusterServer;
  private List<ClusterNode> clusterNodes;
  private ClusterNode activeClusterNode;
  
  private List<ClusterNode> filteredNodes;
  private String filter;
  
  @Inject
  private IClusterManager clusterManager;
  
  public ClusterBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
    isClusterServer = !clusterManager.isClusterServer();
    clusterNodes = loadClusterNodes();
    clusterNodes.addAll(clusterNodes);
    activeClusterNode = new ClusterNode(null);
  }
  
  public boolean isClusterServer()
  {
    return isClusterServer;
  }
  
  private List<ClusterNode> loadClusterNodes()
  {
    if (isClusterServer)
    {
      return clusterManager.getClusterNodes().stream().map(node -> new ClusterNode(node)).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
  
  public List<ClusterNode> getNodes()
  {
    return clusterNodes;
  }
  
  public void setActiveClusterNode(ClusterNode node)
  {
    activeClusterNode = node;
  }
  
  public ClusterNode getActiveClusterNode()
  {
    return activeClusterNode;
  }

  public List<ClusterNode> getFilteredNodes()
  {
    return filteredNodes;
  }
  
  public void setFilteredNodes(List<ClusterNode> filteredNodes)
  {
    this.filteredNodes = filteredNodes;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public void setFilter(String filter)
  {
    this.filter = filter;
  }
  
}
