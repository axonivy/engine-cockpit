package ch.ivyteam.enginecockpit.services;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.services.model.DatabaseDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.db.Databases;

@Named
@ViewScoped
public class DatabaseBean implements Serializable {

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
