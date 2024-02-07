package ch.ivyteam.enginecockpit.system.ssl;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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

  public boolean isLdaps(String Uri) {
    return Uri.startsWith("ldaps");
  }

  public void fileDownloadView() {
    TLSTest store = new TLSTest(testResult, "https://test-webservices.ivyteam.io:8090/api/v3");
    var certs = store.getMissingCerts();
    var aliases = store.getAliase();
    var alias = aliases.get(2);
    var certificate = certs.get(2);
    try {
        ByteArrayInputStream stream = new ByteArrayInputStream(certificate.getEncoded());

        file = DefaultStreamedContent.builder()
                .name(alias + ".crt")
                .contentType("application/x-x509-ca-cert")
                .stream(() -> stream)
                .build();

    } catch (CertificateEncodingException e) {
        e.printStackTrace();
    }
  }

  private StreamedContent file;

  public StreamedContent getFile() {
      return file;
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
      return "remove-circle state-inactive";
    }
    if (result.contains("1")) {
      return "check-circle-1 state-active";
    }
    return "question-circle state-unused";
  }

}
