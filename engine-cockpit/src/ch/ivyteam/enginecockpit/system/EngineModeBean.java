package ch.ivyteam.enginecockpit.system;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ch.ivyteam.ivy.server.restricted.EngineMode;

@ManagedBean
@SessionScoped
public class EngineModeBean {
  public boolean isDemo() {
    return EngineMode.is(EngineMode.DEMO) || EngineMode.is(EngineMode.DESIGNER_EMBEDDED);
  }

  public boolean isMaintenance() {
    return EngineMode.is(EngineMode.MAINTENANCE);
  }
}
