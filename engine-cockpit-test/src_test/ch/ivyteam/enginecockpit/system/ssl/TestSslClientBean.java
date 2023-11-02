package ch.ivyteam.enginecockpit.system.ssl;

import static ch.ivyteam.enginecockpit.system.ssl.StoredCert.shortSubject;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.junit.jupiter.api.Test;

class TestSslClientBean {

  @Test
  void subject() throws CertificateException, IOException {
    var certFactory = CertificateFactory.getInstance("X509");

    try (var resource = TestSslClientBean.class.getResourceAsStream("jiraaxonivycom.crt")) {
      var certFile = certFactory.generateCertificate(resource);
      var cert = new StoredCert("ivy1", (X509Certificate) certFile);
      var subject = cert.getSubject();
      assertThat(subject).isEqualTo("jira.axonivy.com");
    }
  }

  @Test
  void subject_noCN() {
    assertThat(shortSubject("CN=jira.axonivy.com,OU=ivyteam")).isEqualTo("jira.axonivy.com");
    assertThat(shortSubject("jira.axonivy.com")).isEqualTo("jira.axonivy.com");
    assertThat(shortSubject("CN=,OU=ivyteam")).isEqualTo("");
    assertThat(shortSubject("")).isEqualTo("");
  }

}
