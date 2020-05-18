package ch.ivyteam.enginecockpit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.ConfigFile;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class EditorBean
{
  private List<ConfigFile> configFiles = new ArrayList<>();
  
  private ConfigFile activeConfigFile;
  
  private ManagerBean managerBean;

  public EditorBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    File ivyYaml = UrlUtil.getConfigFile("ivy.yaml");
    configFiles.add(new ConfigFile(ivyYaml, IConfiguration.instance()));
    configFiles.addAll(managerBean.getIApplications().stream()
            .map(this::createAppConfigFile)
            .collect(Collectors.toList()));
  }
  
  private ConfigFile createAppConfigFile(IApplication app)
  {
    File appYaml = UrlUtil.getConfigFile("app-" + app.getName() + ".yaml");
    return new ConfigFile(appYaml, ((IApplicationInternal) app).getConfiguration());
  }
  
  public List<ConfigFile> getConfigFiles()
  {
    return configFiles;
  }
  
  public ConfigFile getActiveConfigFile()
  {
    return activeConfigFile;
  }
  
  public void setActiveConfigFile(ConfigFile activeConfigFile)
  {
    this.activeConfigFile = activeConfigFile;
  }
  
}
