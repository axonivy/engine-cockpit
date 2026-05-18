package ch.ivyteam.enginecockpit.system;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;

import ch.ivyteam.ivy.server.restricted.EngineMode;

@Named
@SessionScoped
public class EngineModeBean {
  public boolean isDemo() {
    return EngineMode.is(EngineMode.DEMO);
  }

  public boolean isMaintenance() {
    return EngineMode.is(EngineMode.MAINTENANCE);
  }
}
