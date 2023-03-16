package ch.ivyteam.enginecockpit.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.Advisor;
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
    return "&copy; 2001 - " + Calendar.getInstance().get(Calendar.YEAR);
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
}
