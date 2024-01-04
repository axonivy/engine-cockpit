package ch.ivyteam.enginecockpit.dynamic.config;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.internal.context.SecurityContext;

public class DynamicConfig {

  private ISecurityContext securityContext;
  private List<ConfigPropertyGroup> groups;

  public DynamicConfig(List<ConfigPropertyGroup> groups, ISecurityContext securityContext) {
    this.groups = groups;
    this.securityContext = securityContext;
  }

  public List<ConfigPropertyGroup> getGroups() {
    return groups;
  }

  @SuppressWarnings("restriction")
  public void save(ConfigPropertyGroup group) {
    var cfg = ((SecurityContext) securityContext).config();
    var gKey = ch.ivyteam.ivy.configuration.restricted.ConfigKey.create(group.getName());
    for (var p : group.getProperties()) {
      var shortKey = StringUtils.substringAfter(p.getName(), group.getName()+".");
      var pKey = ch.ivyteam.ivy.configuration.restricted.ConfigKey.create(p.getName());
      if (!shortKey.isBlank()) {
        pKey = gKey.append(shortKey);
      }
      int separator = shortKey.indexOf('.');
      if (separator != -1) {
        var before = shortKey.substring(0, separator);
        var next = shortKey.substring(separator+1);
        pKey = gKey.append(before).append(next);
      }
      cfg.identity().setProperty(pKey, p.getValue());
    }
    message();
  }

  public static void message() {
    var msg = new FacesMessage("Successfully saved");
    FacesContext.getCurrentInstance().addMessage("dynamicConfigFormSaveSuccess", msg);
  }
}
