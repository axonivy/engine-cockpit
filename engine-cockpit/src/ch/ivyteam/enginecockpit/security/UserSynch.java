package ch.ivyteam.enginecockpit.security;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.security.ISecurityContext;

public class UserSynch {

  private final ISecurityContext securityContext;
  private String log;
  private final String preConfiguredUserName;
  private String userNameOnTheDialog;

  public UserSynch(ISecurityContext securityContext) {
    this(securityContext, null);
  }

  public UserSynch(ISecurityContext securityContext, String preConfiguredUserName) {
    this.securityContext = securityContext;
    this.preConfiguredUserName = preConfiguredUserName;
  }

  public void synch() {
    var result = securityContext.synchronizeUser(getUserName());
    log = result.getSynchLog();
  }

  public void reset() {
    log = null;
  }

  public String getLog() {
    return log;
  }

  public String getUserName() {
    if (StringUtils.isNotBlank(preConfiguredUserName)) {
      return preConfiguredUserName;
    }
    return userNameOnTheDialog;
  }

  public boolean isPreconfigured() {
    return preConfiguredUserName != null;
  }

  public void setUserName(String userName) {
    this.userNameOnTheDialog = userName;
  }
}
