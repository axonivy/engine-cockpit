package ch.ivyteam.enginecockpit.configuration;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import ch.ivyteam.ivy.configuration.restricted.ConfigKey;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@ManagedBean
@ViewScoped
public class SslClientBean {

  private String trustStoreFile;
  private String trustStorePassword;
  private String trustStoreProvider;
  private String trustStoreType;
  private String trustStoreAlgorithim;
  private String trustManagerClass;
  private SslClientConfig config = new SslClientConfig();


  public SslClientBean() {
    this.trustStoreFile = config.getTrustStoreFile();
    this.trustStorePassword = config.getTrustStorePassword();
    this.trustStoreProvider = config.getTrustStoreProvider();
    this.trustStoreType = config.getTrustStoreType();
    this.trustStoreAlgorithim = config.getTrustStoreAlgorithim();
    this.trustManagerClass = config.getTrustManagerClass();
  }

  @SuppressWarnings("restriction")
  public static class SslClientConfig {

    private interface Key {

      ConfigKey CLIENT = ConfigKey.create("SSL").append("Client");
      ConfigKey TRUST = CLIENT.append("TrustStore");
      ConfigKey FILE = TRUST.append("File");
      ConfigKey PASSWORD = TRUST.append("Password");
      ConfigKey PROVIDER = TRUST.append("Provider");
      ConfigKey TYPE = TRUST.append("Type");
      ConfigKey ALGORITHM = TRUST.append("Algorithm");
      ConfigKey MANAGERCLASS = TRUST.append("ManagerClass");
    }

    private IConfiguration config = IConfiguration.instance();

    public String getTrustStoreFile() {
      return config.get(Key.FILE).orElse("");
    }

    public void setTrustStoreFile(String trustStoreFile) {
      config.set(Key.FILE, trustStoreFile);
    }

    public String getTrustStorePassword() {
      return config.get(Key.PASSWORD).orElse("");
    }

    public void setTrustStorePassword(String trustStorePassword) {
      config.set(Key.PASSWORD, trustStorePassword);
    }

    public String getTrustStoreProvider() {
      return config.get(Key.PROVIDER).orElse("");
    }

    public void setTrustStoreProvider(String trustStoreProvider) {
      config.set(Key.PROVIDER, trustStoreProvider);
    }

    public String getTrustStoreType() {
      return config.get(Key.TYPE).orElse("");
    }

    public void setTrustStoreType(String trustStoreType) {
      config.set(Key.TYPE, trustStoreType);
    }

    public String getTrustStoreAlgorithim() {
      return config.get(Key.ALGORITHM).orElse("");
    }

    public void setTrustStoreAlgorithim(String trustStoreAlgorithim) {
      config.set(Key.ALGORITHM, trustStoreAlgorithim);
    }

    public String getTrustManagerClass() {
      return config.get(Key.MANAGERCLASS).orElse("");
    }

    public void setTrustManagerClass(String trustManagerClass) {
      config.set(Key.MANAGERCLASS, trustManagerClass);
    }
  }

  public String getTrustStoreFile() {
    return trustStoreFile;
  }

  public void setTrustStoreFile(String trustStoreFile) {
    this.trustStoreFile = trustStoreFile;
  }

  public void save() {
    config.setTrustStoreFile(trustStoreFile);
    config.setTrustStorePassword(trustStorePassword);
    config.setTrustStoreProvider(trustStoreProvider);
    config.setTrustStoreType(trustStoreType);
    config.setTrustStoreAlgorithim(trustStoreAlgorithim);
    config.setTrustManagerClass(trustManagerClass);

    FacesContext.getCurrentInstance().addMessage("sslTruststoreSaveSuccess",
            new FacesMessage("Trust Store configurations saved"));
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

  public String getTrustStoreAlgorithim() {
    return trustStoreAlgorithim;
  }

  public void setTrustStoreAlgorithim(String trustStoreAlgorithim) {
    this.trustStoreAlgorithim = trustStoreAlgorithim;
  }

  public String getTrustManagerClass() {
    return trustManagerClass;
  }

  public void setTrustManagerClass(String trustManagerClass) {
    this.trustManagerClass = trustManagerClass;
  }
}
