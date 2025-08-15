package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import java.util.List;
import ch.ivyteam.ivy.environment.Ivy;

public final class Option {
  private final String name;
  private final String label;
  private final String description;
  private final String defaultValue;
  private String value;

  public Option(String name, String label, String description, String value) {
    this.name = name;
    this.label = label;
    this.description = description;
    this.value = value;
    this.defaultValue = value;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isDefaultValue() {
    return defaultValue.equals(value);
  }

  public static List<Option> createAll() {
    return List.of(
        new Option("duration",
            getCmsValue("duration/Name"),
            getCmsValue("duration/Description"),
            getCmsValue("duration/DefaultValue")),
        new Option("maxAge",
            getCmsValue("maxAge/Name"),
            getCmsValue("maxAge/Description"),
            getCmsValue("maxAge/DefaultValue")),
        new Option("maxSize",
            getCmsValue("maxSize/Name"),
            getCmsValue("maxSize/Description"),
            getCmsValue("maxSize/DefaultValue")),
        new Option("disk",
            getCmsValue("disk/Name"),
            getCmsValue("disk/Description"),
            getCmsValue("disk/DefaultValue")),
        new Option("dumpOnExit",
            getCmsValue("dumpOnExit/Name"),
            getCmsValue("dumpOnExit/Description"),
            getCmsValue("dumpOnExit/DefaultValue")));
  }

  private static String getCmsValue(String cmsPath) {
    return Ivy.cm().co("/monitor/jfr/Option/" + cmsPath);
  }
}
