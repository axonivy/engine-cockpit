package ch.ivyteam.enginecockpit.configuration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.configuration.model.BrandingResource;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.branding.BrandingIO;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class BrandingBean {

  private static final List<String> RESOURCE_NAMES = List.of("logo", "logo_white", "logo_small", "logo_mail", "favicon");

  private ManagerBean managerBean;
  private BrandingIO brandingIO;

  private String currentResourceName;
  private List<BrandingResource> resources;
  private String customCssContent;


  public BrandingBean() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadResources();
  }

  public void reloadResources() {
    var app = managerBean.getSelectedIApplication();
    brandingIO = new BrandingIO(app);
    resources = brandingIO.findResources(RESOURCE_NAMES).entrySet().stream()
            .map(entry -> new BrandingResource(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(BrandingResource::getLabel))
            .collect(Collectors.toList());
    customCssContent = brandingIO.readCustomCss();
  }

  public List<BrandingResource> getResources() {
    return resources;
  }

  public void setCurrentRes(String resourceName) {
    this.currentResourceName = resourceName;
  }

  public String getCurrentRes() {
    return currentResourceName;
  }

  public void setCustomCssContent(String value) {
    this.customCssContent = value;
  }

  public String getCustomCssContent() {
    return customCssContent;
  }

  public void saveCustomCss() {
    var message = new FacesMessage("Successfully saved custom.css", "");
    try {
      brandingIO.writeCustomCss(customCssContent);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't save custom.css", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
  }

  public void resetRes() {
    var message = new FacesMessage("Successfully reset " + currentResourceName, "");
    try {
      brandingIO.resetResource(currentResourceName);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't reset " + currentResourceName, ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
    reloadResources();
  }

}
