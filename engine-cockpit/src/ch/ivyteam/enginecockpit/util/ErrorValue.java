package ch.ivyteam.enginecockpit.util;

import javax.management.openmbean.CompositeData;

public class ErrorValue {

  private final CompositeData error;

  public ErrorValue(CompositeData error) {
    this.error = error;
  }

  public String getStackTrace() {
    if (!isAvailable()) {
      return "n.a.";
    }
    return (String) error.get("stackTrace");
  }

  public String getMessage() {
    if (!isAvailable()) {
      return "n.a.";
    }
    return (String) error.get("message");
  }

  public boolean isAvailable() {
    return error != null;
  }
}
