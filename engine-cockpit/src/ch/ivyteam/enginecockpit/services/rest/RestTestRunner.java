package ch.ivyteam.enginecockpit.services.rest;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.restricted.di.ApplicationContext;
import ch.ivyteam.ivy.application.restricted.di.ProcessModelVersionContext;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.security.di.SecurityContextContext;

public class RestTestRunner {

  private final IApplication app;
  private final RestClient uiClient;

  public RestTestRunner(IApplication app, RestClient uiClient) {
    this.app = app;
    this.uiClient = uiClient;
  }

  public ConnectionTestResult test() {
    return new SecurityContextContext(app.getSecurityContext()).getInContext(this::testInSecurityContext);
  }

  @SuppressWarnings("restriction")
  private ConnectionTestResult testInSecurityContext() {
    return new ApplicationContext(app).getInContext(this::testInAppContext);
  }

  @SuppressWarnings("restriction")
  private ConnectionTestResult testInAppContext() {
    var clientPmv = findClientPmv();
    return new ProcessModelVersionContext(clientPmv.get()).getInContext(this::testInPmvContext);
  }

  @SuppressWarnings("restriction")
  private Optional<IProcessModelVersion> findClientPmv() {
    var restManager = ch.ivyteam.ivy.rest.client.config.restricted.IRestClientsManager.instance();
    return app.getProcessModels().stream()
        .map(IProcessModel::getReleasedProcessModelVersion)
        .filter(pmv -> restManager.getProjectDataModelFor(pmv).findRestClient(uiClient.uniqueId()).isPresent())
        .findAny();
  }

  private ConnectionTestResult testInPmvContext() {
    try {
      var client = createClient();
      try (var response = client.request().head()) {
        StatusType status = response.getStatusInfo();
        return toTestResult(status);
      }
    } catch (ProcessingException ex) {
      return new ConnectionTestResult("", 0, TestResult.ERROR, "Invalid Url (may contains script context)\n"
          + "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }

  @SuppressWarnings("restriction")
  private WebTarget createClient() {
    return new ch.ivyteam.ivy.rest.client.internal.ExternalRestWebService(app, uiClient)
        .getWebTargetFactory()
        .create();
  }

  static ConnectionTestResult toTestResult(StatusType status) {
    var code = status.getStatusCode();
    Family family = status.getFamily();
    if (Family.SUCCESSFUL.equals(family) || Family.REDIRECTION.equals(family)) {
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
