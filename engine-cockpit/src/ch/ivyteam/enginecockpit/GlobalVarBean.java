package ch.ivyteam.enginecockpit;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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

  public GlobalVarBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);

    reloadGlobalVars();
  }

  public void reloadGlobalVars()
  {
    globalVariables = new ArrayList<>();
    activeVar = new SimpleVariable();
    app = managerBean.getSelectedIApplication();

    app.getGlobalVariables()
      .stream()
      .forEach(var -> globalVariables.add(new SimpleVariable(var.getName(), var.getDescription(), var.getValue())));
  }

  public List<SimpleVariable> getGlobalVariables()
  {
    return globalVariables;
  }

  public void saveNewGlobalVar()
  {
    if (app.findGlobalVariable(activeVar.name) == null)
    {
      app.createGlobalVariable(activeVar.getName(), activeVar.getDescription(), activeVar.getValue());
    }
    
    activeVar = new SimpleVariable();
  }

  public void updateGlobalVar()
  {
    app.deleteGlobalVariable(activeVar.name);
    saveNewGlobalVar();
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
      activeVar = new SimpleVariable(selectedVar.getName(), selectedVar.getDescription(), selectedVar.getValue());
    }
    
  }

  public static final class SimpleVariable
  {
    private String name;
    private String description;
    private String value;

    public SimpleVariable()
    {
      
    }
    
    public SimpleVariable(String name, String desc, String value)
    {
      this.name = name;
      this.description = desc;
      this.value = value;
    }
    
    public void setName(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public void setDescription(String desc)
    {
      this.description = desc;
    }

    public String getDescription()
    {
      return description;
    }

    public void setValue(String value)
    {
      this.value = value;
    }

    public String getValue()
    {
      return value;
    }
  }
}
