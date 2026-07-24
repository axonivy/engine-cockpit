package ch.ivyteam.enginecockpit.services.rest;

import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.ivy.application.app.Application;
import ch.ivyteam.ivy.application.app.context.ApplicationContext;
import ch.ivyteam.ivy.application.project.Project;
import ch.ivyteam.ivy.application.project.context.ProjectContext;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.security.di.SecurityContextContext;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response.Status.Family;
import jakarta.ws.rs.core.Response.StatusType;

public class RestTestRunner {

  private final Application app;
  private final RestClient uiClient;

  public RestTestRunner(Application app, RestClient uiClient) {
    this.app = app;
    this.uiClient = uiClient;
  }

  public ConnectionTestResult test() {
    return new SecurityContextContext(app.securityContext()).getInContext(this::testInSecurityContext);
  }

  private ConnectionTestResult testInSecurityContext() {
    return new ApplicationContext(app).getInContext(this::testInAppContext);
  }

  private ConnectionTestResult testInAppContext() {
    var clientProject = findClientProject();
    String invalidUrlMsg = Ivy.cm().co("/connectionTestResult/InvalidRestClientUrlMessage");
    String successMsg = Ivy.cm().co("/connectionTestResult/ConnectToRestServiceSuccessMessage");
    String notUnderstandRequestMsg = Ivy.cm().co("/connectionTestResult/ConnectToRestServiceSuccessMessage");
    String authMsg = Ivy.cm().co("/connectionTestResult/ConnectionTestRestClientAuthenticationMessage");
    String failMsg = Ivy.cm().co("/connectionTestResult/ConnectToRestServiceFailMessage");
    return new ProjectContext(clientProject.get())
        .getInContext(() -> testInPmvContext(invalidUrlMsg, successMsg, notUnderstandRequestMsg, authMsg, failMsg));
  }

  private Optional<Project> findClientProject() {
    var restManager = ch.ivyteam.ivy.rest.client.config.restricted.IRestClientsManager.instance();
    return app.projects().all()
        .filter(pmv -> restManager.getProjectDataModelFor(pmv.model()).findRestClient(uiClient.key()).isPresent())
        .findAny();
  }

  private ConnectionTestResult testInPmvContext(String invalidUrlMsg, String successMsg, String notUnderstandRequestMsg,
      String authMsg, String failMsg) {
    try {
      var client = createClient();
      try (var response = client.request().head()) {
        StatusType status = response.getStatusInfo();
        return toTestResult(status, successMsg, notUnderstandRequestMsg, authMsg, failMsg);
      }
    } catch (ProcessingException ex) {
      return new ConnectionTestResult("", 0, TestResult.ERROR, invalidUrlMsg + ExceptionUtils.getStackTrace(ex));
    }
  }

  private WebTarget createClient() {
    return new ch.ivyteam.ivy.rest.client.internal.ExternalRestWebService(app, uiClient)
        .getWebTargetFactory()
        .create();
  }

  static ConnectionTestResult toTestResult(StatusType status, String successMsg, String notUnderstandRequestMsg,
      String authMsg, String failMsg) {
    var code = status.getStatusCode();
    Family family = status.getFamily();
    if (Family.SUCCESSFUL.equals(family) || Family.REDIRECTION.equals(family)) {
      return new ConnectionTestResult("HEAD", code, TestResult.SUCCESS, successMsg);
    }
    if (code == 400) {
      return new ConnectionTestResult("HEAD", code, TestResult.WARNING, notUnderstandRequestMsg);
    } else if (code == 401) {
      return new ConnectionTestResult("HEAD", code, TestResult.WARNING, authMsg);
    }
    return new ConnectionTestResult("HEAD", code, TestResult.ERROR, failMsg);
  }
}
