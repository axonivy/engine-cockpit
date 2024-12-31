package ch.ivyteam.enginecockpit.system.ssl;

import java.io.InputStream;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;

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
    FacesContext.getCurrentInstance().addMessage("sslTruststoreSaveSuccess",
        new FacesMessage("Trust Store configurations saved"));
  }

  @Override
  public void deleteCertificate(String alias) {
    getKeyStoreUtils().deleteCertificate(alias);
  }

  @Override
  public Certificate handleUploadCertificate(FileUploadEvent event) throws Exception {
    try (InputStream is = event.getFile().getInputStream()) {
      return getKeyStoreUtils().handleUploadCert(is);
    }
  }

  public void addToStore(Certificate cert) {
    getKeyStoreUtils().addNewCert(cert);
    if ("X.509".equals(cert.getPublicKey().getFormat())) {
      X509Certificate X509cert = (X509Certificate) cert;
      FacesContext.getCurrentInstance().addMessage("addMissingCertSuccess",
          new FacesMessage(X509cert.getSubjectX500Principal() + " was successfully added"));
    } else {
      FacesContext.getCurrentInstance().addMessage("addMissingCertSuccess",
          new FacesMessage("The certificate was successfully added."));
    }
  }

  @Override
  public List<StoredCert> getCertificats() {
    return getKeyStoreUtils().getStoredCerts();
  }

  private KeyStoreUtils getKeyStoreUtils() {
    return new KeyStoreUtils(file, type, provider, password);
  }
}
