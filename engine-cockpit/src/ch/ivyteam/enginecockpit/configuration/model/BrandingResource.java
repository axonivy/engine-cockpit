package ch.ivyteam.enginecockpit.configuration.model;

import org.apache.commons.io.FilenameUtils;

public class BrandingResource {
  private final String label;
  private final String name;
  private final String url;

  public BrandingResource(String resourceName, String url) {
    this.label = FilenameUtils.getBaseName(resourceName);
    this.name = resourceName;
    this.url = url;
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

}
