package ch.ivyteam.enginecockpit.fileupload;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class DeployOptionsBean
{
  private boolean deployTestUsers = false;
  private boolean overwriteProject = false;
  private String cleanup = "DISABLED";
  private List<String> cleanupValues = Arrays.asList(cleanup, "REMOVE_UNUSED", "REMOVE_ALL");
  private String version = "AUTO";
  private List<String> versions = Arrays.asList(version, "RELEASED"); //TODO: versions???
  private String state = "ACTIVE_AND_RELEASED";
  private List<String> states = Arrays.asList(state, "ACTIVE", "INACTIVE");
  private String fileFormat = "AUTO";
  private List<String> fileFormats = Arrays.asList(fileFormat, "PACKED", "EXPANDED");
  
  public boolean isDeployTestUsers()
  {
    return deployTestUsers;
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
  
}
