package ch.ivyteam.enginecockpit.security.identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import ch.ivyteam.api.API;
import ch.ivyteam.enginecockpit.security.directory.DirectoryBrowserBean;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.configuration.meta.Metadata;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.identity.core.IdentityProviderConfigMetadataProvider;
import ch.ivyteam.ivy.security.identity.core.config.IdpConfig;
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
  private DirectoryBrowserBean browserBean;

  private ConfigProperty browserProperty;
  private IdentityProviderListDialogBean providerListDialogBean;

  public void onload() {
    securityContext = (ISecurityContextInternal) ISecurityManager.instance().securityContexts().get(securitySystemName);
    identityProvider = securityContext.identityProviders().get(0);
    var configurator = identityProvider.configurator();
    var properties = new IdentityProviderConfigMetadataProvider(configurator).get().entrySet().stream()
      .map(entry -> toConfigProperty(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());
    this.propertyGroups = ConfigPropertyGroup.toGroups(properties);
    this.browserBean = new DirectoryBrowserBean();
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
    var config = getIdpConfig();
    var value = "";
    Map<String, String> keyValue = Map.of();
    if (metadata.isKeyValue()) {
      keyValue = new HashMap<>(config.getPropertyAsKeyValue(key));
    } else {
      value = config.getProperty(key);
    }
    return new ConfigProperty(config, key, value, keyValue, metadata);
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

  static void message() {
    var msg = new FacesMessage("Successfully saved");
    FacesContext.getCurrentInstance().addMessage("securityIdentityProviderSaveSuccess", msg);
  }

  public void browseProperty(ConfigProperty property) {
    API.checkParameterNotNull(property, "property");
    providerListDialogBean = null;
    browserProperty = property;
    configureBrowser(property.getValue());
  }

  public void browseBeanProperty(IdentityProviderListDialogBean bean) {
    API.checkParameterNotNull(bean, "bean");
    browserProperty = null;
    providerListDialogBean = bean;
    configureBrowser(bean.getNewValue());
  }

  public void setSelectedItem() {
    String selection = browserBean.getSelectedNameString();
    if (providerListDialogBean == null) {
      browserProperty.setValue(selection);
    } else {
      providerListDialogBean.setNewValue(selection);
    }
  }

  public DirectoryBrowserBean getDirectoryBrowser() {
    return browserBean;
  }

  private void configureBrowser(String currentValue) {
    var browser = this.identityProvider.directoryBrowser(getIdpConfig());
    browserBean.browse(browser, currentValue);
  }

  private IdpConfig getIdpConfig() {
    return ((SecurityContext) securityContext).config().identity();
  }
}
