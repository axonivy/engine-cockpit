package ch.ivyteam.enginecockpit.services;

import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;

public abstract class DetailView extends HelpServices implements IConnectionTestResult, PropertyEditor {

  @Override
  public abstract String getYaml();
}
