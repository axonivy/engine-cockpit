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

  private IConfiguration config = IConfiguration.instance();

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

  public SslClientBean() {
    this.trustStoreFile = config.get(Key.FILE).orElse("");
    this.trustStorePassword = config.get(Key.PASSWORD).orElse("");
    this.trustStoreProvider = config.get(Key.PROVIDER).orElse("");
    this.trustStoreType = config.get(Key.TYPE).orElse("");
    this.trustStoreAlgorithim = config.get(Key.ALGORITHM).orElse("");
    this.trustManagerClass = config.get(Key.MANAGERCLASS).orElse("");
  }

  public String getTrustStoreFile() {
    return trustStoreFile;
  }

  public void setTrustStoreFile(String trustStoreFile) {
    this.trustStoreFile = trustStoreFile;
  }

  public void save() {
    config.set(Key.FILE, trustStoreFile);
    config.set(Key.PASSWORD, trustStorePassword);
    config.set(Key.PROVIDER, trustStoreProvider);
    config.set(Key.TYPE, trustStoreType);
    config.set(Key.ALGORITHM, trustStoreAlgorithim);
    config.set(Key.MANAGERCLASS, trustManagerClass);

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
