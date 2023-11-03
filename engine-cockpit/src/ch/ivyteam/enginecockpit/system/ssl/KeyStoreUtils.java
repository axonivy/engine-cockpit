package ch.ivyteam.enginecockpit.system.ssl;

import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;

import ch.ivyteam.ivy.ssl.restricted.IvyKeystore;
import ch.ivyteam.log.Logger;

@SuppressWarnings("restriction")
class KeyStoreUtils {

  private static final Logger LOGGER = Logger.getLogger(KeyStoreUtils.class);

  private final String file;
  private final String type;
  private final String provider;
  private final char[] password;

  KeyStoreUtils(String file, String type, String provider, String password) {
    this.file = file;
    this.type = type;
    this.provider = provider;
    this.password = password.toCharArray();
  }

  private IvyKeystore loadInternal() throws KeyStoreException {
    return ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(file, type, provider, password);
  }

  Certificate handleUploadCert(InputStream is) throws Exception {
    var certFactory = CertificateFactory.getInstance("X509");
    Certificate cert = certFactory.generateCertificate(is);
    addNewCert(cert);
    return cert;
  }

  private void addNewCert(Certificate certFile) {
    try {
      var ivyKS = loadInternal();
      ivyKS.addCert(certFile);
      ivyKS.store(password);
    } catch (KeyStoreException ex) {
      LOGGER.error("failed to add certificat to "+file, ex);
    }
  }

  List<StoredCert> getStoredCerts() {
    try {
      var certs = loadInternal().getStoredCertificates();
      return certs.stream()
        .map(cert -> new StoredCert(cert.alias(), cert.cert()))
        .toList();
    } catch (KeyStoreException ex) {
      LOGGER.error("failed to read certificates of "+file, ex);
      return List.of();
    }
  }

  void deleteCertificate(String alias) {
    try {
      var ivyKS = loadInternal();
      ivyKS.getKeyStore().deleteEntry(alias);
      ivyKS.store(password);
    } catch (Exception ex) {
      LOGGER.error("failed to delete " + alias, ex);
    }
  }

}
