package ch.ivyteam.enginecockpit.dynamic.config;

import java.util.List;
import java.util.function.BiConsumer;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.restricted.ConfigKey;

@SuppressWarnings("restriction")
public class DynamicConfig {

  private List<ConfigPropertyGroup> groups;
  private BiConsumer<ConfigKey, String> saver;

  public DynamicConfig(List<ConfigPropertyGroup> groups, BiConsumer<ConfigKey, String> saver) {
    this.groups = groups;
    this.saver = saver;
  }

  public List<ConfigPropertyGroup> getGroups() {
    return groups;
  }

  public void save(ConfigPropertyGroup group) {
    var gKey = ConfigKey.create(group.getName());
    for (var p : group.getProperties()) {
      var shortKey = StringUtils.substringAfter(p.getName(), group.getName() + ".");
      var pKey = ConfigKey.create(p.getName());
      if (!shortKey.isBlank()) {
        pKey = gKey.append(shortKey);
      }
      int separator = shortKey.indexOf('.');
      if (separator != -1) {
        var before = shortKey.substring(0, separator);
        var next = shortKey.substring(separator+1);
        pKey = gKey.append(before).append(next);
      }
      saver.accept(pKey, p.getValue());
    }
    message();
  }

  public static void message() {
    var msg = new FacesMessage("Successfully saved");
    FacesContext.getCurrentInstance().addMessage("dynamicConfigFormSaveSuccess", msg);
  }
}
