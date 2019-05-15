package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.SimpleVariable;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.restricted.IGlobalVariable;

@ManagedBean
@ViewScoped
public class GlobalVarBean
{
  private ManagerBean managerBean;
  private List<SimpleVariable> globalVariables;
  private SimpleVariable activeVar;
  private IApplication app;
  private List<SimpleVariable> filteredVariables;

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
    
    globalVariables = app.getGlobalVariables().stream()
      .map(var -> new SimpleVariable(var.getName(), var.getDescription(), var.getValue(), app.getName()))
      .collect(Collectors.toList());
    filteredVariables = null;
  }

  public List<SimpleVariable> getGlobalVariables()
  {
    return globalVariables;
  }

  public void saveGlobalVar()
  {
    if (app.findGlobalVariable(activeVar.getName()) == null)
    {
      app.createGlobalVariable(activeVar.getName(), activeVar.getDescription(), activeVar.getValue());
      reloadAndUiMessage("saved");
    }
    reloadGlobalVars();
  }

  public void updateGlobalVar(String name)
  {
    IGlobalVariable var = app.findGlobalVariable(name);
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
    IGlobalVariable selectedVar = managerBean.getSelectedIApplication().findGlobalVariable(name);
    
    if (selectedVar == null)
    {
      activeVar = new SimpleVariable();
    }
    else
    {
      activeVar = new SimpleVariable(selectedVar.getName(), selectedVar.getDescription(), selectedVar.getValue(), app.getName());
    }
  }

  public void deleteGlobalVar(String name)
  {
    app.deleteGlobalVariable(name);
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