package ch.ivyteam.enginecockpit.services;

import ch.ivyteam.enginecockpit.util.UrlUtil;

public abstract class EditServices
{
  public String getTitle()
  {
    return "Edit Service";
  }
  
  public String getGuideText()
  {
    return "Services can not be edited over the engine cockpit yet. To do so please overwrite the app.yaml file from your Application.";
  }
  
  public String getHelpUrl()
  {
    return UrlUtil.getEngineGuideBaseUrl() + "/configuration/files/app-yaml.html";
  }
  abstract String getYaml();
}
