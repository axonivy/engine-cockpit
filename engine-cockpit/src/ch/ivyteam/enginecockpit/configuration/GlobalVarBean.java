package ch.ivyteam.enginecockpit.configuration;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SimpleVariable;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.restricted.IEnvironment;
import ch.ivyteam.ivy.application.restricted.IGlobalVariable;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class GlobalVarBean
{
  private ManagerBean managerBean;
  private List<SimpleVariable> globalVariables;
  private List<SimpleVariable> filteredVariables;
  private SimpleVariable activeVar;
  private IApplication app;
  private IEnvironment env;
  private IConfiguration configuration;

  public GlobalVarBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);

    reloadGlobalVars();
  }

  public void reloadGlobalVars()
  {
    activeVar = new SimpleVariable();
    if (managerBean.getApplications().size() != 0)
    {
      configuration = ((IApplicationInternal) managerBean.getSelectedIApplication()).getConfiguration();
      app = managerBean.getSelectedIApplication();
      env = managerBean.getSelectedIEnvironment();
      globalVariables = env.getGlobalVariables()
              .stream().map(var -> new SimpleVariable(var, app)).collect(Collectors.toList());
    }
    filteredVariables = null;
  }

  public List<SimpleVariable> getGlobalVariables()
  {
    return globalVariables;
  }

  public void saveGlobalVar()
  {
    configuration.set(SimpleVariable.GLOBAL_VARIABLES + activeVar.getName(), activeVar.getValue());
    reloadAndUiMessage("saved");
    reloadGlobalVars();
  }

  public SimpleVariable getActiveVar()
  {
    return activeVar;
  }

  public void setActiveVar(String name)
  {
    IGlobalVariable selectedVar = env.findGlobalVariable(name);
    
    if (selectedVar == null)
    {
      activeVar = new SimpleVariable();
      return;
    }
    activeVar = new SimpleVariable(selectedVar, app);
  }

  public void resetGlobalVar()
  {
    configuration.remove(SimpleVariable.GLOBAL_VARIABLES + activeVar.getName());
    reloadAndUiMessage("reset to default");
    reloadGlobalVars();
  }
  
  public List<SimpleVariable> getFilteredVariables()
  {
    return filteredVariables;
  }
  
  public void setFilteredVariables(List<SimpleVariable> filteredVariables)
  {
    this.filteredVariables = filteredVariables;
  }

  private void reloadAndUiMessage(String message)
  {
    FacesContext.getCurrentInstance().addMessage("msgs",
            new FacesMessage("'" + activeVar.getName() + "' " + message));
  }
  
  public boolean isDefaultEnv()
  {
    return env.isDefault();
  }
}