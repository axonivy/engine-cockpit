package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import java.util.List;

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
        new Option("duration", "Duration", """
          Sets how long the recording should be running.

          "0" if no limit should be imposed, otherwise a string representation of a positive Long
          followed by an empty space and one of the following units:

          "ns" (nanoseconds)
          "us" (microseconds)
          "ms" (milliseconds)
          "s" (seconds)
          "m" (minutes)
          "h" (hours)
          "d" (days)

          Examples:
          "60 s",
          "10 m",
          "4 h",
          "0"
          """, "0"),
        new Option("maxAge", "Max age", """
          Specify the length of time that the data is kept in the disk repository until the oldest
          data may be deleted. Only works if disk=true, otherwise this parameter is ignored.

          "0" if no limit is imposed, otherwise a string representation of a positive Long value
          followed by an empty space and one of the following units,

          "ns" (nanoseconds)
          "us" (microseconds)
          "ms" (milliseconds)
          "s" (seconds)
          "m" (minutes)
          "h" (hours)
          "d" (days)

          Examples:
          "2 h"
          "24 h"
          "2 d"
          "0"
          """, "0"),
        new Option("maxSize", "Max size", """
          Specifies the size, measured in bytes, at which data is kept in disk repository.
          Only works if disk=true, otherwise this parameter is ignored.

          String representation of a Long value, must be positive

          Examples:
          "0"
          "100000000"
          """, "0"),
        new Option("disk", "Disk", """
          Stores recorded data as it is recorded.

          String representation of a Boolean value, "true" or "false"

          Examples:
          "true"
          "false"
          """, "false"),
        new Option("dumpOnExit", "Dump on exit", """
          Dumps recording data to disk on Java Virtual Machine (JVM) exit.

          String representation of a Boolean value, "true" or "false"

          Examples:
          "true"
          "false"
          """, "false"));
  }
}
