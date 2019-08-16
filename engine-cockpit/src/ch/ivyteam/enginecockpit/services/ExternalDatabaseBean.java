package ch.ivyteam.enginecockpit.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.ExternalDatabase;

@ManagedBean
@ViewScoped
public class ExternalDatabaseBean
{
  private List<ExternalDatabase> externalDatabases;
  private List<ExternalDatabase> filteredExternalDatabases;
  private String filter;
  
  private ManagerBean managerBean;
  
  public ExternalDatabaseBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadExternalDatabases();
  }

  public void reloadExternalDatabases()
  {
    externalDatabases = managerBean.getSelectedIEnvironment().getExternalDatabaseConfigurations().stream()
            .filter(db -> !StringUtils.equals(db.getUserFriendlyName(), "IvyDatabase"))
            .map(db -> new ExternalDatabase(db))
            .collect(Collectors.toList());
  }
  
  public List<ExternalDatabase> getExternalDatabases()
  {
    return externalDatabases;
  }
  
  public List<ExternalDatabase> getFilteredExternalDatabases()
  {
    return filteredExternalDatabases;
  }

  public void setFilteredExternalDatabases(List<ExternalDatabase> filteredExternalDatabases)
  {
    this.filteredExternalDatabases = filteredExternalDatabases;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public void setFilter(String filter)
  {
    this.filter = filter;
  }
  
  
}
