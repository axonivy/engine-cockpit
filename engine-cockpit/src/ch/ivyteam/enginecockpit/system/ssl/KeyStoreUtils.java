package ch.ivyteam.enginecockpit.system.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
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

  Certificate handleUploadCert(InputStream is) throws Exception {
//    try {
      var certFactory = CertificateFactory.getInstance("X509");
      Certificate cert = certFactory.generateCertificate(is);
      addNewCert(cert);
      return cert;
//    } finally {
//
//      String keyContent = readInputStreamAsString(is);
//      PrivateKey privateKey = parsePrivateKey(keyContent);
//
//      addNewPrivateKey(privateKey);
//    }
//    return null;
  }

  private PrivateKey parsePrivateKey(String keyContent) throws Exception {
    String privateKeyPEM = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "").replace("-----BEGIN RSA PRIVATE KEY-----", "")
        .replace("-----END RSA PRIVATE KEY-----", "").replace("-----BEGIN EC PRIVATE KEY-----", "")
        .replace("-----END EC PRIVATE KEY-----", "").replace("-----BEGIN CERTIFICATE REQUEST-----", "")
        .replace("-----END CERTIFICATE REQUEST-----", "").replaceAll("\\s", "");

    var keyBytes = Base64.getDecoder().decode(privateKeyPEM);
    return tryParsePrivateKey(keyBytes);
  }

  private PrivateKey tryParsePrivateKey(byte[] keyBytes) throws Exception {
    KeyFactory keyFactory;

    try {
      PKCS8EncodedKeySpec key = new PKCS8EncodedKeySpec(keyBytes);

      try {
        keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(key);
      } catch (Exception e) {
        throw new Exception("Unable to parse private key.", e);
      }
    } catch (Exception e) {
      throw new Exception("Unable to parse private key.", e);
    }
  }

  private String readInputStreamAsString(InputStream is) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      StringBuilder content = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
      }
      return content.toString();
    }
  }

  public void addNewPrivateKey(PrivateKey privateKey) throws Exception {
    var ivyKS = loadInternal();
    var certFactory = CertificateFactory.getInstance("X509");

    try (var resource = KeyStoreUtils.class.getResourceAsStream("jiraaxonivycom.crt")) {
      var certFile = certFactory.generateCertificate(resource);
      Certificate[] certChain = { certFile };
      ivyKS.addCert(privateKey, password, certChain);
      ivyKS.store(password);
    }
  }

  public void addNewCert(Certificate certFile) throws KeyStoreException {
    var ivyKS = loadInternal();
    ivyKS.addCert(certFile);
    ivyKS.store(password);
  }

  List<StoredCert> getStoredCerts() throws KeyStoreException {
    var certs = loadInternal().getStoredCertificates();
    return certs.stream().map(cert -> new StoredCert(cert.alias(), cert.cert())).toList();
  }

  void deleteCertificate(String alias) throws KeyStoreException {
    var ivyKS = loadInternal();
    ivyKS.getKeyStore().deleteEntry(alias);
    ivyKS.store(password);
  }

}
