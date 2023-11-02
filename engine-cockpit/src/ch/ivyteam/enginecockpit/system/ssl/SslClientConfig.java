package ch.ivyteam.enginecockpit.system.ssl;

import ch.ivyteam.ivy.configuration.restricted.ConfigKey;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class SslClientConfig {

  private static final ConfigKey CLIENT = ConfigKey.create("SSL").append("Client");

  private IConfiguration config = IConfiguration.instance();

  public TrustStoreConfig getTrustStore() {
    return new TrustStoreConfig();
  }

  public KeyStoreConfig getKeyStore() {
    return new KeyStoreConfig();
  }

  private void setConfig(ConfigKey key, String value) {
    config.set(key, value);
  }

  public class TrustStoreConfig {

    private interface Key {
      ConfigKey TRUST = CLIENT.append("TrustStore");
      ConfigKey FILE = TRUST.append("File");
      ConfigKey PASSWORD = TRUST.append("Password");
      ConfigKey PROVIDER = TRUST.append("Provider");
      ConfigKey TYPE = TRUST.append("Type");
      ConfigKey ALGORITHM = TRUST.append("Algorithm");
      ConfigKey ENABLE_INSECURE_SSL = TRUST.append("EnableInsecureSSL");
    }

    public String getFile() {
      return config.getOrDefault(Key.FILE);
    }

    public void setFile(String File) {
      setConfig(Key.FILE, File);
    }

    public String getPassword() {
      return config.getOrDefault(Key.PASSWORD);
    }

    public void setPassword(String Password) {
      setConfig(Key.PASSWORD, Password);
    }

    public String getProvider() {
      return config.getOrDefault(Key.PROVIDER);
    }

    public void setProvider(String Provider) {
      setConfig(Key.PROVIDER, Provider);
    }

    public String getType() {
      return config.getOrDefault(Key.TYPE);
    }

    public void setType(String Type) {
      setConfig(Key.TYPE, Type);
    }

    public String getAlgorithm() {
      return config.getOrDefault(Key.ALGORITHM);
    }

    public void setAlgorithm(String Algorithm) {
      setConfig(Key.ALGORITHM, Algorithm);
    }

    public String getEnableInsecureSSL() {
      return config.getOrDefault(Key.ENABLE_INSECURE_SSL);
    }

    public void setEnableInsecureSSL(String EnableInsecureSSL) {
      setConfig(Key.ENABLE_INSECURE_SSL, EnableInsecureSSL);
    }
  }

  public class KeyStoreConfig {

    private static interface Key {
      ConfigKey Key = CLIENT.append("KeyStore");
      ConfigKey KEY_FILE = Key.append("File");
      ConfigKey KEY_STORE_PASSWORD = Key.append("Password");
      ConfigKey KEY_PASSWORD = Key.append("Password");
      ConfigKey KEY_ROVIDER = Key.append("Provider");
      ConfigKey KEY_TYPE = Key.append("Type");
      ConfigKey KEY_ALGORITHM = Key.append("Algorithm");
    }

    public String getFile() {
      return config.getOrDefault(Key.KEY_FILE);
    }

    public void setFile(String File) {
      setConfig(Key.KEY_FILE, File);
    }

    public String getPassword() {
      return config.getOrDefault(Key.KEY_STORE_PASSWORD);
    }

    public void setPassword(String Password) {
      setConfig(Key.KEY_STORE_PASSWORD, Password);
    }

    public String getKeyPassword() {
      return config.getOrDefault(Key.KEY_PASSWORD);
    }

    public void setKeyPassword(String keyPassword) {
      setConfig(Key.KEY_PASSWORD, keyPassword);
    }

    public String getProvider() {
      return config.getOrDefault(Key.KEY_ROVIDER);
    }

    public void setProvider(String Provider) {
      setConfig(Key.KEY_ROVIDER, Provider);
    }

    public String getType() {
      return config.getOrDefault(Key.KEY_TYPE);
    }

    public void setType(String Type) {
      setConfig(Key.KEY_TYPE, Type);
    }

    public String getAlgorithm() {
      return config.getOrDefault(Key.KEY_ALGORITHM);
    }

    public void setAlgorithm(String Algorithm) {
      setConfig(Key.KEY_ALGORITHM, Algorithm);
    }

  }

}
