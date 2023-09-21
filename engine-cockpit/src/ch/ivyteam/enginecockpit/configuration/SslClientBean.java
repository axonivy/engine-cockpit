package ch.ivyteam.enginecockpit.configuration;

import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class SslClientBean {

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
  private String keyStoreAlgorithim;
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
    this.keyStoreAlgorithim = config.getKeyStoreAlgorithim();
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
    config.setTrustStoreAlgorithim(trustStoreAlgorithm);
    config.setTrustManagerClass(trustManagerClass);
    FacesContext.getCurrentInstance().addMessage("sslTruststoreSaveSuccess",
            new FacesMessage("Trust Store configurations saved"));
  }

  public void saveKeyStore() {
    config.setKeyStoreFile(keyStoreFile);
    config.setKeyStorePassword(keyStorePassword);
    config.setKeyPassword(keyPassword);
    config.setKeyStoreProvider(keyStoreProvider);
    config.setKeyStoreType(keyStoreType);
    config.setKeyStoreAlgorithim(keyStoreAlgorithim);
    FacesContext.getCurrentInstance().addMessage("sslKeystoreSaveSuccess",
            new FacesMessage("Key Store configurations saved"));
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

  public String getTrustStoreType() {
    return trustStoreType;
  }

  public void setTrustStoreType(String trustStoreType) {
    this.trustStoreType = trustStoreType;
  }

  public String getTrustStoreAlgorithm() {
    return trustStoreAlgorithm;
  }

  public void setTrustStoreAlgorithm(String trustStoreAlgorithm) {
    this.trustStoreAlgorithm = trustStoreAlgorithm;
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
    return Arrays.stream(Security.getProviders())
            .flatMap(provider -> provider.getServices().stream())
            .filter(service -> "KeyStore".equals(service.getType()))
            .map(service -> service.getAlgorithm())
            .collect(Collectors.toList());
  }

  public String getKeyStoreAlgorithm() {
    return keyStoreAlgorithim;
  }

  public void setKeyStoreAlgorithim(String KeyStoreAlgorithim) {
    this.keyStoreAlgorithim = KeyStoreAlgorithim;
  }

  public List<String> getkeyStoreAlgorithms() {
    List<String> algorithms = new ArrayList<>();
    for (var securityProvider : Security.getProviders()) {
      for (var service : securityProvider.getServices()) {
        if (service.getType().equals("KeyManagerFactory")) {
          algorithms.add(service.getAlgorithm());
        }
      }
    }
    return algorithms;
  }
}
