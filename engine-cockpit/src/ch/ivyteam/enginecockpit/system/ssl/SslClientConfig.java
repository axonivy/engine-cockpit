package ch.ivyteam.enginecockpit.system.ssl;

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
    ConfigKey ENABLE_INSECURE_SSL = TRUST.append("EnableInsecureSSL");

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
    return config.getOrDefault(Key.FILE);
  }

  public void setTrustStoreFile(String trustStoreFile) {
    setConfig(Key.FILE, trustStoreFile);
  }

  public String getTrustStorePassword() {
    return config.getOrDefault(Key.PASSWORD);
  }

  public void setTrustStorePassword(String trustStorePassword) {
    setConfig(Key.PASSWORD, trustStorePassword);
  }

  private void setConfig(ConfigKey key, String value) {
    config.set(key, value);
  }

  public String getTrustStoreProvider() {
    return config.getOrDefault(Key.PROVIDER);
  }

  public void setTrustStoreProvider(String trustStoreProvider) {
    setConfig(Key.PROVIDER, trustStoreProvider);
  }

  public String getTrustStoreType() {
    return config.getOrDefault(Key.TYPE);
  }

  public void setTrustStoreType(String trustStoreType) {
    setConfig(Key.TYPE, trustStoreType);
  }

  public String getTrustStoreAlgorithm() {
    return config.getOrDefault(Key.ALGORITHM);
  }

  public void setTrustStoreAlgorithm(String trustStoreAlgorithm) {
    setConfig(Key.ALGORITHM, trustStoreAlgorithm);
  }

  public String getKeyStoreFile() {
    return config.getOrDefault(Key.KEY_FILE);
  }

  public void setKeyStoreFile(String keyStoreFile) {
    setConfig(Key.KEY_FILE, keyStoreFile);
  }

  public String getKeyStorePassword() {
    return config.getOrDefault(Key.KEY_STORE_PASSWORD);
  }

  public void setKeyStorePassword(String keyStorePassword) {
    setConfig(Key.KEY_STORE_PASSWORD, keyStorePassword);
  }

  public String getKeyPassword() {
    return config.getOrDefault(Key.KEY_PASSWORD);
  }

  public void setKeyPassword(String keyPassword) {
    setConfig(Key.KEY_PASSWORD, keyPassword);
  }

  public String getKeyStoreProvider() {
    return config.getOrDefault(Key.KEY_ROVIDER);
  }

  public void setKeyStoreProvider(String keyStoreProvider) {
    setConfig(Key.KEY_ROVIDER, keyStoreProvider);
  }

  public String getKeyStoreType() {
    return config.getOrDefault(Key.KEY_TYPE);
  }

  public void setKeyStoreType(String keyStoreType) {
    setConfig(Key.KEY_TYPE, keyStoreType);
  }

  public String getKeyStoreAlgorithm() {
    return config.getOrDefault(Key.KEY_ALGORITHM);
  }

  public void setKeyStoreAlgorithm(String keyStoreAlgorithm) {
    setConfig(Key.KEY_ALGORITHM, keyStoreAlgorithm);
  }

  public String getEnableInsecureSSL() {
    return config.getOrDefault(Key.ENABLE_INSECURE_SSL);
  }

  public void setEnableInsecureSSL(String EnableInsecureSSL) {
    setConfig(Key.ENABLE_INSECURE_SSL, EnableInsecureSSL);
  }
}
