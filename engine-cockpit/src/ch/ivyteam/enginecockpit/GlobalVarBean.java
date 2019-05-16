package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.SimpleVariable;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.restricted.IEnvironment;
import ch.ivyteam.ivy.application.restricted.IGlobalVariable;

@ManagedBean
@ViewScoped
public class GlobalVarBean
{
  private ManagerBean managerBean;
  private List<SimpleVariable> globalVariables;
  private List<SimpleVariable> filteredVariables;
  private SimpleVariable activeVar;
  private IApplication app;
  private IEnvironment env;

  public GlobalVarBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);

    reloadGlobalVars();
  }

  public void reloadGlobalVars()
  {
    activeVar = new SimpleVariable();
    app = managerBean.getSelectedIApplication();
    env = managerBean.getSelectedIEnvironment();
    
    globalVariables = env.getGlobalVariables()
            .stream().map(var -> new SimpleVariable(var, app.getName())).collect(Collectors.toList());
    
    filteredVariables = null;
  }

  public List<SimpleVariable> getGlobalVariables()
  {
    return globalVariables;
  }

  public void saveGlobalVar()
  {
    if (env.findGlobalVariable(activeVar.getName()) != null)
    {
      reloadAndUiMessage("already exists");
      return;
    }
    
    env.createGlobalVariable(activeVar.getName(), activeVar.getDescription(), activeVar.getValue());
    reloadAndUiMessage("saved");
    reloadGlobalVars();
  }

  public void updateGlobalVar()
  {
    IGlobalVariable var = env.findGlobalVariable(activeVar.getName());
    if (var != null)
    {
      var.setValue(activeVar.getValue());
      reloadAndUiMessage("changed");
    }
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
    activeVar = new SimpleVariable(selectedVar, app.getName());
  }

  public void deleteGlobalVar()
  {
    env.deleteGlobalVariable(activeVar.getName());
    reloadAndUiMessage("deleted");
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
}