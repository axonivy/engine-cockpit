package ch.ivyteam.enginecockpit.system.ssl;

import java.io.InputStream;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;

import ch.ivyteam.enginecockpit.system.ssl.SslClientConfig.TrustStoreConfig;

@ManagedBean
@ViewScoped
public class TrustStoreBean {

  private final TrustStoreConfig store;

  private String file;
  private String password;
  private String provider;
  private String type;
  private String algorithm;
  private String enableInsecureSSL;

  public TrustStoreBean() {
    this.store = new SslClientConfig().getTrustStore();
    this.file = store.getFile();
    this.password = store.getPassword();
    this.provider = store.getProvider();
    this.type = store.getType();
    this.algorithm = store.getAlgorithm();
    this.enableInsecureSSL = store.getEnableInsecureSSL();
  }

  public String getFile() {
    return file;
  }

  public String getPassword() {
    return password;
  }

  public String getProvider() {
    return provider;
  }

  @SuppressWarnings("hiding")
  public List<String> getProviders() {
    List<String> providers = new ArrayList<>(Arrays.stream(Security.getProviders())
            .map(provider -> provider.getName())
            .toList());
    providers.add("");
    return providers;
  }

  public String getType() {
    return type;
  }

  public List<String> getTypes() {
      List<String> types = new ArrayList<>();
      types.addAll(SecurityProviders.getTypes(getProvider()));
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
    this.password = password;
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
    store.setPassword(password);
    store.setProvider(provider);
    store.setType(type);
    store.setAlgorithm(algorithm);
    store.setEnableInsecureSSL(enableInsecureSSL);
    FacesContext.getCurrentInstance().addMessage("sslTruststoreSaveSuccess",
            new FacesMessage("Trust Store configurations saved"));
  }

  public void deleteTrustCertificate(String alias) {
    getKeyStoreUtils().deleteCertificate(alias);
  }

  public Certificate handleUploadTrustCert(FileUploadEvent event) throws Exception {
    try (InputStream is = event.getFile().getInputStream()) {
      return getKeyStoreUtils().handleUploadCert(is);
    }
  }

  public List<StoredCert> getStoredCerts() {
    return getKeyStoreUtils().getStoredCerts();
  }

  private KeyStoreUtils getKeyStoreUtils() {
    return new KeyStoreUtils(file, type, provider, password);
  }
}
