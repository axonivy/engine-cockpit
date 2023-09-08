package ch.ivyteam.enginecockpit.configuration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.net.ssl.TrustManager;
import ch.ivyteam.di.restricted.DiCore;

@ManagedBean
@ViewScoped
public class SslClientBean {

  private String trustStoreFile;
  private char[] trustStorePassword;
  private String trustStoreProvider;
  private String trustStoreType;
  private String trustStoreAlgorithim;
  private Class<? extends TrustManager> trustManagerClass;

  public SslClientBean() {
    var settings = DiCore.getGlobalInjector()
            .getInstance(ch.ivyteam.ivy.ssl.client.restricted.SslClientSettings.class);
    this.trustStoreFile = settings.getTrustStoreFile();
    this.trustStorePassword = settings.getTrustStorePassword();
    this.trustStoreProvider = settings.getTrustStoreProvider();
    this.trustStoreType = settings.getTrustStoreType();
    this.trustStoreAlgorithim = settings.getTrustStoreAlgorithm();
    this.setTrustManagerClass(settings.getTrustManagerClass());
  }

  public String getTrustStoreFile() {
    return trustStoreFile;
  }

  public void setTrustStoreFile(String trustStoreFile) {
    this.trustStoreFile = trustStoreFile;
  }

  public void save() {
    // save to ivy.yaml
  }

  public char[] getTrustStorePassword() {
    return trustStorePassword;
  }

  public void setTrustStorePassword(char[] trustStorePassword) {
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

  public Class<? extends TrustManager> getTrustManagerClass() {
    return trustManagerClass;
  }

  public void setTrustManagerClass(Class<? extends TrustManager> trustManagerClass) {
    this.trustManagerClass = trustManagerClass;
  }
}
