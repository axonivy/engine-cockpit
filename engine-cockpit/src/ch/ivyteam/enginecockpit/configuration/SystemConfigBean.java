package ch.ivyteam.enginecockpit.configuration;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.configuration.model.ConfigProperty;
import ch.ivyteam.enginecockpit.configuration.model.ConfigViewImpl;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@ManagedBean
@ViewScoped
public class SystemConfigBean {
  private final ConfigViewImpl configView;

  public SystemConfigBean() {
    configView = new ConfigViewImpl(List.of(
        ConfigViewImpl.defaultFilter(),
        new ContentFilter<ConfigProperty>("Security Systems", "Show Security Systems",
            p -> !Strings.CS.startsWith(p.getKey(), "SecuritySystems."), true)));
  }

  public ConfigViewImpl getConfigView() {
    return configView;
  }

  public void reloadConfig() {
    IConfiguration.instance().reload();
    Message.info()
        .summary("System configuration reloaded")
        .show();
  }
}
