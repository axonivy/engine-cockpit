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

    ConfigKey Key = CLIENT.append("KeyStore");
    ConfigKey KEY_FILE = Key.append("File");
    ConfigKey KEY_STORE_PASSWORD = Key.append("Password");
    ConfigKey KEY_PASSWORD = Key.append("Password");
    ConfigKey KEY_ROVIDER = Key.append("Provider");
    ConfigKey KEY_TYPE = Key.append("Type");
    ConfigKey KEY_ALGORITHM = Key.append("Algorithm");
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

  public String getTrustPassword() {
    return config.get(Key.PASSWORD).orElse("");
  }

  public void setTrustPassword(String trustStorePassword) {
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

  public String getTrustStoreAlgorithm() {
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

  public String getKeyStoreFile() {
    return config.get(Key.KEY_FILE).orElse("");
  }

  public void setKeyStoreFile(String keyStoreFile) {
    config.set(Key.KEY_FILE, keyStoreFile);
  }

  public String getKeyStorePassword() {
    return config.get(Key.KEY_STORE_PASSWORD).orElse("");
  }

  public void setKeyStorePassword(String keyStorePassword) {
    config.set(Key.KEY_STORE_PASSWORD, keyStorePassword);
  }

  public String getKeyPassword() {
    return config.get(Key.KEY_PASSWORD).orElse("");
  }

  public void setKeyPassword(String keyPassword) {
    config.set(Key.KEY_PASSWORD, keyPassword);
  }

  public String getKeyStoreProvider() {
    return config.getOrDefault(Key.KEY_ROVIDER);
  }

  public void setKeyStoreProvider(String keyStoreProvider) {
    config.set(Key.KEY_ROVIDER, keyStoreProvider);
  }

  public String getKeyStoreType() {
    return config.getOrDefault(Key.KEY_TYPE);
  }

  public void setKeyStoreType(String keyStoreType) {
    config.set(Key.KEY_TYPE, keyStoreType);
  }

  public String getKeyStoreAlgorithim() {
    return config.getOrDefault(Key.KEY_ALGORITHM);
  }

  public void setKeyStoreAlgorithim(String keyStoreAlgorithim) {
    config.set(Key.KEY_ALGORITHM, keyStoreAlgorithim);
  }
}
