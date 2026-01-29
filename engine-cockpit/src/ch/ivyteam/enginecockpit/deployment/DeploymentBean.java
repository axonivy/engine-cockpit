package ch.ivyteam.enginecockpit.deployment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.file.UploadedFile;

import ch.ivyteam.io.TemporaryDirectory;
import ch.ivyteam.ivy.deployment.DeployLog;
import ch.ivyteam.ivy.deployment.Deployable;
import ch.ivyteam.ivy.deployment.DeploymentOptions;
import ch.ivyteam.ivy.deployment.DeploymentRunner;
import ch.ivyteam.ivy.deployment.log.impl.Log4j2DeploymentLogger;
import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
public class DeploymentBean {

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".iar", ".zip");
  private static final String ALLOWED_EXTENSIONS_TEXT = String.join(", ", ALLOWED_EXTENSIONS);

  private DeployOptions deployOptions = new DeployOptions();
  private CockpitDeploymentLogger deploymentLogger;
  private boolean showDeployOptions = false;
  private boolean showDeployLogs = false;
  private UploadedFile file;
  private Path uploadedFile;

  private String appNameFromView;
  private String appVersionFromView;
  private final DeploymentStatus status = new DeploymentStatus();

  public String getDropZoneText() {
    if (this.uploadedFile == null) {
      return Ivy.cm().co("/fileUpload/AcceptFilesPlaceholder") + " " + getAllowedExtensionsText();
    }
    var uploaded = uploadedFile.toFile();
    return uploaded.getName() + " (" + prettyFileSize(uploaded.length()) + ")";
  }

  public void setAppNameAndVersion(String appName, String appVersion) {
    this.appNameFromView = appName;
    this.appVersionFromView = appVersion;
  }

  public UploadedFile getFile() {
    return this.file;
  }

  public void setFile(UploadedFile file) {
    if (file == null) {
      return;
    }
    var originalFileName = file.getFileName();

    if (!hasAllowedExtension(originalFileName)) {
      var title = Ivy.cm().co("/deployment/errors/InvalidFile/title");
      var detail = Ivy.cm().co("/deployment/errors/InvalidFile/detail");
      FacesContext.getCurrentInstance().addMessage("deploymentMessages",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, title, detail + getAllowedExtensionsText()));
      return;
    }

    try (var in = file.getInputStream()) {
      var safeName = Path.of(originalFileName).getFileName().toString();
      var tempDir = Files.createTempDirectory("engine-cockpit-deploy-");
      var target = tempDir.resolve(safeName);
      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
      this.uploadedFile = target;
    } catch (IOException ex) {
      var title = Ivy.cm().co("/deployment/errors/UploadError/title");
      var detail = Ivy.cm().co("/deployment/errors/UploadError/detail");
      FacesContext.getCurrentInstance().addMessage("deploymentMessages",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, title, detail + ex.getMessage()));
    }
  }

  public void uploadAndDeploy() {
    if (this.uploadedFile == null) {
      this.status.error();
      return;
    }
    if (!Files.exists(this.uploadedFile)) {
      this.uploadedFile = null;
      this.status.error();
      return;
    }
    deploy();
  }

  private void deploy() {
    this.deploymentLogger = new CockpitDeploymentLogger();
    this.showDeployLogs = true;
    this.status.running();
    var log = DeployLog.of(new Log4j2DeploymentLogger(), deploymentLogger);

    var deploymentOptions = DeploymentOptions.create()
        .deployTestUsers(deployOptions.getDeployTestUsers())
        .targetState(deployOptions.getState())
        .toOptions();

    var projectDeployable = Deployable.create()
        .appName(this.appNameFromView)
        .appVersion(Integer.parseInt(appVersionFromView))
        .fileToDeploy(this.uploadedFile)
        .deployOptions(deploymentOptions)
        .toDeployable();

    try {
      DeploymentRunner.create().log(log).deploy(projectDeployable);
      this.status.success();
    } catch (Exception ex) {
      this.status.error();
      deploymentLogger.error("Exception during deployment:\n" + ex.getMessage(), ex);
      this.file = null;
      this.uploadedFile = null;
    } finally {
      if (this.uploadedFile != null) {
        TemporaryDirectory.deleteOrIgnore(uploadedFile.getParent());
      }
    }
  }

  private boolean hasAllowedExtension(String name) {
    var lower = name.toLowerCase();
    return ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
  }

  public Boolean getShowDeployOptions() {
    return showDeployOptions;
  }

  public void toggleDeployOptions() {
    this.showDeployOptions = !showDeployOptions;
  }

  public DeployOptions getDeployOptions() {
    return deployOptions;
  }

  public Boolean isFileSet() {
    return this.uploadedFile != null;
  }

  public String getAllowedExtensionsText() {
    return ALLOWED_EXTENSIONS_TEXT;
  }

  public Boolean getShowDeployLogs() {
    return showDeployLogs;
  }

  public void setShowDeployLogs(Boolean showDeployLogs) {
    this.showDeployLogs = showDeployLogs;
  }

  public void hideDeployLogs() {
    this.showDeployLogs = false;
    this.status.idle();
  }

  public void onDialogClose() {
    resetData();
  }

  private void resetData() {
    this.deployOptions = new DeployOptions();
    this.file = null;
    this.uploadedFile = null;
    this.showDeployLogs = false;
    this.showDeployOptions = false;
    this.status.idle();
  }

  public String getLogs() {
    if (deploymentLogger == null) {
      return "";
    }
    return deploymentLogger.getLogs().stream().collect(Collectors.joining("\n"));
  }

  public DeploymentStatus getStatus() {
    return status;
  }

  private static String prettyFileSize(long bytes) {
    if (bytes < 1_000) {
      return bytes + " B";
    }
    if (bytes < 1_000_000) {
      return (bytes / 1_000) + " KB";
    }
    return (bytes / 1_000_000) + " MB";
  }
}
