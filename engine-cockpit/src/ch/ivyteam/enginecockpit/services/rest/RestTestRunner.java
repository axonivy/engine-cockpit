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
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.restricted.di.ApplicationContext;
import ch.ivyteam.ivy.application.restricted.di.ProcessModelVersionContext;
import ch.ivyteam.ivy.environment.Ivy;
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

  private ConnectionTestResult testInSecurityContext() {
    return new ApplicationContext(app).getInContext(this::testInAppContext);
  }

  private ConnectionTestResult testInAppContext() {
    var clientPmv = findClientPmv();
    String invalidUrlMsg = Ivy.cm().co("/connectionTestResult/InvalidRestClientUrlMessage");
    String successMsg = Ivy.cm().co("/connectionTestResult/ConnectToRestServiceSuccessMessage");
    String notUnderstandRequestMsg = Ivy.cm().co("/connectionTestResult/ConnectToRestServiceSuccessMessage");
    String authMsg = Ivy.cm().co("/connectionTestResult/ConnectionTestRestClientAuthenticationMessage");
    String failMsg = Ivy.cm().co("/connectionTestResult/ConnectToRestServiceFailMessage");
    return new ProcessModelVersionContext(clientPmv.get())
        .getInContext(() -> testInPmvContext(invalidUrlMsg, successMsg, notUnderstandRequestMsg, authMsg, failMsg));
  }

  private Optional<IProcessModelVersion> findClientPmv() {
    var restManager = ch.ivyteam.ivy.rest.client.config.restricted.IRestClientsManager.instance();
    return app.getProcessModelVersions()
        .filter(pmv -> restManager.getProjectDataModelFor(pmv).findRestClient(uiClient.uniqueId()).isPresent())
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
