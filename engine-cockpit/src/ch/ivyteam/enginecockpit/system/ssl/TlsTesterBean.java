package ch.ivyteam.enginecockpit.system.ssl;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.ssl.restricted.tester.TLSTest;
import ch.ivyteam.ivy.ssl.restricted.tester.TLSTestData;

@ManagedBean
@ViewScoped
public class TlsTesterBean {

  public boolean tlsTestRendered;
  public List<TLSTestData> testResult = new ArrayList<>();

  public boolean isHttps(String Uri) {
    return Uri.startsWith("https");
  }

  public void testConnection(String targetUri) {
    testResult.clear();
    setTlsTestRendered(true);
    TLSTest test = new TLSTest(testResult, targetUri);
    test.runTLSTests();
  }
  public List<TLSTestData> getTestResult() {
    return testResult;
  }

  public boolean isTlsTestRendered() {
    return tlsTestRendered;
  }

  public void setTlsTestRendered(boolean tlsTestRendered) {
    this.tlsTestRendered = tlsTestRendered;
  }

  public boolean isExecuted(String logEntry) {
    return !logEntry.contains("2");
}

  public String backgroundColor(String logEntry) {
    if (logEntry.contains("2")) {
      return "unused-column";
    }
    return "";
  }

  public String icon(String result) {
    if (result.contains("0")) {
      return "check-circle-1 state-active";
    }
    if (result.contains("1")) {
      return "remove-circle state-inactive";
    }
    return "question-circle state-unused";
  }

}
