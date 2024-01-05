package ch.ivyteam.enginecockpit.security.identity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.api.API;
import ch.ivyteam.enginecockpit.dynamic.config.ConfigProperty;
import ch.ivyteam.enginecockpit.dynamic.config.ConfigPropertyGroup;
import ch.ivyteam.enginecockpit.dynamic.config.DynamicConfig;
import ch.ivyteam.enginecockpit.dynamic.config.DynamicConfigListDialogBean;
import ch.ivyteam.enginecockpit.security.directory.DirectoryBrowserBean;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.configuration.configurator.ConfiguratorMetadataProvider;
import ch.ivyteam.ivy.configuration.meta.Metadata;
import ch.ivyteam.ivy.security.ISecurityManager;
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
  private DynamicConfig dynamicConfig;
  private DirectoryBrowserBean browserBean;

  private ConfigProperty browserProperty;
  private DynamicConfigListDialogBean providerListDialogBean;

  public void onload() {
    securityContext = (ISecurityContextInternal) ISecurityManager.instance().securityContexts().get(securitySystemName);
    identityProvider = securityContext.identityProviders().get(0);
    var configurator = identityProvider.configurator();
    var properties = new ConfiguratorMetadataProvider(configurator).get().entrySet().stream()
      .map(entry -> toConfigProperty(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());
    var propertyGroups = ConfigPropertyGroup.toGroups(properties);
    var cfg = ((SecurityContext) securityContext).config();
    this.dynamicConfig = new DynamicConfig(propertyGroups, cfg.identity()::setProperty);
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

  public DynamicConfig getDynamicConfig() {
    return dynamicConfig;
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
    return new ConfigProperty(config::setProperty, key, value, keyValue, metadata);
  }

  public void browseProperty(ConfigProperty property) {
    API.checkParameterNotNull(property, "property");
    providerListDialogBean = null;
    browserProperty = property;
    configureBrowser(property.getValue());
  }

  public void browseBeanProperty(DynamicConfigListDialogBean bean) {
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
