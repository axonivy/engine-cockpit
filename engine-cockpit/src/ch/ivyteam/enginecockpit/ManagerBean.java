package ch.ivyteam.enginecockpit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;

import com.google.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.restricted.IEnvironment;
import ch.ivyteam.ivy.security.ISecurityManager;

@ManagedBean
@SessionScoped
public class ManagerBean
{
  private List<Application> applications = Collections.emptyList();
  private int selectedApplicationIndex;
  
  private Map<Long, List<String>> environments = new HashMap<>();
  private String selectedEnvironment;

  @Inject
  private IApplicationConfigurationManager manager;
  
  @Inject
  private ISecurityManager securityManager;
  
  public ManagerBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
    reloadApplications();
  }
  
  public void reloadEnvironments()
  {
    if (!applications.isEmpty())
    {
      if (StringUtils.isBlank(selectedEnvironment))
      {
        selectedEnvironment = StringUtils.defaultString(getSelectedIApplication().getActiveEnvironment(), IEnvironment.DEFAULT_ENVIRONMENT_NAME);
      }
      for (IApplication iApplication : getIApplications())
      {
        environments.put(iApplication.getId(), iApplication.getEnvironmentsSortedByName().stream().map(e -> e.getName()).collect(Collectors.toList()));
      }
    }
  }

  public void reloadApplications()
  {
    int appCount = applications.size();
    applications = getIApplications().stream()
            .map(app -> new Application(app))
            .collect(Collectors.toList());
    if (selectedApplicationIndex != 0 && appCount != applications.size())
    {
      selectedApplicationIndex = 0;
    }
    reloadEnvironments();
  }
  
  public List<Application> getApplications()
  {
    reloadApplications();
    return applications;
  }

  public int getSelectedApplicationIndex()
  {
    return selectedApplicationIndex;
  }

  public void setSelectedApplicationIndex(int index)
  {
    selectedApplicationIndex = index;
  }

  public void updateSelectedApplication(TabChangeEvent event)
  {
    setSelectedApplicationIndex(0);
    for (Application app : applications)
    {
      if (app.getName().equals(event.getTab().getTitle()))
      {
        setSelectedApplicationIndex(applications.indexOf(app));
      }
    }
  }
  
  public void setSelectedApplicationName(String appName) 
  {
    for (int i = 0; i < applications.size(); i++)
    {
      if (applications.get(i).getName().equals(appName))
      {
        setSelectedApplicationIndex(i);
        return;
      }
    }
  }
  
  public String getSelectedApplicationName()
  {
    if (applications.isEmpty())
    {
      return "";
    }
    return getSelectedApplication().getName();
  }
  
  public IApplicationConfigurationManager getManager() {
    return manager;
  }

  public Application getSelectedApplication()
  {
    return applications.get(selectedApplicationIndex);
  }

  public IApplication getSelectedIApplication()
  {
    return manager.getApplication(getSelectedApplication().getId());
  }

  public IApplication getIApplication(long id)
  {
    return manager.getApplication(id);
  }

  public List<IApplication> getIApplications()
  {
    return manager.getApplicationsSortedByName(false);
  }

  public long getSessionCount()
  {
    return securityManager.getSessionCount();
  }
  
  public long getApplicationCount()
  {
    return getIApplications().size();
  }
  
  public long getUsersCount()
  {
    return getIApplications().stream().mapToLong(app -> app.getSecurityContext().getUsers().size() - 1).sum();
  }
  
  public long getRunningCasesCount()
  {
    return getApplications().stream().mapToLong(a -> a.getRunningCasesCount()).sum();
  }

  public Locale getDefaultEmailLanguageForSelectedApp()
  {
    return getSelectedIApplication().getDefaultEMailLanguage();
  }

  public List<SelectItem> getSupportedLanguagesWithDefault()
  {
    Locale appLanguage = getSelectedIApplication().getDefaultEMailLanguage();
    List<SelectItem> languages = new ArrayList<>();
    languages.add(new SelectItem("app", "Application default (" + appLanguage.getDisplayLanguage() + ")"));
    languages.addAll(getSupportedLanguages());
    return languages;
  }
  
  public List<SelectItem> getSupportedLanguages()
  {
    //Fix for PortalKit (creates a second german language)
    return manager.getLanguages().stream()
            .map(l -> l.getLocale())
            .collect(Collectors.toMap(Locale::getLanguage, l -> l, (l1, l2) -> l1)).values().stream()
            .map(l -> new SelectItem(l.getLanguage(), l.getDisplayLanguage()))
            .collect(Collectors.toList());
  }
  
  public boolean isIvySecuritySystem() 
  {
    return getSelectedIApplication().getSecurityContext().getExternalSecuritySystemProvider().getProviderName().equals("ivy Security System");
  }
  
  public List<String> getEnvironments()
  {
    return environments.get(getSelectedApplication().getId());
  }
  
  public void setSelectedEnvironment(String environment)
  {
    selectedEnvironment = environment;
  }
  
  public String getSelectedEnvironment()
  {
    if (environments.get(getSelectedApplication().getId()).contains(selectedEnvironment))
    {
      return selectedEnvironment;
    }
    return IEnvironment.DEFAULT_ENVIRONMENT_NAME;
  }
  
  public IEnvironment getSelectedIEnvironment()
  {
    return getSelectedIApplication().findEnvironment(getSelectedEnvironment());
  }
}
