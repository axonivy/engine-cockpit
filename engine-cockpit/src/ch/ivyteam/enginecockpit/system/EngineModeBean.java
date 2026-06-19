package ch.ivyteam.enginecockpit.system;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

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
