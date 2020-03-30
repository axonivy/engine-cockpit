package ch.ivyteam.enginecockpit;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean
@ApplicationScoped
public class RestartBean
{
  private boolean restartEngine;
  
  public boolean isRestartEngine()
  {
    return restartEngine;
  }
  
  public void setRestartEngine(boolean restartEngine)
  {
    this.restartEngine = restartEngine;
  }
  
  public void reset()
  {
    if (restartEngine && FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("resetRestartHint"))
    {
      this.restartEngine = false;
    }
  }
}
