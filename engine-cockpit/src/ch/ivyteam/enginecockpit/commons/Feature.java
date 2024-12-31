package ch.ivyteam.enginecockpit.commons;

public class Feature {
  private String clazz;
  private final boolean isDefault;

  public Feature() {
    this.isDefault = false;
  }

  public Feature(String clazz) {
    this(clazz, false);
  }

  public Feature(String clazz, boolean isDefault) {
    this.clazz = clazz;
    this.isDefault = isDefault;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public boolean isDefault() {
    return isDefault;
  }

}
