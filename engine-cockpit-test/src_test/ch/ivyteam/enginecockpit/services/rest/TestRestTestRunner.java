package ch.ivyteam.enginecockpit.services.rest;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;

class TestRestTestRunner {

  @Test
  void resultOk() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.OK);
    assertThat(result.getTestResult()).isEqualTo("Success");
  }

  @Test
  void resultOkRedirected() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.TEMPORARY_REDIRECT);
    assertThat(result.getTestResult()).isEqualTo("Success");
  }

  @Test
  void resultError() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.INTERNAL_SERVER_ERROR);
    assertThat(result.getTestResult()).isEqualTo("Error");
  }

  @Test
  void resultAuthenificationError() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.UNAUTHORIZED);
    assertThat(result.getTestResult()).isEqualTo("Warning");
  }

  @Test
  void resultBadRequest() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.BAD_REQUEST);
    assertThat(result.getTestResult()).isEqualTo("Warning");
  }
}
