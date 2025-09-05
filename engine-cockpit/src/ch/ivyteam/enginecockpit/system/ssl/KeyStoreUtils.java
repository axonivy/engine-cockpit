package ch.ivyteam.enginecockpit.system.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collections;
import java.util.List;

import ch.ivyteam.ivy.ssl.restricted.IvyKeystore;
import ch.ivyteam.ivy.ssl.restricted.PrivateKey;

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

  void handleUploadstore(InputStream is, char[] storePassword, char[] keyPassword)
      throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
    addCertsFromStore(is, storePassword, keyPassword);
  }

  void handleUploadCert(InputStream is) throws CertificateException, KeyStoreException {
    var certFactory = CertificateFactory.getInstance("X509");
    Certificate cert = certFactory.generateCertificate(is);
    addNewCert(cert);
  }

  public void addNewCert(Certificate certFile) throws KeyStoreException {
    var ivyKS = loadInternal();
    ivyKS.addCert(certFile);
    ivyKS.store(password);
  }

  private void addCertsFromStore(InputStream inputStream, char[] storePassword, char[] keyPassword)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
    var importedKeystore = KeyStore.getInstance(type);
    importedKeystore.load(inputStream, storePassword);

    var aliases = Collections.list(importedKeystore.aliases());

    for (String alias : aliases) {
      store(storePassword, importedKeystore, alias, keyPassword);
    }
  }

  private void store(char[] storePassword, KeyStore importedKeystore, String alias , char[] keyPassword)
      throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException {
    var privateKey = (java.security.PrivateKey) importedKeystore.getKey(alias, keyPassword);
    var certChain = importedKeystore.getCertificateChain(alias);
    var ivyKS = loadInternal();
    if (privateKey != null && certChain != null) {
      var privateKeyRecord = new PrivateKey(privateKey, certChain, alias);
      ivyKS.addCert(privateKeyRecord, keyPassword);
    } else {
      ivyKS.addCert(importedKeystore.getCertificate(alias));
    }
    ivyKS.store(this.password);
  }

  List<StoredCert> getStoredCerts() throws KeyStoreException {
    var certs = loadInternal().getStoredCertificates();
    return certs.stream()
        .map(cert -> new StoredCert(cert.alias(), cert.cert(), cert.isPrivateKey()))
        .toList();
  }

  void deleteCertificate(String alias) throws KeyStoreException {
    var ivyKS = loadInternal();
    ivyKS.getKeyStore().deleteEntry(alias);
    ivyKS.store(password);
  }

}
