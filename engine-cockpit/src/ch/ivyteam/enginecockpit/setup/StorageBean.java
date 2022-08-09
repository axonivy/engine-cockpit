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
  private static final String FILE_DIR = DATA + "FilesDirectory";

  private String appDir;
  private String appDirHelp;
  private String fileDir;
  private String fileDirHelp;

  public StorageBean() {
    initConfigs();
    appDirHelp = IConfiguration.instance().getMetadata(APP_DIR).description().replaceAll("\n", "<br/>");
    fileDirHelp = IConfiguration.instance().getMetadata(FILE_DIR).description().replaceAll("\n", "<br/>");
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

  public String getFileDir() {
    return fileDir;
  }

  public void setFileDir(String fileDir) {
    this.fileDir = fileDir;
  }

  public String getFileDirHelp() {
    return fileDirHelp;
  }

  public void save() {
    setConfig(APP_DIR, appDir);
    setConfig(FILE_DIR, fileDir);
    showChangeMessage();
  }

  public void reset() {
    setConfig(APP_DIR, null);
    setConfig(FILE_DIR, null);
    showChangeMessage();
    initConfigs();
  }

  private void initConfigs() {
    appDir = IConfiguration.instance().getOrDefault(APP_DIR);
    fileDir = IConfiguration.instance().getOrDefault(FILE_DIR);
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
