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
}
