package ch.ivyteam.enginecockpit.system.ssl;

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

  static Optional<IvyKeystore> load(String file, String type, String provider, String password) {
    try {
      var tmpKS = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(file, type,
              provider, password.toCharArray());
      return Optional.of(tmpKS);
    } catch (Exception ex) {
      KeyStoreUtils.LOGGER.error("failed to load keystore " + file, ex);
      return Optional.empty();
    }
  }

  static Certificate handleUploadCert(FileUploadEvent event, String file, String type, String provider, String password) throws Exception {
    var certFactory = CertificateFactory.getInstance("X509");
    Certificate certFile = certFactory.generateCertificate(event.getFile().getInputStream());
    var store = load(file, type, provider, password);
    if (store.isPresent()) {
      store.get()
        .addCert(certFile)
        .store(file, password.toCharArray());
    }
    return certFile;
  }

  static List<StoredCert> getStoredCerts(IvyKeystore ivyKeystore) {
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

  static void deleteCertificate(IvyKeystore tmpKS, String alias, String file, String password) {
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