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
  private boolean disableAddUntrust;

  public boolean isHttps(String Uri) {
    return Uri.startsWith("https");
  }

  public boolean isLdaps(String Uri) {
    return Uri.startsWith("ldaps");
  }

  public void testConnection(String targetUri) {
    testResult.clear();
    missingCert = Optional.empty();
    setDisableAddUntrust(false);
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

  public String getMissingCertsSubject() {
    return missingCert
            .map(X509Certificate::getSubjectX500Principal)
            .map(principal -> principal.getName())
            .orElse("No certificate present");
  }

  public X509Certificate getMissingCert() {
    return missingCert.orElse(null);
  }

  public boolean isDisableAddUntrust() {
    return disableAddUntrust;
  }

  public void setDisableAddUntrust(boolean disableAddUntrust) {
    this.disableAddUntrust  = disableAddUntrust;
  }

  public String getFormatedCert() {
    if (getMissingCert() == null) {
      return "";
    }
    return formatCertificateInfo(getMissingCert());
  }

  String formatCertificateInfo(X509Certificate cert) {
    String signatureAlgorithm = cert.getSigAlgName();
    String validity = cert.getNotBefore() + " - " + cert.getNotAfter();
    String issuer = cert.getIssuerX500Principal().getName();
    String key = cert.getPublicKey().getFormat();
    return String.format(
            "Issuer: %s \n Signature Algorithm: %s \n Validity: %s \n Public Key Format: %s \n",
            issuer, signatureAlgorithm, validity, key);
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
