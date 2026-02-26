package ch.ivyteam.enginecockpit.commons;

public class Feature {
  private String clazz;

  public Feature() {}

  public Feature(String clazz) {
    this.clazz = clazz;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }
  
  @Override
  public String toString() {
    return this.clazz;
  }

}
