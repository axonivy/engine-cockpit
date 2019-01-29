package ch.ivyteam.enginecockpit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.TabChangeEvent;

import com.google.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.TaskState;

@ManagedBean
@SessionScoped
public class ManagerBean
{
  private List<Application> applications;
  private int selectedApplicationIndex;

  @Inject
  private IApplicationConfigurationManager manager;

  public ManagerBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);

    applications = manager.getApplicationsSortedByName(false).stream()
            .map(app -> new Application(app))
            .collect(Collectors.toList());
  }

  public List<Application> getApplications()
  {
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

  public List<IApplication> getIApplicaitons()
  {
    return manager.getApplicationsSortedByName(false);
  }

  public int getSessionCount()
  {
    return manager.getApplications().get(0).getSecurityContext().getClusterSessionsSnapshot()
            .getSessionInfos().size();
  }

  public long getApplicationCount()
  {
    return manager.countApplications();
  }
  
  public int getUsersCount()
  {
    return getIApplicaitons().stream().mapToInt(app -> app.getSecurityContext().getUsers().size()).sum();
  }
  
  public long getTasksCount()
  {
    return Ivy.wf().getGlobalContext().getTaskQueryExecutor().createTaskQuery().where().state()
            .isEqual(TaskState.SUSPENDED).executor().count();
  }

  public Locale getDefaultEmailLanguageForSelectedApp()
  {
    return getSelectedIApplication().getDefaultEMailLanguage();
  }

  public List<SelectItem> getSupportedLanguages()
  {
    Locale appLanguage = getSelectedIApplication().getDefaultEMailLanguage();
    List<SelectItem> languages = new ArrayList<>();
    languages.add(new SelectItem("app", "Application default (" + appLanguage.getDisplayLanguage() + ")"));
    languages.addAll(manager.getLanguages().stream()
            .map(l -> new SelectItem(l.getLocale().getLanguage(), l.getLocale().getDisplayLanguage()))
            .collect(Collectors.toList()));
    return languages;
  }
  
  public boolean isIvySecuritySystem() 
  {
    return getSelectedIApplication().getSecurityContext().getExternalSecuritySystemProvider().getProviderName().equals("ivy Security System");
  }
}
