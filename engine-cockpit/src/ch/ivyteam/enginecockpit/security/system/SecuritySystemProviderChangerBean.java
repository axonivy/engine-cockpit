package ch.ivyteam.enginecockpit.security.system;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.configuration.restricted.ConfigKey;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.internal.SecurityContext;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class SecuritySystemProviderChangerBean {

  private SecuritySystem system;

  private String newProvider;

  public void setSystem(SecuritySystem system) {
    this.system = system;
  }

  public String getName() {
    if (system == null) {
      return "";
    }
    return system.getSecuritySystemName();
  }

  public String getProvider() {
    if (system == null) {
      return "";
    }
    return system.getSecuritySystemProvider();
  }

  public void setProvider(String newProvider) {
    this.newProvider = newProvider;
  }

  public SecuritySystem getSystem() {
    return system;
  }

  public void change() {
    if (StringUtils.isEmpty(newProvider)) {
      return;
    }
    if (StringUtils.equals(newProvider, system.getSecuritySystemProvider())) {
      return;
    }
    var context = (SecurityContext) system.getSecurityContext();
    var key = ConfigKey.create("SecuritySystems").append(system.getSecuritySystemName());
    IConfiguration.instance().remove(key);
    context.config().setProperty(ISecurityConstants.PROVIDER_CONFIG_KEY, newProvider);
  }
}
