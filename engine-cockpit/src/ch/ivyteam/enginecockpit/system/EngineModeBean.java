package ch.ivyteam.enginecockpit.system;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;

import java.io.Serializable;

import ch.ivyteam.ivy.server.restricted.EngineMode;

@Named
@SessionScoped
public class EngineModeBean implements Serializable {
  public boolean isDemo() {
    return EngineMode.is(EngineMode.DEMO);
  }

  public boolean isMaintenance() {
    return EngineMode.is(EngineMode.MAINTENANCE);
  }
}
