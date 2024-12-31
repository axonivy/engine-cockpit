package ch.ivyteam.enginecockpit.system.ssl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.ssl.restricted.tester.TLSTest;
import ch.ivyteam.ivy.ssl.restricted.tester.TLSTestData;

@ManagedBean
@ViewScoped
public class TlsTesterBean {

  public boolean tlsTestRendered;
  public List<TLSTestData> testResult = new ArrayList<>();
  public List<String> infos;
  private Optional<X509Certificate> missingCert = Optional.empty();

  public boolean isHttps(String Uri) {
    return Uri.startsWith("https");
  }

  public boolean isLdaps(String Uri) {
    return Uri.startsWith("ldaps");
  }

  public boolean isJdbc(String Uri) {
    return Uri.startsWith("jdbc");
  }

  public void testConnection(String targetUri) {
    testResult.clear();
    missingCert = Optional.empty();
    setTlsTestRendered(true);
    TLSTest test = new TLSTest(testResult, targetUri);
    test.runTLSTests();
    missingCert = test.getMissingCert();
    infos = test.getExtendedInformations();
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

  public boolean hasExtendedInformations(String logEntry) {
    return !getExtendedInformations(logEntry).isBlank();
  }

  public String getExtendedInformations(String logEntry) {
    return infos.stream()
        .filter(info -> info.contains(getSubject(logEntry)))
        .findFirst()
        .orElse("");
  }

  String getSubject(String inputStrings) {
    String[] parts = inputStrings.split("alg=");
    if (parts.length > 1) {
      return parts[0].replace("Cert alias found: ", "").trim();
    }
    return inputStrings;
  }

  public boolean hasMissingCerts(String Uri) {
    if (!(getMissingCert() == null) && isHttps(Uri) && tlsTestRendered) {
      return true;
    }
    return false;
  }

  public X509Certificate getMissingCert() {
    return missingCert.orElse(null);
  }

  public void clearMissingCert() {
    missingCert = Optional.empty();
  }

  public String icon(String result) {
    if (result.contains("0")) {
      return "remove-circle state-inactive";
    }
    if (result.contains("1")) {
      return "check-circle-1 state-active";
    }
    return "question-circle state-unused";
  }
}
