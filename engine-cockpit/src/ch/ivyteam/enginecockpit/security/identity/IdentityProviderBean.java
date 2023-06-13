package ch.ivyteam.enginecockpit.security.identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.configuration.meta.Metadata;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.identity.core.IdentityProviderConfigMetadataProvider;
import ch.ivyteam.ivy.security.identity.spi.IdentityProvider;
import ch.ivyteam.ivy.security.internal.context.SecurityContext;
import ch.ivyteam.ivy.security.restricted.ISecurityContextInternal;

@ManagedBean
@ViewScoped
public class IdentityProviderBean {

  private String securitySystemName;

  private ISecurityContextInternal securityContext;
  private IdentityProvider identityProvider;
  private List<ConfigPropertyGroup> propertyGroups;

  public void onload() {
    securityContext = (ISecurityContextInternal) ISecurityManager.instance().securityContexts().get(securitySystemName);
    identityProvider = securityContext.identityProviders().get(0);
    var configurator = identityProvider.configurator();
    var properties = new IdentityProviderConfigMetadataProvider(configurator).get().entrySet().stream()
            .map(entry -> toConfigProperty(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    this.propertyGroups = ConfigPropertyGroup.toGroups(properties);
  }

  public void setSecuritySystemName(String securitySystemName) {
    this.securitySystemName = securitySystemName;
  }

  public String getSecuritySystemName() {
    return securitySystemName;
  }

  public String getSecuritySystemLink() {
    return SecuritySystem.link(securityContext);
  }

  public String getIdentityProviderName() {
    return identityProvider.name();
  }

  public List<ConfigPropertyGroup> getPropertyGroups() {
    return propertyGroups;
  }

  private ConfigProperty toConfigProperty(String key, Metadata metadata) {
    var config = ((SecurityContext) securityContext).config();
    var value = "";
    Map<String, String> keyValue = Map.of();
    if (metadata.isKeyValue()) {
      keyValue = new HashMap<>(config.getPropertyAsKeyValue(key));
    } else {
      value = config.getProperty(key);
    }
    return new ConfigProperty(config, key, value, keyValue, metadata);
  }

  public void save(ConfigPropertyGroup group) {
    var cfg = ((SecurityContext) securityContext).config();
    for (var p : group.getProperties()) {
      cfg.setProperty(p.getName(), p.getValue());
    }
    message();
  }

  static void message() {
    var msg = new FacesMessage("Successfully saved");
    FacesContext.getCurrentInstance().addMessage("securityIdentityProviderSaveSuccess", msg);
  }
}
