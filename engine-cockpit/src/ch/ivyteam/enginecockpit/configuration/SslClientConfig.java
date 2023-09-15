package ch.ivyteam.enginecockpit.configuration;

import ch.ivyteam.ivy.configuration.restricted.ConfigKey;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class SslClientConfig {

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
