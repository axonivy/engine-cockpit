package ch.ivyteam.enginecockpit.configuration;

import java.io.IOException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@ViewScoped
public class SslClientBean {

  private interface Key {

    String STORE = "KeyStore";
  }


  private boolean useCustomKeyStore;
  private String trustStoreFile;
  private String trustStorePassword;
  private String trustStoreProvider;
  private String trustStoreType;
  private String trustStoreAlgorithm;
  private String trustManagerClass;
  private String keyStoreFile;
  private String keyStorePassword;
  private String keyPassword;
  private String keyStoreProvider;
  private String keyStoreType;
  private String keyStoreAlgorithm;
  private String EnableInsecureSSL;
  private SslClientConfig config = new SslClientConfig();

  public SslClientBean() {
    this.trustStoreFile = config.getTrustStoreFile();
    this.trustStorePassword = config.getTrustStorePassword();
    this.trustStoreProvider = config.getTrustStoreProvider();
    this.trustStoreType = config.getTrustStoreType();
    this.trustStoreAlgorithm = config.getTrustStoreAlgorithm();
    this.trustManagerClass = config.getTrustManagerClass();
    this.keyStoreFile = config.getKeyStoreFile();
    this.keyStorePassword = config.getKeyStorePassword();
    this.keyPassword = config.getKeyPassword();
    this.keyStoreProvider = config.getKeyStoreProvider();
    this.keyStoreType = config.getKeyStoreType();
    this.keyStoreAlgorithm = config.getKeyStoreAlgorithm();
    this.EnableInsecureSSL = config.getEnableInsecureSSL();
  }

  public String getTrustStoreFile() {
    return trustStoreFile;
  }

  public void setTrustStoreFile(String trustStoreFile) {
    this.trustStoreFile = trustStoreFile;
  }

  public void saveTrustStore() {
    config.setTrustStoreFile(trustStoreFile);
    config.setTrustStorePassword(trustStorePassword);
    config.setTrustStoreProvider(trustStoreProvider);
    config.setTrustStoreType(trustStoreType);
    config.setTrustStoreAlgorithm(trustStoreAlgorithm);
    config.setTrustManagerClass(trustManagerClass);
    config.setEnableInsecureSSL(EnableInsecureSSL);
    FacesContext.getCurrentInstance().addMessage("sslTruststoreSaveSuccess",
            new FacesMessage("Trust Store configurations saved"));
  }

  public void saveKeyStore() {
    config.setKeyStoreFile(keyStoreFile);
    config.setKeyStorePassword(keyStorePassword);
    config.setKeyPassword(keyPassword);
    config.setKeyStoreProvider(keyStoreProvider);
    config.setKeyStoreType(keyStoreType);
    config.setKeyStoreAlgorithm(keyStoreAlgorithm);
    FacesContext.getCurrentInstance().addMessage("sslKeystoreSaveSuccess",
            new FacesMessage("Key Store configurations saved"));
  }

  public boolean isUseCustomKeyStore() {
    return useCustomKeyStore;
  }

  public void setUseCustomKeyStore(boolean useCustomKeyStore) {
    this.useCustomKeyStore = useCustomKeyStore;
  }

  public String getTrustStorePassword() {
    return trustStorePassword;
  }

  public void setTrustStorePassword(String trustStorePassword) {
    this.trustStorePassword = trustStorePassword;
  }

  public String getTrustStoreProvider() {
    return trustStoreProvider;
  }

  public void setTrustStoreProvider(String trustStoreProvider) {
    this.trustStoreProvider = trustStoreProvider;
  }

  public List<String> getTrustStoreProviders() {
    return Arrays.stream(Security.getProviders())
            .map(provider -> provider.getName())
            .toList();
  }

  public String getTrustStoreType() {
    return trustStoreType;
  }

  public void setTrustStoreType(String trustStoreType) {
    this.trustStoreType = trustStoreType;
  }

  public List<String> getTrustStoreTypes() {
    return getTypes(getTrustStoreProvider());
  }

  public String getTrustStoreAlgorithm() {
    return trustStoreAlgorithm;
  }

  public void setTrustStoreAlgorithm(String trustStoreAlgorithm) {
    this.trustStoreAlgorithm = trustStoreAlgorithm;
  }

  public List<String> getTrustStoreAlgorithms() {
    return getAlgorithms("TrustManagerFactory");
  }

  public String getTrustManagerClass() {
    return trustManagerClass;
  }

  public void setTrustManagerClass(String trustManagerClass) {
    this.trustManagerClass = trustManagerClass;
  }

  public String getKeyStoreFile() {
    return keyStoreFile;
  }

  public void setKeyStoreFile(String KeyStoreFile) {
    this.keyStoreFile = KeyStoreFile;
  }

  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  public void setKeyStorePassword(String KeyStorePassword) {
    this.keyStorePassword = KeyStorePassword;
  }

  public String getKeyPassword() {
    return keyPassword;
  }

  public void setKeyPassword(String KeyPassword) {
    this.keyPassword = KeyPassword;
  }

  public String getKeyStoreProvider() {
    return keyStoreProvider;
  }

  public void setKeyStoreProvider(String KeyStoreProvider) {
    this.keyStoreProvider = KeyStoreProvider;
  }

  public List<String> getkeyStoreProviders() {
    return Arrays.stream(Security.getProviders())
            .map(provider -> provider.getName())
            .toList();
  }

  public String getKeyStoreType() {
    return keyStoreType;
  }

  public void setKeyStoreType(String KeyStoreType) {
    this.keyStoreType = KeyStoreType;
  }

  public List<String> getkeyStoreTypes() {
    return getTypes(getKeyStoreProvider());
  }

  public String getKeyStoreAlgorithm() {
    return keyStoreAlgorithm;
  }

  public void setKeyStoreAlgorithm(String KeyStoreAlgorithm) {
    this.keyStoreAlgorithm = KeyStoreAlgorithm;
  }

  public List<String> getKeyStoreAlgorithms() {
    return getAlgorithms("KeyManagerFactory");
  }

  public String getEnableInsecureSSL() {
    return EnableInsecureSSL;
  }

  public void setEnableInsecureSSL(String EnableInsecureSSL) {
    this.EnableInsecureSSL = EnableInsecureSSL;
  }

  @SuppressWarnings("restriction")
  public Certificate handleUploadCert(FileUploadEvent event) throws CertificateException, IOException, Exception {
    var certFactory = CertificateFactory.getInstance("X509");
    Certificate certFile = certFactory.generateCertificate(event.getFile().getInputStream());
    var store = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(trustStoreFile,trustStoreType, trustStoreProvider,trustStorePassword.toCharArray());
    store.addCert(certFile)
         .store(trustStoreFile, trustStorePassword.toCharArray());
    return certFile;
  }

  private List<String> getTypes(String selectedProvider) {
    if (StringUtils.isEmpty(selectedProvider)) {
      return getAlgorithms(Key.STORE);
    } else {
      var provider = Security.getProvider(selectedProvider);
      if (provider != null) {
        return provider.getServices().stream()
                .filter(service -> service.getType().equals(Key.STORE))
                .map(service -> service.getAlgorithm())
                .toList();
      }
    }
    return getAlgorithms(Key.STORE);
  }

  private List<String> getAlgorithms(String type) {
    return Arrays.stream(Security.getProviders())
            .flatMap(securityProvider -> securityProvider.getServices().stream())
            .filter(service -> service.getType().equals(type))
            .map(service -> service.getAlgorithm())
            .collect(Collectors.toList());
  }
}
