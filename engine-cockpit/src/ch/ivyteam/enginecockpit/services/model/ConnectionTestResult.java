package ch.ivyteam.enginecockpit.services.model;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

public class ConnectionTestResult {
  private final String method;
  private final int statusCode;
  private final TestResult restResult;
  private final String message;

  public ConnectionTestResult(String method, int statusCode, TestResult testResult, String message) {
    this.method = method;
    this.statusCode = statusCode;
    this.restResult = testResult;
    this.message = message;
  }

  public String getMessage() {
    if (StringUtils.isNotBlank(method) && statusCode != 0) {
      var reason = Status.fromStatusCode(statusCode);
      return message + " (Method " + method + " >> Status " + statusCode + " " + reason + ")";
    }
    return message;
  }

  public String getTestResult() {
    return restResult.name;
  }

  public String getTestResultColor() {
    return restResult.color;
  }

  public interface IConnectionTestResult {
    ConnectionTestResult getResult();
  }

  public enum TestResult {
    SUCCESS("Success", "green"), WARNING("Warning", "#FFC107"), ERROR("Error", "red");

    private final String name;
    private final String color;

    TestResult(String name, String color) {
      this.name = name;
      this.color = color;
    }
  }
}
