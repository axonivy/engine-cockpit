package ch.ivyteam.enginecockpit.system;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.persistence.db.DatabasePersistencyService;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.persistence.db.info.SystemDbIndex;
import ch.ivyteam.ivy.persistence.db.info.SystemDbInfo;
import ch.ivyteam.ivy.persistence.db.info.SystemDbTable;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SystemDatabaseInfoBean {
  private SystemDbInfo systemDbInfo;
  private List<SystemDbTable> filteredTables;
  private List<SystemDbIndex> filterIndexes;
  private String filterTable;
  private String filterIndex;

  public SystemDatabaseInfoBean() throws SQLException {
    var dbs = (DatabasePersistencyService) ISystemDatabasePersistencyService.instance();
      this.systemDbInfo = SystemDbInfo.getInfoFor(dbs);
      this.filteredTables = systemDbInfo.getTables();
      this.filterIndexes = systemDbInfo.getIndexes();
  }

  public List<SystemDbTable> getFilteredTables() {
    return filteredTables;
  }

  public void setFilteredTables(List<SystemDbTable> filteredTables) {
    this.filteredTables = filteredTables;
  }

  public List<SystemDbIndex> getFilteredIndexes() {
    return filterIndexes;
  }

  public void setFilteredIndexes(List<SystemDbIndex> filterIndexes) {
    this.filterIndexes = filterIndexes;
  }

  public String getFilterTable() {
    return filterTable;
  }

  public void setFilterTable(String filter) {
    this.filterTable = filter;
  }

  public String getFilterIndex() {
    return filterIndex;
  }

  public void setFilterIndex(String filter) {
    this.filterIndex = filter;
  }

  public String getSystemDbDatabaseProduct() {
    return this.systemDbInfo.getDatabaseProduct();
  }

  public String getSystemDbDatabaseVersion() {
    return this.systemDbInfo.getDatabaseVersion();
  }

  public String getSystemDbDriverName() {
    return this.systemDbInfo.getDriverName();
  }

  public String getSystemDbDriverVersion() {
    return this.systemDbInfo.getDriverVersion();
  }

  public String getSystemDbUrl() {
    return this.systemDbInfo.getDriverUrl();
  }

  public String getSystemDbUser() {
    return this.systemDbInfo.getUser();
  }

  public long getSystemDbDiskSize() throws SQLException {
    return this.systemDbInfo.getDiskSize();
  }

  public boolean isNaN(float number) {
    return Float.isNaN(number);
  }

  public List<String> getErrorMessages() throws SQLException {
    return this.systemDbInfo.getMessages();
  }
}
