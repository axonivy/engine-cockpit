package ch.ivyteam.enginecockpit.configuration.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

public class BrandingResource {
  private final String label;
  private final String name;
  private final String url;

  public BrandingResource(String resourceName, Path path, String contextPath) {
    this.label = imageLabel(resourceName);
    this.name = imageName(path);
    this.url = imageUrl(contextPath, name, path);
  }

  public String getLabel() {
    return label;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  private String imageLabel(String resourceName) {
    return WordUtils.capitalize(StringUtils.replace(resourceName, "_", " "));
  }

  private String imageName(Path resource) {
    return resource.getFileName().toString();
  }

  private String imageUrl(String contextPath, String resourceName, Path resource) {
    return contextPath +
            "/faces/javax.faces.resources/" +
            resourceName +
            "?ln=xpertivy-branding&xv=" + lastModificationOf(resource);
  }

  private String lastModificationOf(Path resource) {
    try {
      return String.valueOf(Date.from(Files.getLastModifiedTime(resource).toInstant()).getTime());
    } catch (IOException ex) {
      return "";
    }
  }


}
