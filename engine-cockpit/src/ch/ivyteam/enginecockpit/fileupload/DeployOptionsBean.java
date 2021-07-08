package ch.ivyteam.enginecockpit.fileupload;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetFileFormat;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetState;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetVersion;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TestUser;

@ManagedBean
@ViewScoped
public class DeployOptionsBean
{
  private String deployTestUsers = TestUser.AUTO.name();
  private List<String> deployTestUsersValues = listNames(TestUser.values());
  
  private String version = TargetVersion.AUTO.name();
  private List<String> versions = listNames(TargetVersion.values());
  
  private String state = TargetState.ACTIVE_AND_RELEASED.name();
  private List<String> states = listNames(TargetState.values());
  
  private String fileFormat = TargetFileFormat.AUTO.name();
  private List<String> fileFormats = listNames(TargetFileFormat.values());
  
  private static List<String> listNames(Enum<?>[] vals)
  {
    return Arrays.stream(vals)
        .map(val -> val.name())
        .collect(Collectors.toList());
  }
  
  public String getDeployTestUsers()
  {
    return deployTestUsers;
  }
  
  public List<String> getDeployTestUsersValues()
  {
    return deployTestUsersValues;
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
