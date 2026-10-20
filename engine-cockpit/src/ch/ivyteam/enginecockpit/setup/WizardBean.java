package ch.ivyteam.enginecockpit.setup;

import java.io.Serializable;
import java.util.List;

import ch.ivyteam.enginecockpit.system.LicenceBean;
import ch.ivyteam.enginecockpit.system.SystemDatabaseBean;
import ch.ivyteam.enginecockpit.system.administrators.AdministratorBean;
import ch.ivyteam.ivy.environment.Ivy;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@SessionScoped
public class WizardBean implements Serializable {

  @Inject
  private LicenceBean licenceBean;
  @Inject
  private AdministratorBean administratorBean;
  @Inject
  private WebServerConnectorBean webServerConnectorBean;
  @Inject
  private StorageBean storageBean;
  @Inject
  private SystemDatabaseBean systemDatabaseBean;

  private Steps activeStep;

  @PostConstruct
  public void init() {
    activeStep = Steps.LICENCE;
    Steps firstStep = getFirstStepWithWarning();
    if (firstStep != null) {
      activeStep = firstStep;
    }
  }

  public String getInfoPageUrl() {
    return FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
  }

  public int getActiveStep() {
    return activeStep.value;
  }

  public void setActiveStep(int step) {
    activeStep = Steps.valueOf(step);
  }

  public void nextStep() {
    activeStep = Steps.valueOf(activeStep.value + 1);
  }

  public void prevStep() {
    activeStep = Steps.valueOf(activeStep.value - 1);
  }

  public boolean isLastStep() {
    return activeStep.value == Steps.values().length - 1;
  }

  public boolean isFirstStep() {
    return activeStep.value == 0;
  }

  public Steps getFirstStepWithWarning() {
    var steps = List.of(licenceBean, administratorBean, webServerConnectorBean, storageBean, systemDatabaseBean);
    for (StepStatus step : steps) {
      if (!step.isStepOk()) {
        return Steps.valueOf(steps.indexOf(step));
      }
    }
    return null;
  }

  public void gotoFirstWarningStep() {
    var firstStepWithWarning = getFirstStepWithWarning();
    if (firstStepWithWarning != null) {
      setActiveStep(getFirstStepWithWarning().value);
    }
  }

  public enum Steps {
    LICENCE(0, Ivy.cm().co("/setup/Licence")),
    ADMINS(1, Ivy.cm().co("/setup/Administrators")),
    WEBSERVER(2, Ivy.cm().co("/setup/WebServer")),
    STORAGE(3, Ivy.cm().co("/setup/Storage")),
    SYSTEMDB(4, Ivy.cm().co("/setup/SystemDatabase"));

    private final int value;
    private final String name;

    Steps(int value, String name) {
      this.value = value;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public static Steps valueOf(int index) {
      if (index >= Steps.values().length || index < 0) {
        return Steps.LICENCE;
      }
      return values()[index];
    }
  }

  public abstract static class StepStatus {
    public abstract boolean isStepOk();

    public abstract String getStepWarningMessage();

    public String getStepStatus() {
      return isStepOk() ? "step-ok" : "step-warning";
    }
  }
}
