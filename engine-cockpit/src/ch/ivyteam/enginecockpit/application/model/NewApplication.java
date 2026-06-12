package ch.ivyteam.enginecockpit.application.model;

public class NewApplication {
    
  private String securityContextName;
  private String appName;
  private boolean isActive = true;

  public String getSecurityContextName() {
    return securityContextName;
  }

  public void setSecurityContextName(String securityContextName) {
    this.securityContextName = securityContextName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    this.isActive = active;
  }

}
