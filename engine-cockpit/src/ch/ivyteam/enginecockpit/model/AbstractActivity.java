package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.ActivityOperationState;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IActivity;
import ch.ivyteam.ivy.application.ReleaseState;

public abstract class AbstractActivity
{
  private String name;
  private long id;
  private IActivity activity;
  private ActivityState state;
  private ActivityOperationState operationState;
  protected boolean disable;
  
  public AbstractActivity()
  {
    this("", 0, null);
  }
  
  public AbstractActivity(String name, long id, IActivity activity)
  {
    this.name = name;
    this.id = id;
    this.activity = activity;
    updateStats();
  }
  
  private void updateStats()
  {
    if (activity == null)
    {
      state = ActivityState.INACTIVE;
      operationState = ActivityOperationState.INACTIVE;
    }
    else
    {
      state = activity.getActivityState();
      operationState = activity.getActivityOperationState();
    }
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
  
  public void activate()
  {
    activity.activate();
    updateStats();
  }
  
  public void deactivate()
  {
    activity.deactivate();
    updateStats();
  }
  
  public void lock()
  {
    activity.lock();
    updateStats();
  }
}
