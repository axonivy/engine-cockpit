package ch.ivyteam.enginecockpit.application.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.application.IActivity;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.environment.Ivy;

public abstract class AppTreeItem {

  public static final String APP = "APP";
  public static final String PMV = "PMV";

  private final ApplicationBean bean;

  protected final IActivity activity;
  private StateOfActivity state;

  public abstract String getName();
  public abstract String getDisplayName();
  public abstract String getDetailView();
  public abstract List<String> isDeletable();

  protected List<String> projectConversionLog = new ArrayList<>();

  public AppTreeItem(ApplicationBean bean, IActivity activity) {
    this.bean = bean;
    this.activity = activity;
    updateStats();
  }

  public String getNotDeletableMessage() {
    return isDeletable().stream().collect(Collectors.joining("\n"));
  }

  // APP stuff
  public boolean isApp() {
    return false;
  }

  public String getReleaseStateIcon() {
    return null;
  }

  public ReleaseState getReleaseState() {
    return null;
  }

  public boolean isNotStartable() {
    return true;
  }

  public boolean isNotStopable() {
    return true;
  }

  public boolean isNotLockable() {
    return true;
  }

  public boolean isReleasable() {
    return false;
  }

  public void release() {}

  public void activate() {}

  public void deactivate() {}

  public void lock() {}

  // PMV stuff
  public boolean isPmv() {
    return false;
  }

  public abstract String getVersion();

  public String getLastChangeDate() {
    return null;
  }

 public abstract String getActivityType();

  protected void execute(Runnable executor, String action, boolean reloadOnlyStats) {
    try {
      executor.run();
      reloadBean(reloadOnlyStats);
      Message.info()
          .clientId("applicationMessage")
          .summary(Ivy.cm().content("/applications/ExecutionSuccessMessageSummary").replace("action", action).get())
          .detail(getActivityType() + " " + getName())
          .show();
    } catch (IllegalStateException ex) {
      Message.error()
          .clientId("applicationMessage")
          .summary(Ivy.cm().content("/applications/ExecutionErrorMessageSummary").replace("action", action).get())
          .detail(ex.getMessage())
          .exception(ex)
          .show();
    }
  }

  private void reloadBean(boolean reloadOnlyStats) {
    if (bean == null) {
      updateStats();
      return;
    }
    if (reloadOnlyStats) {
      bean.reloadActivityStates();
    } else {
      bean.reloadTree();
    }
  }

  public StateOfActivity getState() {
    return state;
  }

  public void updateStats() {
    if (activity == null) {
      state = new StateOfActivity();
    } else {
      state = new StateOfActivity(activity);
    }
  }

  public String getProjectConversionLog() {
    return projectConversionLog.stream().collect(Collectors.joining("\n"));
  }

  public boolean isNotConvertable() {
    return true;
  }

  public abstract long getApplicationId();
  public abstract long getProcessModelVersionId();

  public void convert() {}
  public abstract void delete();

  class ProjectConversionLog implements Consumer<String> {

    @Override
    public void accept(String msg) {
      projectConversionLog.add(msg);
    }
  }
}
