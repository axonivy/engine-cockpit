package ch.ivyteam.enginecockpit.services.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.apache.commons.lang.StringUtils.EMPTY;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;

class TestRestTestRunner {

  @Test
  void resultOk() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.OK, EMPTY, EMPTY, EMPTY, EMPTY);
    assertThat(result.getTestResult()).isEqualTo("Success");
  }

  @Test
  void resultOkRedirected() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.TEMPORARY_REDIRECT, EMPTY, EMPTY, EMPTY, EMPTY);
    assertThat(result.getTestResult()).isEqualTo("Success");
  }

  @Test
  void resultError() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.INTERNAL_SERVER_ERROR, EMPTY, EMPTY, EMPTY, EMPTY);
    assertThat(result.getTestResult()).isEqualTo("Error");
  }

  @Test
  void resultAuthenificationError() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.UNAUTHORIZED, EMPTY, EMPTY, EMPTY, EMPTY);
    assertThat(result.getTestResult()).isEqualTo("Warning");
  }

  @Test
  void resultBadRequest() {
    ConnectionTestResult result = RestTestRunner.toTestResult(Status.BAD_REQUEST, EMPTY, EMPTY, EMPTY, EMPTY);
    assertThat(result.getTestResult()).isEqualTo("Warning");
  }
}
