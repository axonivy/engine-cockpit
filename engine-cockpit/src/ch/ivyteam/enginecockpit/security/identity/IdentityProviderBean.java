package ch.ivyteam.enginecockpit.security.identity;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.api.API;
import ch.ivyteam.enginecockpit.dynamic.config.ConfigProperty;
import ch.ivyteam.enginecockpit.dynamic.config.DynamicConfig;
import ch.ivyteam.enginecockpit.dynamic.config.DynamicConfigListDialogBean;
import ch.ivyteam.enginecockpit.security.directory.DirectoryBrowserBean;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
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
    identityProvider = securityContext.identityProvider();
    var configurator = identityProvider.configurator();
    var idp = ((SecurityContext) securityContext).config().identity();
    this.dynamicConfig = DynamicConfig.create()
            .configurator(configurator)
            .config(idp)
            .toDynamicConfig();
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
