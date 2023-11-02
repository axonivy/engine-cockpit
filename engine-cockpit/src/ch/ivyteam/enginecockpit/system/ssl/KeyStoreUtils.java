package ch.ivyteam.enginecockpit.system.ssl;

import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.primefaces.event.FileUploadEvent;

import ch.ivyteam.ivy.ssl.restricted.IvyKeystore;
import ch.ivyteam.log.Logger;

@SuppressWarnings("restriction")
class KeyStoreUtils {

  static final Logger LOGGER = Logger.getLogger(KeyStoreUtils.class);

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

  Optional<IvyKeystore> load() {
    try {
      var tmpKS = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(file, type, provider, password);
      return Optional.of(tmpKS);
    } catch (Exception ex) {
      KeyStoreUtils.LOGGER.error("failed to load keystore " + file, ex);
      return Optional.empty();
    }
  }

  Certificate handleUploadCert(FileUploadEvent event) throws Exception {
    var certFactory = CertificateFactory.getInstance("X509");
    try (InputStream is = event.getFile().getInputStream()) {
      Certificate cert = certFactory.generateCertificate(is);
      return addNewCert(cert);
    }
  }

  private Certificate addNewCert(Certificate certFile) throws KeyStoreException {
    var store = load();
    if (store.isPresent()) {
      store.get()
        .addCert(certFile)
        .store(file, password);
    }
    return certFile;
  }

  List<StoredCert> getStoredCerts() {
    var tmpKS = load();
    if (tmpKS.isEmpty()) {
      return List.of();
    }
    var ivyKeystore = tmpKS.get();
    try {
      List<String> list = Collections.list(ivyKeystore.getKeyStore().aliases());
      List<StoredCert> certificates = new ArrayList<>();
      for (String alias : list) {
        Certificate cert = ivyKeystore.getKeyStore().getCertificate(alias);
        if (cert instanceof X509Certificate) {
          var x509 = (X509Certificate) cert;
          certificates.add(new StoredCert(alias, x509));
        }
      }
      return certificates;
    } catch (KeyStoreException ex) {
      LOGGER.error("failed to read certificates of "+ivyKeystore, ex);
      return List.of();
    }
  }

  void deleteCertificate(String alias) {
    var tmpKS = load();
    if (tmpKS.isEmpty()) {
      return;
    }
    try {
      tmpKS.get().getKeyStore().deleteEntry(alias);
    } catch (Exception ex) {
      LOGGER.error("failed to delete " + alias, ex);
    }
    try {
      tmpKS.get().store(file, password);
    } catch (Exception ex) {
      LOGGER.error("failed to load " + alias, ex);
    }
  }

}
