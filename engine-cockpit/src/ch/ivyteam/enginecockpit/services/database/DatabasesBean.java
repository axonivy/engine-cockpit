package ch.ivyteam.enginecockpit.services.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.db.Databases;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class DatabasesBean implements Serializable {

  @Inject
  private ManagerBean managerBean;

  private List<DatabaseDto> databases;
  private List<DatabaseDto> filteredDatabases;
  private String filter;

  public void onload() {
    var app = managerBean.getSelectedApplication();
    if (app == null) {
      databases = new ArrayList<>();
      return;
    }
    databases = Databases.of(app).all().stream()
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
