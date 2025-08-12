package ch.ivyteam.enginecockpit.system.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.ssl.restricted.SslClientSettings;
import ch.ivyteam.ivy.ssl.restricted.SslClientSettings.TrustStoreConfig;

@ManagedBean
@ViewScoped
public class TrustStoreBean implements SslTableStore {

  private final TrustStoreConfig store;

  private String file;
  private char[] password;
  private String provider;
  private String type;
  private String algorithm;
  private String enableInsecureSSL;
  private final SslClientSettings sslClientSettings;

  private String certificateLoadError;

  public TrustStoreBean() {
    this.sslClientSettings = SslClientSettings.instance();
    this.store = sslClientSettings.getTrustStore();
    this.file = store.getFile();
    this.password = store.getPassword();
    this.provider = store.getProvider();
    this.type = store.getType();
    this.algorithm = store.getAlgorithm();
    this.enableInsecureSSL = sslClientSettings.getEnableInsecureSSL();
  }

  public String getFile() {
    return file;
  }

  public String getPassword() {
    return String.valueOf(password);
  }

  public String getProvider() {
    return provider;
  }

  @SuppressWarnings("hiding")
  public List<String> getProviders() {
    List<String> providers = new ArrayList<>(Arrays.stream(Security.getProviders())
        .map(Provider::getName)
        .toList());
    providers.add("");
    return providers;
  }

  public String getType() {
    return type;
  }

  public List<String> getTypes() {
    List<String> types = new ArrayList<>(SecurityProviders.getTypes(getProvider()));
    types.add("");
    return types;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public List<String> getAlgorithms() {
    return SecurityProviders.getAlgorithms("TrustManagerFactory");
  }

  public void setFile(String file) {
    this.file = file;
  }

  public void setPassword(String password) {
    if (password.isBlank()) {
      return;
    }
    this.password = password.toCharArray();
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getEnableInsecureSSL() {
    return enableInsecureSSL;
  }

  public void setEnableInsecureSSL(String enableInsecureSSL) {
    this.enableInsecureSSL = enableInsecureSSL;
  }

  public void saveTrustStore() {
    store.setFile(file);
    store.setPassword(String.valueOf(password));
    store.setProvider(provider);
    store.setType(type);
    store.setAlgorithm(algorithm);
    sslClientSettings.setEnableInsecureSSL(enableInsecureSSL);
    getCertificats();
    Message.info()
        .clientId("sslTruststoreSaveSuccess")
        .summary("Trust Store configurations saved")
        .show();
  }

  @Override
  public void deleteCertificate(String alias) throws KeyStoreException {
    getKeyStoreUtils().deleteCertificate(alias);
    Message.info()
        .clientId("sslDeleteCertificate")
        .summary("Certificate " + "'" + alias + "'" + "deleted")
        .show();
  }

  @Override
  public Certificate handleUploadCertificate(FileUploadEvent event)
      throws CertificateException, KeyStoreException, IOException {
    try (InputStream is = event.getFile().getInputStream()) {
      return getKeyStoreUtils().handleUploadCert(is);
    }

  }

  public void addToStore(Certificate cert) throws KeyStoreException {
      getKeyStoreUtils().addNewCert(cert);
    if ("X.509".equals(cert.getPublicKey().getFormat())) {
      X509Certificate X509cert = (X509Certificate) cert;
      Message.info()
          .clientId("addMissingCertSuccess")
          .summary(X509cert.getSubjectX500Principal() + " was successfully added")
          .show();
    } else {
      Message.info()
          .clientId("addMissingCertSuccess")
          .summary("The certificate was successfully added.")
          .show();
    }
  }

  public List<StoredCert> getCertificats() {
    try {
      setCertificateLoadError(null);
      return getKeyStoreUtils().getStoredCerts();
    } catch (KeyStoreException ex) {
      if (ex.getCause() instanceof IOException) {
        setCertificateLoadError(ex.getMessage());
      } else {
        setCertificateLoadError(ex.getCause().getMessage());
      }
      return List.of();
    }
  }

  @PostConstruct
  public void init() {
    getCertificats();
  }

  private KeyStoreUtils getKeyStoreUtils() {
    return new KeyStoreUtils(file, type, provider, password);
  }

  public String getCertificateLoadError() {
    return certificateLoadError;
  }

  public void setCertificateLoadError(String certificateLoadError) {
    this.certificateLoadError = certificateLoadError;
  }

}
