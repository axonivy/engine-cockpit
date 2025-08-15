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
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.directory.InvalidAttributesException;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import ch.ivyteam.enginecockpit.configuration.model.BrandingResource;
import ch.ivyteam.enginecockpit.configuration.model.CssColorDTO;
import ch.ivyteam.enginecockpit.download.AllResourcesDownload;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.branding.BrandingIO;
import ch.ivyteam.ivy.config.IFileAccess;
import ch.ivyteam.ivy.environment.Ivy;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class BrandingBean implements AllResourcesDownload {

  private static final Map<String, String> RESOURCE_NAMES = Map.of("logo", Ivy.cm().co("/branding/LogoHelperMessage"),
      "logo_light", Ivy.cm().co("/branding/LogoLightHelperMessage"),
      "logo_small", Ivy.cm().co("/branding/LogoSmallHelperMessage"),
      "logo_mail", Ivy.cm().co("/branding/LogoMailHelperMessage"),
      "favicon", Ivy.cm().co("/branding/FaviconHelperMessage"));
  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "svg", "webp", "gif");

  private final ManagerBean managerBean;
  private BrandingIO brandingIO;

  private List<BrandingResource> resources;
  private String customCssContent;
  private List<CssColorDTO> cssColors;
  private List<CssColorDTO> filteredCssColors;

  private String currentResourceName;
  private CssColorDTO selectedCssColor;

  private String filter;
  private UploadedFile file;

  public BrandingBean() {
    managerBean = ManagerBean.instance();
    reloadResources();
  }

  public void upload(FileUploadEvent event) {
    var uploadFile = event.getFile();
    var message = new FacesMessage();
    try {
      var extension = FilenameUtils.getExtension(uploadFile.getFileName());
      if (!ALLOWED_EXTENSIONS.contains(extension)) {
        throw new InvalidAttributesException("Not supported file extension: '" + extension + "'");
      }
      var app = IApplicationRepository.instance().findByName(managerBean.getSelectedApplicationName()).orElse(null);
      try (var in = uploadFile.getInputStream()) {
        var newResourceName = new BrandingIO(app).setImage(getCurrentRes(), extension, in);
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, Ivy.cm().co("/common/Success"),
            Ivy.cm().content("/branding/UploadFileSuccessfulMessage").replace("resource", newResourceName).get());
      }
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage());
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
        .findFirst().orElse(new CssColorDTO(Ivy.cm().co("/branding/UnknownColor"), "", ""));
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
    var message = new FacesMessage(
        Ivy.cm().content("/branding/UpdateColorSuccessfulMessage").replace("color", selectedCssColor.getColor()).get(),
        "");
    try {
      brandingIO.writeCssColor(selectedCssColor.getColor(), value);
    } catch (Exception ex) {
      message =
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().content("/branding/UpdateColorUnsuccessfulMessage")
              .replace("color", selectedCssColor.getColor()).get(), ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
    reloadCustomCssContent();
  }

  public void saveCustomCss() {
    var message = new FacesMessage(Ivy.cm().co("/branding/UpdateCustomCSSSuccessfulMessage"), "");
    try {
      brandingIO.writeCustomCss(customCssContent);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/branding/UpdateCustomCSSUnsuccessfulMessage"), ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
    reloadCustomCssContent();
  }

  public void resetRes() {
    var message = new FacesMessage(
        Ivy.cm().content("/branding/ResetResourceSuccessfulMessage").replace("resource", currentResourceName).get(),
        "");
    try {
      brandingIO.resetResource(currentResourceName);
    } catch (Exception ex) {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Ivy.cm().content("/branding/ResetResourceUnsuccessfulMessage").replace("resource", currentResourceName).get(),
          ex.getMessage());
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
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage());
      FacesContext.getCurrentInstance().addMessage("msgs", message);
    }
    return null;
  }

  private Path appBrandingDir(String appName) throws IOException {
    var brandingDir = IFileAccess.instance().config().resolve("applications").resolve(appName).resolve("branding");
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

  public UploadedFile getFile() {
    return file;
  }

  public void setFile(UploadedFile file) {
    this.file = file;
  }

  public String getAllowedExtensionsString() {
    return ALLOWED_EXTENSIONS.stream()
        .map(Object::toString)
        .collect(Collectors.joining(", "));
  }
}
