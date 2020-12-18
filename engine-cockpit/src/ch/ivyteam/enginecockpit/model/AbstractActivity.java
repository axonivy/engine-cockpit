package ch.ivyteam.enginecockpit.model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IActivity;

public abstract class AbstractActivity
{
  private String name;
  private final long id;
  protected final IActivity activity;
  private final ApplicationBean bean;
  protected List<AbstractActivity> children;
  
  public static final String APP = "APP";
  public static final String PM = "PM";
  public static final String PMV = "PMV";
  private StateOfActivity state;
  
  public AbstractActivity(String name, long id, IActivity activity, ApplicationBean bean)
  {
    this.name = name;
    this.id = id;
    this.activity = activity;
    this.bean = bean;
    this.children = new ArrayList<>();
    updateStats();
  }
  
  public boolean isApplication()
  {
    return false;
  }
  
  public boolean isPmv()
  {
    return false;
  }
  
  public String getDetailView()
  {
    return "#";
  }
  
  abstract long getRunningCasesCount();
  
  abstract String getIcon();
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public long getId()
  {
    return id;
  }
  
  public void addChild(AbstractActivity child)
  {
    children.add(child);
  }

  public StateOfActivity getState()
  {
    return state;
  }
  
  public boolean isNotStartable()
  {
    return state.is(ActivityState.ACTIVE) || isProtected();
  }
  
  public boolean isNotStopable()
  {
    return state.is(ActivityState.INACTIVE) || isProtected();
  }
  
  public boolean isNotLockable()
  {
    return state.is(ActivityState.LOCKED, ActivityState.INACTIVE) || isProtected();
  }

  public boolean isReleasable()
  {
    return true;
  }
  
  public boolean isDeletable()
  {
    return !isProtected();
  }
  
  public String getDeleteHint()
  {
    return "Are you sure about deleting this " + getActivityType() + "?";
  }
  
  public void activate()
  {
    execute(() -> activity.activate(), "activate", true);
  }
  
  public void deactivate()
  {
    execute(() -> activity.deactivate(), "deactivate", true);
  }
  
  public void lock()
  {
    execute(() -> activity.lock(), "lock", true);
  }
  
  public void release()
  {
  }
  
  public abstract void delete();
  
  protected void execute(Runnable executor, String action, boolean reloadOnlyStats)
  {
    var message = new FacesMessage("Successfully " + action + " module", getActivityType() + " " + getName());
    try 
    {
      executor.run();
      reloadBean(reloadOnlyStats);
    }
    catch (IllegalStateException ex)
    {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not " + action + " module", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage("applicationMessage", message);
  }

  private void reloadBean(boolean reloadOnlyStats)
  {
    if (bean == null)
    {
      updateStats();
      return;
    }
    if (reloadOnlyStats)
    {
      bean.reloadActivityStates();
    }
    else
    {
      bean.reloadTree();
    }
  }
  
  public abstract long getApplicationId();
  
  public abstract String getActivityType();
  
  public abstract boolean isProtected();
  
  public void updateStats()
  {
    if (activity == null)
    {
      state = new StateOfActivity();
    }
    else
    {
      state = new StateOfActivity(activity);
    }
  }
  
}
