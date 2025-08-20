package ch.ivyteam.enginecockpit.setup;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@SessionScoped
public class WizardBean {
  private Steps activeStep;
  private List<StepStatus> steps;

  public WizardBean() {
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
    steps = new ArrayList<>();
    var context = FacesContext.getCurrentInstance();
    steps.add(context.getApplication().evaluateExpressionGet(context, "#{licenceBean}", StepStatus.class));
    steps.add(context.getApplication().evaluateExpressionGet(context, "#{administratorBean}", StepStatus.class));
    steps.add(context.getApplication().evaluateExpressionGet(context, "#{webServerConnectorBean}", StepStatus.class));
    steps.add(context.getApplication().evaluateExpressionGet(context, "#{storageBean}", StepStatus.class));
    steps.add(context.getApplication().evaluateExpressionGet(context, "#{systemDatabaseBean}", StepStatus.class));
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
