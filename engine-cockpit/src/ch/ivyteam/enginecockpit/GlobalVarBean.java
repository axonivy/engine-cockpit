package ch.ivyteam.enginecockpit;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.ivy.application.restricted.IGlobalVariable;

@ManagedBean
@ViewScoped
public class GlobalVarBean
{
  private ManagerBean managerBean;
  private List<IGlobalVariable> globalVariables;
  private String filter;
  private SimpleVariable activeVar;

  public GlobalVarBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);

    activeVar = new SimpleVariable();
    reloadGlobalVars();
  }

  public void reloadGlobalVars()
  {
    activeVar = new SimpleVariable();
    globalVariables = managerBean.getSelectedIApplication().getGlobalVariables();
  }

  public List<IGlobalVariable> getGlobalVariables()
  {
    return globalVariables;
  }

  public String getFilter()
  {
    return filter;
  }

  public void setFilter(String filter)
  {
    this.filter = filter;
  }

  public void saveNewGlobalVar()
  {
    managerBean.getSelectedIApplication().createGlobalVariable(activeVar.getName(), activeVar.getDesc(),
            activeVar.getValue());
    activeVar = new SimpleVariable();
  }

  public void saveGlobalVar()
  {

  }

  public SimpleVariable getActiveVar()
  {
    return activeVar;
  }

  public static final class SimpleVariable
  {
    private String name;
    private String desc;
    private String value;

    public void setName(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public void setDesc(String desc)
    {
      this.desc = desc;
    }

    public String getDesc()
    {
      return desc;
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
