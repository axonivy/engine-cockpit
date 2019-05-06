package ch.ivyteam.enginecockpit.dashboad;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.Advisor;

@ManagedBean
@ApplicationScoped
@SuppressWarnings("restriction")
public class AdvisorBean
{
  public String getApplicationName()
  {
    return Advisor.getAdvisor().getApplicationName();
  }

  public String getVersion()
  {
    return Advisor.getAdvisor().getVersion().toString();
  }

  public String getBuildDate()
  {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return formatter.format(Advisor.getAdvisor().getBuildDate());
  }

  public String getCopyright()
  {
    return "&copy; 2001 - " + Calendar.getInstance().get(Calendar.YEAR);
  }

  public String getEngineGuideBaseUrl()
  {
    return UrlUtil.getEngineGuideBaseUrl();
  }
}
