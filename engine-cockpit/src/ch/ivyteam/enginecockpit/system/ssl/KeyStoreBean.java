package ch.ivyteam.enginecockpit.system.ssl;

import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;

import ch.ivyteam.enginecockpit.system.ssl.SslClientConfig.KeyStoreConfig;

@ManagedBean
@ViewScoped
public class KeyStoreBean {

  private final KeyStoreConfig store;

  private boolean useCustomKeyStore;
  private String file;
  private String password;
  private String keyPassword;
  private String provider;
  private String type;
  private String algorithm;

  public KeyStoreBean() {
    this.store = new SslClientConfig().getKeyStore();
    this.file = store.getFile();
    this.password = store.getPassword();
    this.keyPassword = store.getKeyPassword();
    this.provider = store.getProvider();
    this.type = store.getType();
    this.algorithm = store.getAlgorithm();
  }

  public boolean isUseCustomKeyStore() {
    return useCustomKeyStore;
  }

  public void setUseCustomKeyStore(boolean useCustomKeyStore) {
    this.useCustomKeyStore = useCustomKeyStore;
  }

  public String getFile() {
    return file;
  }

  public String getPassword() {
    return password;
  }

  public String getKeyPassword() {
    return keyPassword;
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
    return SecurityProviders.getAlgorithms("KeyManagerFactory");
  }

  public List<StoredCert> getStoredKeyCerts() {
    return loadKeyStore()
      .map(KeyStoreUtils::getStoredCerts)
      .orElse(List.of());
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

  public void setKeyPassword(String keyPassword) {
    if (keyPassword.isBlank()) {
      return;
    }
    this.keyPassword = keyPassword;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public void saveKeyStore() {
    store.setFile(file);
    store.setPassword(password);
    store.setKeyPassword(keyPassword);
    store.setProvider(provider);
    store.setType(type);
    store.setAlgorithm(algorithm);
    FacesContext.getCurrentInstance().addMessage("sslKeystoreSaveSuccess",
            new FacesMessage("Key Store configurations saved"));
  }

  @SuppressWarnings("restriction")
  private Optional<ch.ivyteam.ivy.ssl.restricted.IvyKeystore> loadKeyStore() {
    return KeyStoreUtils.load(file, type, provider, password);
  }

  public void deleteKeyCertificate(String alias) {
    var tmpKS = loadKeyStore();
    KeyStoreUtils.deleteCertificate(tmpKS.get(), alias, file, password);
  }

  public Certificate handleUploadKeyCert(FileUploadEvent event) throws Exception {
    return KeyStoreUtils.handleUploadCert(event, file, type, provider, password);
  }

}