package ch.ivyteam.enginecockpit.configuration;

import java.util.Arrays;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
public class ConfigurationBean {

  public String getResetConfigConfirmDialogMessage(String title, String keyConfig) {
    return Ivy.cms().co("/configuration/ResetConfigConfirmDialogMessage", Arrays.asList(title, keyConfig));
  }
}
