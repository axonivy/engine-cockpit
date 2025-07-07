package ch.ivyteam.enginecockpit.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.system.model.ClusterNode;
import ch.ivyteam.ivy.cluster.restricted.IClusterManager;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class ClusterBean {

  private final boolean isClusterServer;
  private final List<ClusterNode> clusterNodes;
  private ClusterNode activeClusterNode;

  private String filter;
  private List<ClusterSessionDto> sessions;

  private final IClusterManager clusterManager = IClusterManager.instance();

  public ClusterBean() {
    isClusterServer = clusterManager.isClusterServer();
    clusterNodes = loadClusterNodes();
    activeClusterNode = new ClusterNode(null);
  }

  public boolean isClusterServer() {
    return isClusterServer;
  }

  private List<ClusterNode> loadClusterNodes() {
    return clusterManager.getClusterNodes().stream().map(ClusterNode::new)
        .collect(Collectors.toList());
  }

  public List<ClusterNode> getNodes() {
    return clusterNodes;
  }

  public void setActiveClusterNode(ClusterNode node) {
    activeClusterNode = node;
  }

  public ClusterNode getActiveClusterNode() {
    return activeClusterNode;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public List<ClusterSessionDto> getSessions() {
    if (sessions == null) {
      sessions = new ArrayList<>();
      for (var context : ISecurityContextRepository.instance().allWithSystem()) {
        for (var session : context.sessions().clusterSnapshot().getSessionInfos()) {
          var s = new ClusterSessionDto(context.getName(), session.getSessionUserName(), session.getClusterNode());
          sessions.add(s);
        }
      }
    }
    return sessions;
  }
  
  public static class ClusterSessionDto {
    
    private final String context;
    private final String userName;
    private final String nodeName;
    
    public ClusterSessionDto(String context, String userName, String nodeName) {
      this.context = context;
      this.userName = userName;
      this.nodeName = nodeName;
    }
    
    public String getContext() {
      return context;
    }
    
    public String getUserName() {
      return userName;
    }
    
    public String getNodeName() {
      return nodeName;
    }
  }
}
