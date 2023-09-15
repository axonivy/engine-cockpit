package ch.ivyteam.enginecockpit.configuration;

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
