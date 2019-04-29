package ch.ivyteam.enginecockpit.dashboad;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.util.Version;

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
    String version;
    if (Advisor.getAdvisor().isReleaseCandidate())
    {
      version = "dev";
    }
    else
    {
      version = Advisor.getAdvisor().getVersion().getVersionString(Version.DETAIL_PATCH, Version.FORM_SHORT);
    }
    return "https://developer.axonivy.com/doc/" + version + "/engine-guide";
  }
}
