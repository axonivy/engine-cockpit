package ch.ivyteam.enginecockpit.configuration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ch.ivyteam.di.restricted.DiCore;

@ManagedBean
@ViewScoped
public class SslClientBean {

  private String trustStoreFile;

  public SslClientBean() {
    var settings = DiCore.getGlobalInjector()
            .getInstance(ch.ivyteam.ivy.ssl.client.restricted.SslClientSettings.class);
    this.trustStoreFile = settings.getTrustStoreFile();
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
}
