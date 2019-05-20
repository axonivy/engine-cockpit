package ch.ivyteam.enginecockpit.fileupload;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class DeployOptionsBean
{
  private String deployTestUsers = "FALSE";
  private List<String> deployTestUsersValues = Arrays.asList(deployTestUsers, "TRUE", "AUTO");
  private boolean overwriteProject = false;
  private String cleanup = "DISABLED";
  private List<String> cleanupValues = Arrays.asList(cleanup, "REMOVE_UNUSED", "REMOVE_ALL");
  private String version = "AUTO";
  private List<String> versions = Arrays.asList(version, "RELEASED", "RANGE");
  private String state = "ACTIVE_AND_RELEASED";
  private List<String> states = Arrays.asList(state, "ACTIVE", "INACTIVE");
  private String fileFormat = "AUTO";
  private List<String> fileFormats = Arrays.asList(fileFormat, "PACKED", "EXPANDED");
  
  public String getDeployTestUsers()
  {
    return deployTestUsers;
  }
  
  public List<String> getDeployTestUsersValues()
  {
    return deployTestUsersValues;
  }
  
  public boolean isOverwriteProject()
  {
    return overwriteProject;
  }

  public String getCleanup()
  {
    return cleanup;
  }

  public List<String> getCleanupValues()
  {
    return cleanupValues;
  }

  public String getVersion()
  {
    return version;
  }

  public List<String> getVersions()
  {
    return versions;
  }

  public String getState()
  {
    return state;
  }

  public List<String> getStates()
  {
    return states;
  }

  public String getFileFormat()
  {
    return fileFormat;
  }
  
  public List<String> getFileFormats()
  {
    return fileFormats;
  }
  
  public void setDeployTestUsers(String deployTestUsers)
  {
    this.deployTestUsers = deployTestUsers;
  }

  public void setDeployTestUsersValues(List<String> deployTestUsersValues)
  {
    this.deployTestUsersValues = deployTestUsersValues;
  }

  public void setOverwriteProject(boolean overwriteProject)
  {
    this.overwriteProject = overwriteProject;
  }

  public void setCleanup(String cleanup)
  {
    this.cleanup = cleanup;
  }

  public void setCleanupValues(List<String> cleanupValues)
  {
    this.cleanupValues = cleanupValues;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

  public void setVersions(List<String> versions)
  {
    this.versions = versions;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public void setStates(List<String> states)
  {
    this.states = states;
  }

  public void setFileFormat(String fileFormat)
  {
    this.fileFormat = fileFormat;
  }
  
  public void setFileFormats(List<String> fileFormats)
  {
    this.fileFormats = fileFormats;
  }
  
}
