package ch.ivyteam.enginecockpit.fileupload;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import ch.ivyteam.ivy.deployment.DeploymentOptions;
import ch.ivyteam.ivy.deployment.DeploymentOptions.Builder;
import ch.ivyteam.ivy.deployment.DeploymentOptions.Cleanup;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetFileFormat;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetState;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetVersion;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TestUser;

public class DeployOptionsJson
{
  private final Map<String, Object> options;
  private final Builder builder;
  
  public DeployOptionsJson()
  {
    this(Collections.emptyMap());
  }

  public DeployOptionsJson(Map<String, Object> options)
  {
    this.options = options;
    this.builder = new Builder();
  }
  
  public DeploymentOptions getDeploymentOptions()
  {
    deployTestUsers();
    overwriteConfiguration();
    cleanupConfiguration();
    targetVersion();
    targetVersionRange();
    targetState();
    targetFileFormat();
    return builder.toDeploymentOptions();
  }
  
  private void deployTestUsers()
  {
    Object obj = options.get("deployTestUsers");
    if (obj != null)
    {
      builder.deployTestUsers(TestUser.valueOf(obj.toString()));
    }
  }
  
  private void overwriteConfiguration()
  {
    Object obj = getConfiguration().get("overwrite");
    if (obj != null)
    {
      builder.overwriteConfiguration(BooleanUtils.toBoolean(obj.toString()));
    }
  }

  private void cleanupConfiguration()
  {
    Object obj = getConfiguration().get("cleanup");
    if (obj != null)
    {
      builder.cleanupConfiguration(Cleanup.valueOf(obj.toString()));
    }
  }
  
  private void targetVersion()
  {
    Object obj = getTarget().get("version");
    if (obj != null)
    {
      builder.targetVersion(TargetVersion.valueOf(obj.toString()));
    }
  }
  
  private void targetVersionRange()
  {
    Object obj = getTarget().get("versionRange");
    if (obj != null)
    {
      builder.targetVersionRange(new VersionRangeParser(obj.toString()).parse());
    }
  }
  
  private void targetState()
  {
    Object obj = getTarget().get("state");
    if (obj != null)
    {
      builder.targetState(TargetState.valueOf(obj.toString()));
    }
  }
  
  private void targetFileFormat()
  {
    Object obj = getTarget().get("fileFormat");
    if (obj != null)
    {
      builder.targetFileFormat(TargetFileFormat.valueOf(obj.toString()));
    }
  }
  
  private Map<String, Object> getConfiguration()
  {
    return getMap("configuration");
  }
  
  private Map<String, Object> getTarget()
  {
    return getMap("target");
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getMap(String key)
  {
    Map <String, Object> map = (Map <String, Object>) options.get(key);
    if (map != null)
    {
      return map;
    }
    return Collections.emptyMap();
  }

}
