package ch.ivyteam.enginecockpit.system;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@ManagedBean
@SessionScoped
public class AdvisorBean {

  public String getApplicationName() {
    return Advisor.getAdvisor().getApplicationName();
  }

  public String getVersion() {
    return Advisor.getAdvisor().getVersion().toString();
  }

  public String getBuildDate() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return formatter.format(Advisor.getAdvisor().getBuildDate());
  }

  public String getCopyright() {
    return Ivy.cm().co("/axonivy/CopyrightValue") + Calendar.getInstance().get(Calendar.YEAR);
  }

  public String getApiBrowserUrl() {
    return systemPath() + "/api-browser";
  }

  /**
   * only used for deployment rest endpoint
   */
  public String getApiBaseUrl() {
    return systemPath() + "/api";
  }

  private String systemPath() {
    return ISecurityContextRepository.instance().getSystem().contextPath();
  }

  public String getEngineGuideBaseUrl() {
    return UrlUtil.getEngineGuideBaseUrl();
  }

  public String getCockpitEngineGuideUrl() {
    return UrlUtil.getCockpitEngineGuideUrl();
  }

  public String getDesignerGuideBaseUrl() {
    return UrlUtil.getDesignerGuideBaseUrl();
  }

  public String getInstallationDirectory() {
    return Paths.get("").toAbsolutePath().toString();
  }

  public String getRestartWarningMessage() {
    return Ivy.cms().co("/restart/RestartWarningMessage", Arrays.asList(getApplicationName()));
  }

  public String getRestartWarningLink(long workingUsers) {
    return Ivy.cms().co("/restart/RestartFirstWarningMessageSecondPart", Arrays.asList(workingUsers));
  }

  public String getRestartFirstWarningMessageThirdPart() {
    return Ivy.cms().co("/restart/RestartFirstWarningMessageThirdPart", Arrays.asList(getApplicationName()));
  }

  public String getRestartSecondWarningMessage() {
    return Ivy.cms().co("/restart/RestartSecondWarningMessage", Arrays.asList(getApplicationName()));
  }

  public String getRestartConfirmMessage() {
    return Ivy.cms().co("/restart/RestartConfirmMessage", Arrays.asList(getApplicationName()));
  }
}
