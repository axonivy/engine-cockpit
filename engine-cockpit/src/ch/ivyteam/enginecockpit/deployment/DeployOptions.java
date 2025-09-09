package ch.ivyteam.enginecockpit.deployment;

import java.util.Arrays;
import java.util.List;

import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetState;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TargetVersion;
import ch.ivyteam.ivy.deployment.DeploymentOptions.TestUser;

public class DeployOptions {

  private TestUser deployTestUsers = TestUser.AUTO;
  private List<TestUser> deployTestUsersValues = Arrays.asList(TestUser.values());

  private TargetVersion version = TargetVersion.AUTO;
  private List<TargetVersion> versions = Arrays.asList(TargetVersion.values());

  private TargetState state = TargetState.ACTIVE_AND_RELEASED;
  private List<TargetState> states = Arrays.asList(TargetState.values());

  public TestUser getDeployTestUsers() {
    return deployTestUsers;
  }

  public List<TestUser> getDeployTestUsersValues() {
    return deployTestUsersValues;
  }

  public TargetVersion getVersion() {
    return version;
  }

  public Boolean isRangeVersion() {
    return TargetVersion.RANGE.equals(version);
  }

  public List<TargetVersion> getVersions() {
    return versions;
  }

  public TargetState getState() {
    return state;
  }

  public List<TargetState> getStates() {
    return states;
  }

  public void setDeployTestUsers(TestUser deployTestUsers) {
    this.deployTestUsers = deployTestUsers;
  }

  public void setDeployTestUsersValues(List<TestUser> deployTestUsersValues) {
    this.deployTestUsersValues = deployTestUsersValues;
  }

  public void setVersion(TargetVersion version) {
    this.version = version;
  }

  public void setVersions(List<TargetVersion> versions) {
    this.versions = versions;
  }

  public void setState(TargetState state) {
    this.state = state;
  }

  public void setStates(List<TargetState> states) {
    this.states = states;
  }
}
