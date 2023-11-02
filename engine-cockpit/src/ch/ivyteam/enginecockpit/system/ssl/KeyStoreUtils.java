package ch.ivyteam.enginecockpit.system.ssl;

import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.primefaces.event.FileUploadEvent;

import ch.ivyteam.log.Logger;

class KeyStoreUtils {

  static final Logger LOGGER = Logger.getLogger(KeyStoreUtils.class);

  @SuppressWarnings("restriction")
  static Certificate handleUploadCert(FileUploadEvent event, String file, String type, String provider, String password) throws Exception {
    var certFactory = CertificateFactory.getInstance("X509");
    Certificate certFile = certFactory.generateCertificate(event.getFile().getInputStream());
    var store = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(file, type, provider, password.toCharArray());
    store.addCert(certFile).store(file, password.toCharArray());
    return certFile;
  }

  @SuppressWarnings("restriction")
  static List<StoredCert> getStoredCerts(ch.ivyteam.ivy.ssl.restricted.IvyKeystore ivyKeystore) {
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

  @SuppressWarnings("restriction")
  static void deleteCertificate(ch.ivyteam.ivy.ssl.restricted.IvyKeystore tmpKS, String alias, String file, String password) {
    try {
      tmpKS.getKeyStore().deleteEntry(alias);
    } catch (Exception ex) {
      LOGGER.error("failed to delete " + alias, ex);
    }
    try {
      tmpKS.store(file, password.toCharArray());
    } catch (Exception ex) {
      LOGGER.error("failed to load " + alias, ex);
    }
  }


}