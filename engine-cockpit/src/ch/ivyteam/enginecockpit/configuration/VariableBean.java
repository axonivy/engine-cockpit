package ch.ivyteam.enginecockpit.configuration;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.configuration.model.ConfigProperty;
import ch.ivyteam.enginecockpit.configuration.model.ConfigView;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.restricted.IEnvironment;
import ch.ivyteam.ivy.vars.Variables;

@ManagedBean
@ViewScoped
public class VariableBean implements ConfigView
{
  private ManagerBean managerBean;
  private List<ConfigProperty> variables;
  private List<ConfigProperty> filteredVariables;
  private String filter;
  private ConfigProperty activeVariable;
  private IApplication app;
  private IEnvironment env;

  public VariableBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);

    reloadVariables();
  }

  public void reloadVariables()
  {
    activeVariable = new ConfigProperty();
    if (managerBean.getApplications().size() != 0)
    {
      app = managerBean.getSelectedIApplication();
      env = managerBean.getSelectedIEnvironment();
      
      variables = variables().all().stream()
              .map(ConfigProperty::new)
              .collect(Collectors.toList());
    }
    filteredVariables = null;
  }

  @Override
  public List<ConfigProperty> getConfigs()
  {
    return variables;
  }

  @Override
  public List<ConfigProperty> getFilteredConfigs()
  {
    return filteredVariables;
  }

  @Override
  public void setFilteredConfigs(List<ConfigProperty> filteredConfigs)
  {
    this.filteredVariables = filteredConfigs;
  }

  @Override
  public String getFilter()
  {
    return filter;
  }

  @Override
  public void setFilter(String filter)
  {
    this.filter = filter;
  }

  @Override
  public void setActiveConfig(String configKey)
  {
    if (StringUtils.isNotBlank(configKey))
    {
      var variable = variables().variable(configKey);
      if (variable != null)
      {
        activeVariable = new ConfigProperty(variable);
        return;
      }
    }
    activeVariable = new ConfigProperty();
  }

  @Override
  public ConfigProperty getActiveConfig()
  {
    return activeVariable;
  }

  @Override
  public void resetConfig()
  {
    variables().reset(activeVariable.getKey());
    reloadAndUiMessage("reset to default");
    reloadVariables();
  }

  @Override
  public void saveConfig()
  {
    variables().set(activeVariable.getKey(), activeVariable.getValue());
    reloadAndUiMessage("saved");
    reloadVariables();
  }

  private void reloadAndUiMessage(String message)
  {
    FacesContext.getCurrentInstance().addMessage("msgs",
            new FacesMessage("'" + activeVariable.getKey() + "' " + message));
    reloadVariables();
  }
  
  public boolean isDefaultEnv()
  {
    return env.isDefault();
  }
  
  private Variables variables()
  {
    return Variables.of(app, env.getName());
  }
}
