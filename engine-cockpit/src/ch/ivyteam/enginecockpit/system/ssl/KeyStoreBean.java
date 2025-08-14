package ch.ivyteam.enginecockpit.system.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
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
import ch.ivyteam.ivy.ssl.restricted.SslClientSettings.KeyStoreConfig;

@ManagedBean
@ViewScoped
public class KeyStoreBean implements SslTableStore {

  private final KeyStoreConfig store;

  private boolean useCustomKeyStore;
  private String file;
  private char[] password;
  private char[] keyPassword;
  private String provider;
  private String type;
  private String algorithm;

  private String certificateLoadError;

  public KeyStoreBean() {
    this.store = SslClientSettings.instance().getKeyStore();
    this.file = store.getFile();
    this.password = store.getPassword();
    this.keyPassword = store.getKeyPassword();
    this.provider = store.getProvider();
    this.type = store.getType();
    this.algorithm = store.getAlgorithm();
    this.useCustomKeyStore = store.useCustomKeyStore();
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
    return String.valueOf(password);
  }

  public String getKeyPassword() {
    return String.valueOf(keyPassword);
  }

  public String getProvider() {
    return provider;
  }

  @SuppressWarnings("hiding")
  public List<String> getProviders() {
    List<String> providers = new ArrayList<>(Arrays.stream(Security.getProviders()).map(Provider::getName).toList());
    providers.add("");
    return providers;
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
    return SecurityProviders.getAlgorithms("KeyManagerFactory");
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

  public void setKeyPassword(String keyPassword) {
    if (keyPassword.isBlank()) {
      return;
    }
    this.keyPassword = keyPassword.toCharArray();
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
    store.setPassword(String.valueOf(password));
    store.setKeyPassword(String.valueOf(keyPassword));
    store.setProvider(provider);
    store.setType(type);
    store.setAlgorithm(algorithm);
    store.setUseCustomKeyStore(String.valueOf(useCustomKeyStore));
    getCertificats();
    Message.info()
        .clientId("sslKeystoreSaveSuccess")
        .summary(Ivy.cm().co("/sslKeyStore/KeyStoreConfigurationsSavedMessage"))
        .show();
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

  @Override
  public void deleteCertificate(String alias) throws KeyStoreException {
    getKeyStoreUtils().deleteCertificate(alias);
    Message.info()
        .clientId("sslDeleteCertificate")
        .summary(Ivy.cms().co("/sslKeyStore/DeletedCertificateMessage", Arrays.asList(alias)))
        .show();
  }

  @Override
  public Certificate handleUploadCertificate(FileUploadEvent event)
      throws CertificateException, KeyStoreException, IOException {
    try (InputStream is = event.getFile().getInputStream()) {
      return getKeyStoreUtils().handleUploadCert(is);
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
