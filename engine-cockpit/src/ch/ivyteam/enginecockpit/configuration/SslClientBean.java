package ch.ivyteam.enginecockpit.configuration;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;

import ch.ivyteam.ivy.ssl.restricted.IvyKeystore;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class SslClientBean {

  private static final Logger LOGGER = Logger.getLogger(SslClientBean.class);

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
  private String enableInsecureSSL;
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
    this.enableInsecureSSL = config.getEnableInsecureSSL();
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
    config.setEnableInsecureSSL(enableInsecureSSL);
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
    List<String> providers = new ArrayList<>(Arrays.stream(Security.getProviders())
            .map(provider -> provider.getName())
            .toList());
    providers.add("");
    return providers;
  }

  public String getTrustStoreType() {
    return trustStoreType;
  }

  public void setTrustStoreType(String trustStoreType) {
    this.trustStoreType = trustStoreType;
  }

  public List<String> getTrustStoreTypes() {
    List<String> types = new ArrayList<>();
    types.addAll(getTypes(getTrustStoreProvider()));
    types.add("");
    return types;
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
    List<String> providers = new ArrayList<>(Arrays.stream(Security.getProviders())
            .map(provider -> provider.getName())
            .toList());
    providers.add("");
    return providers;
}

  public String getKeyStoreType() {
    return keyStoreType;
  }

  public void setKeyStoreType(String KeyStoreType) {
    this.keyStoreType = KeyStoreType;
  }

  public List<String> getkeyStoreTypes() {
    List<String> types = new ArrayList<>();
    types.addAll(getTypes(getKeyStoreProvider()));
    types.add("");
    return types;
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
    return enableInsecureSSL;
  }

  public void setEnableInsecureSSL(String EnableInsecureSSL) {
    this.enableInsecureSSL = EnableInsecureSSL;
  }

  public List<StoredCert> getStoredCerts() throws KeyStoreException {
    var tmpKS = loadTrustStore();
    return getStoredCerts(tmpKS);
  }

  public List<StoredCert> getStoredKeyCerts() throws KeyStoreException {
    var tmpKS = loadKeyStore();
    return getStoredCerts(tmpKS);
  }

  @SuppressWarnings("restriction")
  private List<StoredCert> getStoredCerts(Optional<ch.ivyteam.ivy.ssl.restricted.IvyKeystore> tmpKS) throws KeyStoreException {
    if (tmpKS.isEmpty()) {
      return List.of();
    }
    List<String> list = Collections.list(tmpKS.get().getKeyStore().aliases());
    List<StoredCert> certificates = new ArrayList<>();
    for (String alias : list) {
      Certificate cert = tmpKS.get().getKeyStore().getCertificate(alias);
      if (cert instanceof X509Certificate) {
        var x509 = (X509Certificate) cert;
        certificates.add(new StoredCert(alias, x509));
      }
    }
    return certificates;
  }

  @SuppressWarnings("restriction")
  private Optional<ch.ivyteam.ivy.ssl.restricted.IvyKeystore> loadTrustStore() {
    try {
      var tmpKS = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(trustStoreFile, trustStoreType,
              trustStoreProvider, trustStorePassword.toCharArray());
      return Optional.of(tmpKS);
    } catch (Exception ex) {
      LOGGER.error("failed to load keystore " + trustStoreFile, ex);
      return Optional.empty();
    }
  }

  @SuppressWarnings("restriction")
  private Optional<ch.ivyteam.ivy.ssl.restricted.IvyKeystore> loadKeyStore() {
    try {
      var tmpKS = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(keyStoreFile, keyStoreType,
              keyStoreProvider, keyStorePassword.toCharArray());
      return Optional.of(tmpKS);
    } catch (Exception ex) {
      LOGGER.error("failed to load keystore " + keyStoreFile, ex);
      return Optional.empty();
    }
  }

  public record StoredCert(String alias, X509Certificate cert) {

    public String getAlias() {
      return alias;
    }

    public X509Certificate getCert() {
      return cert;
    }

    public boolean isExpired() {
      return cert != null && !cert.getNotAfter().before(new Date());
    }
  }

  public void deleteTrustCertificate(String alias) {
    var tmpKS = loadTrustStore();
    deleteCertificate(alias, tmpKS.get(), trustStoreFile, trustStorePassword);
  }

  public void deleteKeyCertificate(String alias) {
    var tmpKS = loadKeyStore();
    deleteCertificate(alias, tmpKS.get(), keyStoreFile, keyStorePassword);
  }

  @SuppressWarnings("restriction")
  private void deleteCertificate(String alias, IvyKeystore tmpKS, String file, String password) {
    try {
      tmpKS.getKeyStore().deleteEntry(alias);
    } catch (Exception ex) {
      LOGGER.error("failed to delete " + alias, ex);
    }
    try {
      tmpKS.store(file, password.toCharArray());
    } catch (Exception ex) {
      LOGGER.error("failed to load " + alias, ex);
    }
  }

  public Certificate handleUploadKeyCert(FileUploadEvent event)
          throws CertificateException, IOException, Exception {
    return handleUploadCert(event, keyStoreFile, keyStoreType, keyStoreProvider, keyPassword);
  }

  public Certificate handleUploadTrustCert(FileUploadEvent event)
          throws CertificateException, IOException, Exception {
    return handleUploadCert(event, trustStoreFile, trustStoreType, trustStoreProvider, trustStorePassword);
  }

  @SuppressWarnings("restriction")
  public Certificate handleUploadCert(FileUploadEvent event, String file, String type, String provider,
          String password)
          throws CertificateException, IOException, Exception {
    var certFactory = CertificateFactory.getInstance("X509");
    Certificate certFile = certFactory.generateCertificate(event.getFile().getInputStream());
    var store = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(file, type,
            provider, password.toCharArray());
    store.addCert(certFile)
            .store(file, password.toCharArray());
    return certFile;
  }

  private List<String> getTypes(String selectedProvider) {
    Provider[] providers = Security.getProviders();
    return Arrays.stream(providers)
            .filter(provider -> StringUtils.isEmpty(selectedProvider)
                    || provider.getName().equals(selectedProvider))
            .flatMap(provider -> provider.getServices().stream())
            .filter(service -> service.getType().equals(Key.STORE))
            .map(service -> service.getAlgorithm())
            .collect(Collectors.toList());
  }

  private List<String> getAlgorithms(String type) {
    Provider[] providers = Security.getProviders();
    Stream<String> algorithmsStream = Arrays.stream(providers)
            .flatMap(provider -> provider.getServices().stream())
            .filter(service -> service.getType().equals(type))
            .map(service -> service.getAlgorithm());
    return Stream.concat(algorithmsStream, Stream.of("")).collect(Collectors.toList());
  }
}
