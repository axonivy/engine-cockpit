package ch.ivyteam.enginecockpit.fileupload;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import ch.ivyteam.ivy.application.value.VersionRange;
import ch.ivyteam.ivy.deployment.DeploymentOptions;
import ch.ivyteam.ivy.deployment.DeploymentOptions.Cleanup;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetFileFormat;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetState;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetVersion;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TestUser;
import ch.ivyteam.ivy.deployment.internal.auto.VersionRangeParser;

@SuppressWarnings("restriction")
public class DeployOptionsJson
{
  private final Map<String, Object> options;

  public DeployOptionsJson(Map<String, Object> options)
  {
    this.options = options;
  }
  
  public DeploymentOptions getDeploymentOptions()
  {
    return new DeploymentOptions.Builder()
            .deployTestUsers(getDeployTestUsers())
            .overwriteConfiguration(getOverwriteConfiguration())
            .cleanupConfiguration(getCleanupConfiguration())
            .targetVersion(getTargetVersion())
            .targetVersionRange(getTargetVersionRange())
            .targetState(getTargetState())
            .targetFileFormat(getTargetFileFormat())
            .toDeploymentOptions();
  }
  
  public TestUser getDeployTestUsers()
  {
    return TestUser.valueOf(options.get("deployTestUsers").toString());
  }
  
  public boolean getOverwriteConfiguration()
  {
    return BooleanUtils.toBoolean(getConfiguration().get("overwrite").toString());
  }
  
  public Cleanup getCleanupConfiguration()
  {
    return Cleanup.valueOf(getConfiguration().get("cleanup").toString());
  }
  
  private TargetVersion getTargetVersion()
  {
    return TargetVersion.valueOf(getTarget().get("version").toString());
  }
  
  private VersionRange getTargetVersionRange()
  {
    return new VersionRangeParser(getTarget().get("versionRange").toString()).parse();
  }
  
  public TargetState getTargetState()
  {
    return TargetState.valueOf(getTarget().get("state").toString());
  }

  public TargetFileFormat getTargetFileFormat()
  {
    return TargetFileFormat.valueOf(getTarget().get("fileFormat").toString());
  }
  
  @SuppressWarnings("unchecked")
  private Map<String, Object> getConfiguration()
  {
    return (Map <String, Object>) options.get("configuration");
  }
  
  @SuppressWarnings("unchecked")
  private Map<String, Object> getTarget()
  {
    return (Map <String, Object>) options.get("target");
  }



}
