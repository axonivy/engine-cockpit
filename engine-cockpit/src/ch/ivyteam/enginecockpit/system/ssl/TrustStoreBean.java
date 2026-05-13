package ch.ivyteam.enginecockpit.system.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
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
import ch.ivyteam.ivy.environment.Ivy;
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

  @Override
  public String getFile() {
    return file;
  }

  @Override
  public String getPassword() {
    return String.valueOf(password);
  }

  @Override
  public String getProvider() {
    return provider;
  }

  @Override
  @SuppressWarnings("hiding")
  public List<String> getProviders() {
    List<String> providers = new ArrayList<>(Arrays.stream(Security.getProviders())
        .map(Provider::getName)
        .toList());
    providers.add("");
    return providers;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public List<String> getTypes() {
    List<String> types = new ArrayList<>(SecurityProviders.getTypes(getProvider()));
    types.add("");
    return types;
  }

  @Override
  public String getAlgorithm() {
    return algorithm;
  }

  @Override
  public List<String> getAlgorithms() {
    return SecurityProviders.getAlgorithms("TrustManagerFactory");
  }

  @Override
  public void setFile(String file) {
    this.file = file;
  }

  @Override
  public void setPassword(String password) {
    if (password.isBlank()) {
      return;
    }
    this.password = password.toCharArray();
  }

  @Override
  public void setProvider(String provider) {
    this.provider = provider;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getEnableInsecureSSL() {
    return enableInsecureSSL;
  }

  public void setEnableInsecureSSL(String enableInsecureSSL) {
    this.enableInsecureSSL = enableInsecureSSL;
  }

  @Override
  public void save() {
    store.setFile(file);
    store.setPassword(String.valueOf(password));
    store.setProvider(provider);
    store.setType(type);
    store.setAlgorithm(algorithm);
    sslClientSettings.setEnableInsecureSSL(enableInsecureSSL);
    getCertificats();
    Message.info()
        .clientId("sslTruststoreSaveSuccess")
        .summary(Ivy.cm().co("/sslTrustStore/TrustStoreConfigurationsSavedMessage"))
        .show();
  }

  @Override
  public void deleteCertificate(String alias) throws KeyStoreException {
    getKeyStoreUtils().deleteCertificate(alias);
    Message.info()
        .clientId("sslDeleteCertificate")
        .summary(Ivy.cm().content("/sslTrustStore/DeleteCertificateMessage").replace("certificate", alias).get())
        .show();
  }

  @Override
  public void handleUploadCertificate(FileUploadEvent event)
      throws CertificateException, KeyStoreException, IOException {
    try (InputStream is = event.getFile().getInputStream()) {
      getKeyStoreUtils().handleUploadCert(is);
    }
  }

  @Override
  public boolean isKeystore() {
    return false;
  }

  public void addToStore(Certificate cert) throws KeyStoreException {
      getKeyStoreUtils().addNewCert(cert);
      if ("X.509".equals(cert.getPublicKey().getFormat())) {
        X509Certificate X509cert = (X509Certificate) cert;
        Message.info().clientId("addMissingCertSuccess")
            .summary(Ivy.cm().content("/tlsTesterMissingCertView/AddX509CertificateToTruststoreSuccessMesage")
                .replace("certificate", String.valueOf(X509cert.getSubjectX500Principal())).get())
            .show();
      } else {
        Message.info().clientId("addMissingCertSuccess")
            .summary(Ivy.cm().co("/tlsTesterMissingCertView/AddCertificateToTruststoreSuccessMesage")).show();
      }
  }

  @Override
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

  @Override
  public String getCertificateLoadError() {
    return certificateLoadError;
  }

  @Override
  public void setCertificateLoadError(String certificateLoadError) {
    this.certificateLoadError = certificateLoadError;
  }

  @Override
  public String getStorePassword() {
    return "";
  }

  @Override
  public String getStoreKeyPassword() {
    return "";
  }

  @Override
  public void confirmPassword()
      throws CertificateException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException {}

}
