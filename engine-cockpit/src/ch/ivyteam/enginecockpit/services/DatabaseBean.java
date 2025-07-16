package ch.ivyteam.enginecockpit.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.services.model.DatabaseDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.db.Databases;

@ManagedBean
@ViewScoped
public class DatabaseBean {

  private List<DatabaseDto> databases;
  private List<DatabaseDto> filteredDatabases;
  private String filter;

  private final ManagerBean managerBean;

  public DatabaseBean() {
    managerBean = ManagerBean.instance();
    reloadDatabases();
  }

  public void reloadDatabases() {
    databases = Databases.of(managerBean.getSelectedIApplication())
        .all().stream()
        .filter(db -> !Objects.equals(db.name(), "IvySystemDatabase"))
        .map(DatabaseDto::new)
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
