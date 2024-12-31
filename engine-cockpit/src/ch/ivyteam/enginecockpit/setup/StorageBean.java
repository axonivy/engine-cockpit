package ch.ivyteam.enginecockpit.setup;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class StorageBean extends StepStatus {
  private static final String DATA = "Data.";
  private static final String APP_DIR = DATA + "AppDirectory";
  private static final String DATA_DIR = DATA + "Directory";

  private String appDir;
  private final String appDirHelp;
  private String dataDir;
  private final String dataDirHelp;

  public StorageBean() {
    initConfigs();
    appDirHelp = IConfiguration.instance().getMetadata(APP_DIR).description().replace("\n", "<br/>");
    dataDirHelp = IConfiguration.instance().getMetadata(DATA_DIR).description().replace("\n", "<br/>");
  }

  public String getAppDir() {
    return appDir;
  }

  public void setAppDir(String appDir) {
    this.appDir = appDir;
  }

  public String getAppDirHelp() {
    return appDirHelp;
  }

  public String getDataDir() {
    return dataDir;
  }

  public void setDataDir(String dataDir) {
    this.dataDir = dataDir;
  }

  public String getdataDirHelp() {
    return dataDirHelp;
  }

  public void save() {
    setConfig(APP_DIR, appDir);
    setConfig(DATA_DIR, dataDir);
    showChangeMessage();
  }

  public void reset() {
    setConfig(APP_DIR, null);
    setConfig(DATA_DIR, null);
    showChangeMessage();
    initConfigs();
  }

  private void initConfigs() {
    appDir = IConfiguration.instance().getOrDefault(APP_DIR);
    dataDir = IConfiguration.instance().getOrDefault(DATA_DIR);
  }

  private void setConfig(String key, Object value) {
    IConfiguration.instance().set(key, value);
  }

  private void showChangeMessage() {
    FacesContext.getCurrentInstance().addMessage("",
        new FacesMessage(FacesMessage.SEVERITY_INFO, "Directory changes saved successfully", ""));
  }

  @Override
  public boolean isStepOk() {
    return StringUtils.isNotBlank(getAppDir());
  }

  @Override
  public String getStepWarningMessage() {
    return "AppDirectory shouldn't be empty";
  }

}
