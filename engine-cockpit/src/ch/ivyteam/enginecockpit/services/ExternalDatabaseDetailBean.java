package ch.ivyteam.enginecockpit.services;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.ExternalDatabase;

@ManagedBean
@ViewScoped
public class ExternalDatabaseDetailBean
{
  private ExternalDatabase externalDatabase;
  private String databaseName;
  
  private ManagerBean managerBean;
  
  public ExternalDatabaseDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public String getDatabaseName()
  {
    return databaseName;
  }
  
  public void setDatabaseName(String databaseName)
  {
    this.databaseName = databaseName;
    externalDatabase = new ExternalDatabase(managerBean.getSelectedIApplication().findExternalDatabaseConfiguration(databaseName));
  }
  
  public ExternalDatabase getExternalDatabase()
  {
    return externalDatabase;
  }
    
}
