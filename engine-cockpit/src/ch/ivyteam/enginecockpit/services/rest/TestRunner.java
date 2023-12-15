package ch.ivyteam.enginecockpit.services.rest;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.rest.client.RestClient;

public class TestRunner {

  @SuppressWarnings("restriction")
  public static WebTarget createTarget(IApplication app, RestClient uiStateClient) {
    return new ch.ivyteam.ivy.rest.client.internal.ExternalRestWebService(app, uiStateClient).getWebTargetFactory().create();
  }

  public static ConnectionTestResult testConnection(WebTarget client) {
    try {
      try (var response = client.request().head()) {
        StatusType status = response.getStatusInfo();
        return toTestResult(status);
      }
    } catch (ProcessingException ex) {
      return new ConnectionTestResult("", 0, TestResult.ERROR, "Invalid Url (may contains script context)\n"
              + "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }

  static ConnectionTestResult toTestResult(StatusType status) {
    var code = status.getStatusCode();
    Family family = status.getFamily();
    if (family.equals(Family.SUCCESSFUL) || family.equals(Family.REDIRECTION)) {
      return new ConnectionTestResult("HEAD", code, TestResult.SUCCESS, "Successfully sent test request to REST service");
    }
    if (code == 400) {
      return new ConnectionTestResult("HEAD", code, TestResult.WARNING, "REST service does not understand test request");
    } else if (code == 401) {
      return new ConnectionTestResult("HEAD", code, TestResult.WARNING, "Authentication was not successful");
    }
    return new ConnectionTestResult("HEAD", code, TestResult.ERROR, "Could not connect to REST service");
  }

}
