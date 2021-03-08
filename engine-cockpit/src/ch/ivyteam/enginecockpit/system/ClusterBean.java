package ch.ivyteam.enginecockpit.system;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.system.model.ClusterNode;
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

  private IClusterManager clusterManager = IClusterManager.instance();

  public ClusterBean()
  {
    isClusterServer = clusterManager.isClusterServer();
    clusterNodes = loadClusterNodes();
    activeClusterNode = new ClusterNode(null);
  }

  public boolean isClusterServer()
  {
    return isClusterServer;
  }

  private List<ClusterNode> loadClusterNodes()
  {
    return clusterManager.getClusterNodes().stream().map(node -> new ClusterNode(node))
            .collect(Collectors.toList());
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
