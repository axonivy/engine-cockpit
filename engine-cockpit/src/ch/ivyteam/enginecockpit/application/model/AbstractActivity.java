package ch.ivyteam.enginecockpit.application.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IActivity;

public abstract class AbstractActivity {
  private String name;
  private final long id;
  protected final IActivity activity;
  private final ApplicationBean bean;
  protected List<AbstractActivity> children;

  protected List<String> projectConversionLog = new ArrayList<>();

  public static final String APP = "APP";
  public static final String PM = "PM";
  public static final String PMV = "PMV";
  private StateOfActivity state;

  public AbstractActivity(String name, long id, IActivity activity, ApplicationBean bean) {
    this.name = name;
    this.id = id;
    this.activity = activity;
    this.bean = bean;
    this.children = new ArrayList<>();
    updateStats();
  }

  public boolean isApplication() {
    return false;
  }

  public boolean isPm() {
    return false;
  }

  public boolean isPmv() {
    return false;
  }

  public String getDetailView() {
    return "#";
  }

  public abstract long getRunningCasesCount();

  abstract String getIcon();

  public String getIconTitle() {
    return getActivityType();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public void addChild(AbstractActivity child) {
    children.add(child);
  }

  public StateOfActivity getState() {
    return state;
  }

  public boolean isNotStartable() {
    return state.is(ActivityState.ACTIVE);
  }

  public boolean isNotStopable() {
    return state.is(ActivityState.INACTIVE);
  }

  public boolean isNotLockable() {
    return state.is(ActivityState.LOCKED, ActivityState.INACTIVE);
  }

  public boolean isReleasable() {
    return true;
  }

  public boolean isNotConvertable() {
    return true;
  }

  public void convert() {
  }

  public boolean canConvert() {
    return false;
  }

  public String getProjectConversionLog() {
    return projectConversionLog.stream().collect(Collectors.joining("\n"));
  }

  public abstract List<String> isDeletable();

  public String getNotDeletableMessage() {
    return isDeletable().stream().collect(Collectors.joining("\n"));
  }

  public String getDeleteHint() {
    return "Are you sure you want to delete this " + getActivityType() + "?";
  }

  public void activate() {
    execute(() -> activity.activate(), "activate", true);
  }

  public void deactivate() {
    execute(() -> activity.deactivate(), "deactivate", true);
  }

  public void lock() {
    execute(() -> activity.lock(), "lock", true);
  }

  public void release() {}

  public abstract void delete();

  public void forceDelete() {}
  
  public boolean hasReleasedProcessModelVersion() {
    return true;
  }
  
  public String getWarningMessageForNoReleasedPmv() {
    return "";
  }

  protected void execute(Runnable executor, String action, boolean reloadOnlyStats) {
    try {
      executor.run();
      reloadBean(reloadOnlyStats);
      Message.info()
              .clientId("applicationMessage")
              .summary("Successfully " + action + " module")
              .detail(getActivityType() + " " + getName())
              .show();
    } catch (IllegalStateException ex) {
      Message.error()
              .clientId("applicationMessage")
              .summary("Could not " + action + " module")
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

  public abstract long getApplicationId();

  public abstract String getActivityType();

  public void updateStats() {
    if (activity == null) {
      state = new StateOfActivity();
    } else {
      state = new StateOfActivity(activity);
    }
  }

  class ProjectConversionLog implements Consumer<String> {

    @Override
    public void accept(String msg) {
      projectConversionLog.add(msg);
    }
  }
}
