package ch.ivyteam.enginecockpit.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.services.model.DatabaseDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.db.Databases;

@ManagedBean
@ViewScoped
public class DatabaseBean {
  private List<DatabaseDto> databases;
  private List<DatabaseDto> filteredDatabases;
  private String filter;

  private ManagerBean managerBean;

  public DatabaseBean() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadDatabases();
  }

  public void reloadDatabases() {
    databases = Databases.of(managerBean.getSelectedIApplication(), managerBean.getSelectedEnvironment())
            .all().stream()
            .filter(db -> !StringUtils.equals(db.name(), "IvySystemDatabase"))
            .map(db -> new DatabaseDto(db))
            .collect(Collectors.toList());
  }

  public List<DatabaseDto> getDatabases() {
    return databases;
  }

  public List<DatabaseDto> getFilteredDatabases() {
    return filteredDatabases;
  }

  public void setFilteredDatabases(List<DatabaseDto> filteredDatabases) {
    this.filteredDatabases = filteredDatabases;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

}
