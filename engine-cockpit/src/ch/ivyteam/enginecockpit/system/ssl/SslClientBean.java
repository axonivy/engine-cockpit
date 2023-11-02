package ch.ivyteam.enginecockpit.system.ssl;

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

import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class SslClientBean {

  private static final Logger LOGGER = Logger.getLogger(SslClientBean.class);

  private interface Key {
    String STORE = "KeyStore";
  }

  private SslClientConfig config = new SslClientConfig();
  private KeyStoreBean keyStoreBean = new KeyStoreBean();
  private TrustStoreBean trustStoreBean = new TrustStoreBean();

  public TrustStoreBean getTrustStore() {
    return trustStoreBean;
  }

  public KeyStoreBean getKeyStore() {
    return keyStoreBean;
  }

  public class TrustStoreBean {
    private String file;
    private String password;
    private String provider;
    private String type;
    private String algorithm;
    private String enableInsecureSSL;

    public TrustStoreBean() {
      var trust = config.getTrustStore();
      this.file = trust.getFile();
      this.password = trust.getPassword();
      this.provider = trust.getProvider();
      this.type = trust.getType();
      this.algorithm = trust.getAlgorithm();
      this.enableInsecureSSL = trust.getEnableInsecureSSL();
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
        types.addAll(SslClientBean.this.getTypes(getProvider()));
        types.add("");
        return types;
    }

    public String getAlgorithm() {
      return algorithm;
    }

    public List<String> getAlgorithms() {
      return SslClientBean.this.getAlgorithms("TrustManagerFactory");
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
      var trust = config.getTrustStore();
      trust.setFile(file);
      trust.setPassword(password);
      trust.setProvider(provider);
      trust.setType(type);
      trust.setAlgorithm(algorithm);
      trust.setEnableInsecureSSL(enableInsecureSSL);
      FacesContext.getCurrentInstance().addMessage("sslTruststoreSaveSuccess",
              new FacesMessage("Trust Store configurations saved"));
    }

    @SuppressWarnings("restriction")
    private Optional<ch.ivyteam.ivy.ssl.restricted.IvyKeystore> load() {
      try {
        var tmpKS = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(file, type,
                provider, password.toCharArray());
        return Optional.of(tmpKS);
      } catch (Exception ex) {
        LOGGER.error("failed to load keystore " + file, ex);
        return Optional.empty();
      }
    }

    public void deleteTrustCertificate(String alias) {
      var tmpKS = load();
      deleteCertificate(alias, tmpKS.get(), file, password);
    }

    public Certificate handleUploadTrustCert(FileUploadEvent event)
            throws CertificateException, IOException, Exception {
      return handleUploadCert(event, file, type, provider, password);
    }

    public List<StoredCert> getStoredCerts() throws KeyStoreException {
      var tmpKS = load();
      return SslClientBean.this.getStoredCerts(tmpKS);
    }
  }

  public class KeyStoreBean {
    private boolean useCustomKeyStore;
    private String file;
    private String password;
    private String keyPassword;
    private String provider;
    private String type;
    private String algorithm;

    public KeyStoreBean() {
      var key = config.getKeyStore();
      this.file = key.getFile();
      this.password = key.getPassword();
      this.keyPassword = key.getKeyPassword();
      this.provider = key.getProvider();
      this.type = key.getType();
      this.algorithm = key.getAlgorithm();

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
      types.addAll(SslClientBean.this.getTypes(getProvider()));
      types.add("");
      return types;
    }

    public String getAlgorithm() {
      return algorithm;
    }

    public List<String> getAlgorithms() {
      return SslClientBean.this.getAlgorithms("KeyManagerFactory");
    }

    public List<StoredCert> getStoredKeyCerts() throws Exception {
      var tmpKS = loadKeyStore();
      return getStoredCerts(tmpKS);
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
      var key = config.getKeyStore();
      key.setFile(file);
      key.setPassword(password);
      key.setKeyPassword(keyPassword);
      key.setProvider(provider);
      key.setType(type);
      key.setAlgorithm(algorithm);
      FacesContext.getCurrentInstance().addMessage("sslKeystoreSaveSuccess",
              new FacesMessage("Key Store configurations saved"));
    }

    @SuppressWarnings("restriction")
    private Optional<ch.ivyteam.ivy.ssl.restricted.IvyKeystore> loadKeyStore() {
      try {
        var tmpKS = ch.ivyteam.ivy.ssl.restricted.IvyKeystore.load(file, type,
                provider, password.toCharArray());
        return Optional.of(tmpKS);
      } catch (Exception ex) {
        LOGGER.error("failed to load keystore " + file, ex);
        return Optional.empty();
      }
    }

    public void deleteKeyCertificate(String alias) {
      var tmpKS = loadKeyStore();
      deleteCertificate(alias, tmpKS.get(), file, password);
    }

    public Certificate handleUploadKeyCert(FileUploadEvent event)
            throws CertificateException, IOException, Exception {
      return handleUploadCert(event, file, type, provider, keyPassword);
    }

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
  private void deleteCertificate(String alias, ch.ivyteam.ivy.ssl.restricted.IvyKeystore tmpKS, String file, String password) {
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
