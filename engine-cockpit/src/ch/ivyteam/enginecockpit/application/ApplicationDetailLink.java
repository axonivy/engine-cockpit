package ch.ivyteam.enginecockpit.application;

import jakarta.ws.rs.core.UriBuilder;

public class ApplicationDetailLink {

  public static String getApplicationDetailLink(String appName, String securitySystemName) {
    return UriBuilder.fromPath("application.xhtml")
        .queryParam("context", securitySystemName)
        .queryParam("app", appName)
          .build()
          .toString();
  }
}
