package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.ivy.application.ActivityOperationState;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IActivity;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;

public abstract class AbstractActivity
{
  private String name;
  private long id;
  protected IActivity activity;
  private ActivityState state;
  private ActivityOperationState operationState;
  protected boolean disable;
  private ApplicationBean bean;
  
  public AbstractActivity()
  {
    this("", 0, null);
  }
  
  public AbstractActivity(String name, long id, IActivity activity)
  {
    this(name, id, activity, null);
  }
  
  public AbstractActivity(String name, long id, IActivity activity, ApplicationBean bean)
  {
    this.name = name;
    this.id = id;
    this.activity = activity;
    this.bean = bean;
    updateStats();
  }
  
  public void updateStats()
  {
    if (activity == null)
    {
      state = ActivityState.INACTIVE;
      operationState = ActivityOperationState.INACTIVE;
      return;
    }
    state = activity.getActivityState();
    operationState = activity.getActivityOperationState();
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

  public void setId(long id)
  {
    this.id = id;
  }
  
  public ActivityState getState()
  {
    return state;
  }
  
  public String getStateLowerCase()
  {
    return state.toString().toLowerCase();
  }
  
  public void setState(ActivityState state)
  {
    this.state = state;
  }
  
  public ActivityOperationState getOperationState()
  {
    return operationState;
  }
  
  public String getOperationStateLowerCase()
  {
    return operationState.toString().toLowerCase();
  }
  
  public void setOperationState(ActivityOperationState operationState)
  {
    this.operationState = operationState;
  }
  
  public ReleaseState getReleaseState()
  {
    return null;
  }
  
  public boolean isActive()
  {
    return state == ActivityState.ACTIVE || disable;
  }
  
  public boolean isInActive()
  {
    return state == ActivityState.INACTIVE || disable;
  }
  
  public boolean isLocked()
  {
    return state == ActivityState.LOCKED || state == ActivityState.INACTIVE || disable;
  }
  
  public boolean isReleaseDisabled()
  {
    return true;
  }
  
  public boolean isDeleteDisabled()
  {
    return true;
  }
  
  public void activate()
  {
    activity.activate();
    reloadBeanStats();
  }
  
  public void deactivate()
  {
    activity.deactivate();
    reloadBeanStats();
  }
  
  public void lock()
  {
    activity.lock();
    reloadBeanStats();
  }
  
  public void release()
  {
    reloadBeanStats();
  }
  
  public void delete()
  {
    reloadBeanData();
  }
  
  public long getApplicationId()
  {
    if (activity instanceof IProcessModelVersion)
    {
      return ((IProcessModelVersion) activity).getProcessModel().getApplication().getId();
    }
    return 0;
  }
  
  private void reloadBeanData()
  {
    if (bean != null)
    {
      bean.reloadActivities();
    }
  }
  
  private void reloadBeanStats()
  {
    if (bean != null)
    {
      bean.reloadActivityStates();
    }
  }
}
