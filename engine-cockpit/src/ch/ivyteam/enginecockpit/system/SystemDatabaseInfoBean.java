package ch.ivyteam.enginecockpit.system;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.trace.BackgroundMeterUtil;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.LongValueFormatter;
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
  private List<SystemDbTable> tables;
  private List<SystemDbIndex> indexes;
  private List<SystemDbTable> filteredTables;
  private List<SystemDbIndex> filteredIndexes;
  private String filterTable;
  private String filterIndex;
  private LongValueFormatter scaleValue = new LongValueFormatter(4);

  public void loadData() throws SQLException {
    var dbs = (DatabasePersistencyService) ISystemDatabasePersistencyService.instance();
    this.systemDbInfo = SystemDbInfo.getInfoFor(dbs);
    this.tables = systemDbInfo.getTables();
    this.indexes = systemDbInfo.getIndexes();
  }

  public String formatByteValue (long size) {
    if (size != Long.MIN_VALUE && size != 0) {
      return scaleValue.format(size, Unit.BYTES);
    }
    return "n.a.";
  }

  public List<SystemDbTable> getFilteredTables() {
    return filteredTables;
  }

  public void setFilteredTables(List<SystemDbTable> filteredTable) {
    this.filteredTables = filteredTable;
  }

  public List<SystemDbIndex> getIndexes() {
    return indexes;
  }

  public List<SystemDbTable> getTables() {
    return tables;
  }

  public List<SystemDbIndex> getFilteredIndexes() {
    return filteredIndexes;
  }

  public void setFilteredIndexes(List<SystemDbIndex> filteredIndex) {
    this.filteredIndexes = filteredIndex;
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

  public List<String> getErrorMessages() {
    return this.systemDbInfo.getMessages();
  }

  public String backgroundRow(long rows) {
    var maxRows = tables.stream().mapToLong(i -> i.getRows()).max().orElse(rows);
    var background = BackgroundMeterUtil.background(rows, maxRows);
    return "background: " + background + ";";
  }

  public String backgroundFragmentation(float fragmentation) {
    var maxFragmentation = 100.0;
    var background = BackgroundMeterUtil.background(fragmentation, maxFragmentation);
    return "background: " + background + ";";
  }

  public String backgroundTableDiskSize(long diskSize) {
    return backgroundDiskSize(diskSize, tables.stream().mapToLong(i -> i.getDiskSize()).sum());
  }

  public String backgroundIndexDiskSize(long diskSize) {
    return backgroundDiskSize(diskSize, indexes.stream().mapToLong(i -> i.getDiskSize()).sum());
  }

  private String backgroundDiskSize(long diskSize, long maxDiskSize) {
    if (diskSize != Long.MIN_VALUE) {
      var background = BackgroundMeterUtil.background(diskSize, maxDiskSize);
      return "background: " + background + ";";
    } else {
      return "";
    }
  }
}
