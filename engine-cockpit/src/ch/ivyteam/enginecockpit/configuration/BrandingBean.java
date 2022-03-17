package ch.ivyteam.enginecockpit.configuration;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.PrimeFaces;

import ch.ivyteam.enginecockpit.configuration.model.BrandingResource;
import ch.ivyteam.enginecockpit.configuration.model.CssColorDTO;
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

  private List<BrandingResource> resources;
  private String customCssContent;
  private List<CssColorDTO> cssColors;
  private List<CssColorDTO> filteredCssColors;

  private String currentResourceName;
  private String selectedCssColor;
  private String selectedCssColorValue;

  private String filter;


  public BrandingBean() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadResources();
  }

  public void reloadResources() {
    filter = "";
    brandingIO = new BrandingIO(managerBean.getSelectedIApplication());
    resources = brandingIO.findResources(List.copyOf(RESOURCE_NAMES.keySet())).entrySet().stream()
            .map(this::toBrandingResource)
            .sorted(Comparator.comparing(BrandingResource::getLabel))
            .collect(Collectors.toList());
    reloadCustomCssContent();
  }

  private void reloadCustomCssContent() {
    customCssContent = brandingIO.readCustomCss();
    cssColors = brandingIO.cssColors().stream()
            .map(CssColorDTO::new)
            .collect(Collectors.toList());
    PrimeFaces.current().executeScript("PF('colorsTable').filter();");
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

  public void setSelectedCssColor(String selectedCssColor) {
    this.selectedCssColor = selectedCssColor;
    this.selectedCssColorValue = cssColors.stream()
            .filter(c -> c.getColor().equals(selectedCssColor))
            .map(c -> c.getValue())
            .findFirst().orElse("hsl(0, 0%, 0%)");
  }

  public String getSelectedCssColor() {
    return selectedCssColor;
  }

  public void setSelectedCssColorValue(String value) {
    this.selectedCssColorValue = value;
  }

  public String getSelectedCssColorValue() {
    return selectedCssColorValue;
  }

  public List<CssColorDTO> getCssColors() {
    return cssColors;
  }

  public List<CssColorDTO> getFilteredCssColors() {
    return filteredCssColors;
  }

  public void setFilteredCssColors(List<CssColorDTO> filteredCssColors) {
    this.filteredCssColors = filteredCssColors;
  }

  public void saveColor() {
    setColor(selectedCssColorValue);
  }

  public void resetColor() {
    setColor(null);
  }

  private void setColor(String value) {
    var message = new FacesMessage("Successfully update color '" + selectedCssColor + "'", "");
    try {
      brandingIO.writeCssColor(selectedCssColor, value);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't update '" + selectedCssColor + "'", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
    reloadCustomCssContent();
  }

  public void saveCustomCss() {
    var message = new FacesMessage("Successfully saved 'custom.css'", "");
    try {
      brandingIO.writeCustomCss(customCssContent);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't save 'custom.css'", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
    reloadCustomCssContent();
  }

  public void resetRes() {
    var message = new FacesMessage("Successfully reset '" + currentResourceName + "'", "");
    try {
      brandingIO.resetResource(currentResourceName);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't reset '" + currentResourceName + "'", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
    reloadResources();
  }

  private BrandingResource toBrandingResource(Entry<String, String> entry) {
    var label = FilenameUtils.getBaseName(entry.getKey());
    return new BrandingResource(label, entry.getKey(), entry.getValue(), RESOURCE_NAMES.get(label));
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public boolean globalFilterFunction(Object value, Object filterStr, @SuppressWarnings("unused") Locale locale) {
    var cssColor = (CssColorDTO)value;
    return cssColor.getColor().contains(Objects.toString(filterStr, ""));
  }

}
