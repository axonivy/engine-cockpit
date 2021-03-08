package ch.ivyteam.enginecockpit.system.model;

import static ch.ivyteam.enginecockpit.util.DateUtil.formatDate;

import ch.ivyteam.ivy.cluster.restricted.ClusterNodeCommunicationState;
import ch.ivyteam.ivy.cluster.restricted.ClusterNodeState;
import ch.ivyteam.ivy.cluster.restricted.IClusterNode;
import ch.ivyteam.util.Version;

@SuppressWarnings("restriction")
public class ClusterNode
{

  private IClusterNode node;
  private String name;
  private String hostName;
  private String ipAddress;
  private int ipPort;
  private String jvmRouteName;
  private ClusterNodeState state;
  private ClusterNodeCommunicationState communicationState;
  private boolean master;
  private boolean local;
  private String lastStartTimestamp;
  private String lastStopTimestamp;
  private String lastFailTimestamp;
  private Version ivyVersion;
  private String operatingSystemName;
  private String operatingSystemVersion;
  private String operatingSystemArchitecture;
  private String javaVersion;
  private String javaVirtualMachineName;

  public ClusterNode(IClusterNode node)
  {
    this.node = node;
    if (node != null)
    {
      name = node.getName();
      hostName = node.getHostName();
      ipAddress = node.getIpAddress();
      ipPort = node.getIpPort();
      jvmRouteName = node.getJvmRouteName();
      state = node.getState();
      communicationState = node.getCommunicationState();
      master = node.isMaster();
      local = node.isLocal();
      lastStartTimestamp = formatDate(node.getLastStartTimestamp());
      lastStopTimestamp = formatDate(node.getLastStopTimestamp());
      lastFailTimestamp = formatDate(node.getLastFailTimestamp());
      ivyVersion = node.getIvyVersion();
      operatingSystemName = node.getOperatingSystemName();
      operatingSystemVersion = node.getOperatingSystemVersion();
      operatingSystemArchitecture = node.getOperatingSystemArchitecture();
      javaVersion = node.getJavaVersion();
      javaVirtualMachineName = node.getJavaVirtualMachineName();
    }
  }
  
  public String getName()
  {
    return name;
  }

  public String getHostName()
  {
    return hostName;
  }

  public String getIpAddress()
  {
    return ipAddress;
  }

  public int getIpPort()
  {
    return ipPort;
  }

  public String getJvmRouteName()
  {
    return jvmRouteName;
  }

  public ClusterNodeState getState()
  {
    return state;
  }

  public ClusterNodeCommunicationState getCommunicationState()
  {
    return communicationState;
  }

  public boolean isMaster()
  {
    return master;
  }

  public boolean isLocal()
  {
    return local;
  }

  public Version getIvyVersion()
  {
    return ivyVersion;
  }

  public String getOperatingSystemName()
  {
    return operatingSystemName;
  }

  public String getOperatingSystemVersion()
  {
    return operatingSystemVersion;
  }

  public String getOperatingSystemArchitecture()
  {
    return operatingSystemArchitecture;
  }

  public String getJavaVersion()
  {
    return javaVersion;
  }

  public String getJavaVirtualMachineName()
  {
    return javaVirtualMachineName;
  }

  public String getStateClass()
  {
    if (ClusterNodeState.DOWN.equals(state))
    {
      return "state-inactive";
    }
    else if (ClusterNodeState.RUNNING.equals(state))
    {
      return "state-active";
    }
    return "state-unknown";
  }
  
  public String getCommunicationStateClass()
  {
    if (ClusterNodeCommunicationState.DOWN.equals(communicationState))
    {
      return "state-inactive";
    }
    else if (ClusterNodeCommunicationState.UP.equals(communicationState))
    {
      return "state-active";
    }
    return "state-unknown";
  }
  
  public String getStartTimestamp()
  {
    return lastStartTimestamp;
  }
  
  public String getStopTimestamp()
  {
    return lastStopTimestamp;
  }
  
  public String getFailTimestamp()
  {
    return lastFailTimestamp;
  }
  
  public IClusterNode getNode()
  {
    return node;
  }
}
