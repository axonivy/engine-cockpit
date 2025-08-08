package ch.ivyteam.enginecockpit.system.ssl;

import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

import ch.ivyteam.ivy.ssl.restricted.IvyKeystore;

class KeyStoreUtils {

  private final String file;
  private final String type;
  private final String provider;
  private final char[] password;

  KeyStoreUtils(String file, String type, String provider, char[] password) {
    this.file = file;
    this.type = type;
    this.provider = provider;
    this.password = password;
  }

  private IvyKeystore loadInternal() throws KeyStoreException {
    return ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(Path.of(file), type, provider, password);
  }

  Certificate handleUploadCert(InputStream is) throws CertificateException, KeyStoreException {
    var certFactory = CertificateFactory.getInstance("X509");
    Certificate cert = certFactory.generateCertificate(is);
    addNewCert(cert);
    return cert;
  }

  public void addNewCert(Certificate certFile) throws KeyStoreException {
    var ivyKS = loadInternal();
    ivyKS.addCert(certFile);
    ivyKS.store(password);
  }

  List<StoredCert> getStoredCerts() throws KeyStoreException {
    var certs = loadInternal().getStoredCertificates();
    return certs.stream()
        .map(cert -> new StoredCert(cert.alias(), cert.cert()))
        .toList();
  }

  void deleteCertificate(String alias) throws KeyStoreException {
    var ivyKS = loadInternal();
    ivyKS.getKeyStore().deleteEntry(alias);
    ivyKS.store(password);
  }

}
