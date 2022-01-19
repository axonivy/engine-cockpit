package ch.ivyteam.enginecockpit.configuration;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;

import ch.ivyteam.enginecockpit.configuration.model.BrandingResource;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.branding.BrandingIO;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class BrandingBean {

  private static final Map<String, String> RESOURCE_NAMES = Map.of("logo", "The main logo image",
          "logo_light", "Same as the main logo, but e.g. in our case with white writing",
          "logo_small", "The logo in small (square format recommended), used by e.g. error, login pages",
          "logo_mail", "The logo with is taken by the default Axon Ivy Engine email notifications",
          "favicon", "The logo fot the browser tab (square format recommended)");

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
    resources = brandingIO.findResources(List.copyOf(RESOURCE_NAMES.keySet())).entrySet().stream()
            .map(this::toBrandingResource)
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

  private BrandingResource toBrandingResource(Entry<String, String> entry) {
    var label = FilenameUtils.getBaseName(entry.getKey());
    return new BrandingResource(label, entry.getKey(), entry.getValue(), RESOURCE_NAMES.get(label));
  }

}
