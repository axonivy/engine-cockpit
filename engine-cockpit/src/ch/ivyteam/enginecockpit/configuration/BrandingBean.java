package ch.ivyteam.enginecockpit.configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.configuration.model.BrandingResource;
import ch.ivyteam.enginecockpit.configuration.model.CssColorDTO;
import ch.ivyteam.enginecockpit.download.AllResourcesDownload;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.branding.BrandingIO;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class BrandingBean implements AllResourcesDownload {

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
  private CssColorDTO selectedCssColor;

  private String filter;

  public BrandingBean() {
    managerBean = ManagerBean.instance();
    reloadResources();
  }

  public void upload(FileUploadEvent event) {
    var file = event.getFile();
    var message = new FacesMessage();
    try {
      var app = IApplicationRepository.instance().findByName(managerBean.getSelectedApplicationName()).orElse(null);
      var extension = FilenameUtils.getExtension(file.getFileName());
      var newResourceName = new BrandingIO(app).setImage(getCurrentRes(), extension, file.getInputStream());
      message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Successfully uploaded " + newResourceName);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage("msgs", message);
  }

  public void reloadResources() {
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
    this.selectedCssColor = cssColors.stream()
            .filter(c -> c.getColor().equals(selectedCssColor))
            .findFirst().orElse(new CssColorDTO("unknown color", "", ""));
  }

  public CssColorDTO getSelectedCssColor() {
    return selectedCssColor;
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
    setColor(selectedCssColor.getValue());
  }

  public void resetColor() {
    setColor(null);
  }

  public void setColor(String value) {
    var message = new FacesMessage("Successfully update color '" + selectedCssColor.getColor() + "'", "");
    try {
      brandingIO.writeCssColor(selectedCssColor.getColor(), value);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't update '" + selectedCssColor.getColor() + "'", ex.getMessage());
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

  @Override
  public StreamedContent getAllResourcesDownload() {
    var appName = managerBean.getSelectedApplicationName();
    try (var out = new ByteArrayOutputStream()) {
      DownloadUtil.zipDir(appBrandingDir(appName), out);
      return DefaultStreamedContent
              .builder()
              .stream(() -> new ByteArrayInputStream(out.toByteArray()))
              .contentType("application/zip")
              .name("branding-" + appName + ".zip")
              .build();
    } catch (IOException ex) {
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage());
      FacesContext.getCurrentInstance().addMessage("msgs", message);
    }
    return null;
  }

  private Path appBrandingDir(String appName) throws IOException {
    var brandingDir = UrlUtil.getConfigFile("applications").resolve(appName).resolve("branding");
    if (!isBrandingDirEmpty(brandingDir)) {
      return brandingDir.toRealPath();
    }
    throw new IOException("No branding resources found for app '" + appName + "'");
  }

  private boolean isBrandingDirEmpty(Path brandingDir) throws IOException {
    if (!Files.exists(brandingDir) || !Files.isDirectory(brandingDir)) {
      return true;
    }
    try (var dir = Files.newDirectoryStream(brandingDir)) {
      return !dir.iterator().hasNext();
    }
  }

}
