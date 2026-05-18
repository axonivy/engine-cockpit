package ch.ivyteam.enginecockpit.deployment;

import java.util.Arrays;
import java.util.List;

import ch.ivyteam.ivy.deployment.DeploymentOptions.TestUser;

public class DeployOptions {

  private TestUser deployTestUsers = TestUser.AUTO;
  private List<TestUser> deployTestUsersValues = Arrays.asList(TestUser.values());

  public TestUser getDeployTestUsers() {
    return deployTestUsers;
  }

  public List<TestUser> getDeployTestUsersValues() {
    return deployTestUsersValues;
  }

  public void setDeployTestUsers(TestUser deployTestUsers) {
    this.deployTestUsers = deployTestUsers;
  }

  public void setDeployTestUsersValues(List<TestUser> deployTestUsersValues) {
    this.deployTestUsersValues = deployTestUsersValues;
  }
}
