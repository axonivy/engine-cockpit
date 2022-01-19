package ch.ivyteam.enginecockpit.configuration.model;

public class BrandingResource {
  private final String label;
  private final String name;
  private final String url;
  private final String description;

  public BrandingResource(String label, String resourceName, String url, String description) {
    this.label = label;
    this.name = resourceName;
    this.url = url;
    this.description = description;
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

  public String getDescription() {
    return description;
  }

}
