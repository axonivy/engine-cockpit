package ch.ivyteam.enginecockpit.util;

import javax.management.openmbean.CompositeData;

import ch.ivyteam.ivy.environment.Ivy;

public class ErrorValue {

  private CompositeData error;

  public ErrorValue(CompositeData error) {
    this.error = error;
  }

  public String getStackTrace() {
    Ivy.log().fatal(error);
    if (!isAvailable()) {
      return "n.a.";
    }
    return (String)error.get("stackTrace");
  }

  public String getMessage() {
    if (!isAvailable()) {
      return "n.a.";
    }
    return (String)error.get("message");
  }

  public boolean isAvailable() {
    return error != null;
  }
}
