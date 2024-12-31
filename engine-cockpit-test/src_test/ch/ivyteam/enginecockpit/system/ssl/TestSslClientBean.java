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
    assertThat(shortSubject("E=alexander.suter@axonivy.com,CN=Alexander Suter,OU=ivyTeam")).isEqualTo("Alexander Suter");
    assertThat(shortSubject("CN=,OU=ivyteam")).isEqualTo("");
    assertThat(shortSubject("")).isEqualTo("");
  }

  @Test
  void extractSubject() {
    TlsTesterBean bean = new TlsTesterBean();
    assertThat(bean.getSubject("Cert alias found: CN=DigiCert Assured ID Root CA,OU=www.digicert.com,O=DigiCert Inc,C=US alg=RSA, length=2048"))
        .isEqualTo("CN=DigiCert Assured ID Root CA,OU=www.digicert.com,O=DigiCert Inc,C=US");
    assertThat(bean.getSubject("CN=AffirmTrust Commercial,O=AffirmTrust,C=US"))
        .isEqualTo("CN=AffirmTrust Commercial,O=AffirmTrust,C=US");
    assertThat(bean.getSubject("CN=QuoVadis Root CA 2,O=QuoVadis Limited,C=BM alg=RSA"))
        .isEqualTo("CN=QuoVadis Root CA 2,O=QuoVadis Limited,C=BM");
    assertThat(bean.getSubject("OU=certSIGN ROOT CA G2,O=CERTSIGN SA,C=RO alg=RSA, length=4096"))
        .isEqualTo("OU=certSIGN ROOT CA G2,O=CERTSIGN SA,C=RO");
  }

}
