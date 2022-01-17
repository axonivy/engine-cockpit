package ch.ivyteam.enginecockpit.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.configuration.model.BrandingResource;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.branding.BrandingResolver;
import ch.ivyteam.ivy.config.IFileAccess;

@SuppressWarnings("restriction")
@ManagedBean
@RequestScoped
public class BrandingBean {

  private static final List<String> RESOURCE_NAMES = List.of("logo", "logo_white", "logo_small", "logo_mail", "favicon");

  private ManagerBean managerBean;
  private String resourceName;
  private List<BrandingResource> resources;

  private String customCssContent;

  public BrandingBean() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadResources();
  }

  private void reloadResources() {
    var app = managerBean.getSelectedIApplication();
    resources = RESOURCE_NAMES.stream()
            .map(name -> toBrandingResource(app, name))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    customCssContent = new BrandingResolver(app).findResource("custom.css")
            .map(r -> {
              try {
                return Files.readString(r);
              } catch (IOException ex) {
                return "";
              }
            }).orElse("");
  }

  private BrandingResource toBrandingResource(IApplication app, String res) {
    return new BrandingResolver(app).findResource(res)
            .map(r -> new BrandingResource(res, r, app.getContextPath()))
            .orElse(null);
  }

  public List<BrandingResource> getResources() {
    return resources;
  }

  public void setCurrentRes(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getCurrentRes() {
    return resourceName;
  }

  public void setCustomCssContent(String value) {
    this.customCssContent = value;
  }

  public String getCustomCssContent() {
    return customCssContent;
  }

  public void saveCustomCss() {
    var message = new FacesMessage("Successfully saved custom.css");
    var brandingDir = brandingDir(managerBean.getSelectedApplicationName());
    try {
      Files.createDirectories(brandingDir);
      Files.writeString(brandingDir.resolve("custom.css"), customCssContent);
    } catch (IOException ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't save custom.css", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
  }

  public void resetRes(String resource) {
    var message = new FacesMessage("Successfully reset " + resource);
    try {
      Files.deleteIfExists(brandingDir(managerBean.getSelectedApplicationName()).resolve(resource));
    } catch (IOException ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't reset " + resource, ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
    reloadResources();
  }

  public static Path brandingDir(String appName) {
    return IFileAccess.instance().getConfigFolder().resolve("applications").resolve(appName).resolve(BrandingResolver.BRANDING);
  }

}
