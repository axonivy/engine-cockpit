package ch.ivyteam.enginecockpit.services.database;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.db.Databases;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class DatabasesBean implements Serializable {

  private List<DatabaseDto> databases;
  private List<DatabaseDto> filteredDatabases;
  private String filter;

  public void onload() {
    databases = Databases.of(ManagerBean.instance().getSelectedIApplication())
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
