package ch.ivyteam.enginecockpit.system.tlsTester;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.ssl.restricted.ISslConfig;
import ch.ivyteam.ivy.ssl.restricted.IvySslContext;
import ch.ivyteam.ivy.ssl.restricted.SslClientSettings;
import ch.ivyteam.util.net.SSLUtil;

public class TLSUtils
{
  public static void checkStoretype(String filename) throws Exception
  {
    if (!isCertificate(filename) && !isKeystore(filename))
    {
      throw new IllegalArgumentException("File must be keystore or certificate (.p12, .jks, .cer)");
    }
  }

  public static KeyStore loadKeyStore(String filename, char[] password, String type, String provider) throws Exception
  {
    String storeType = getStoretype(filename, type);
    try (InputStream in = new FileInputStream(filename))
    {
      if (isCertificate(filename))
      {
        return createKeyStoreFromCert(in, storeType);
      }
      return loadKeyStore(password, storeType, provider, in);
    }
  }

  private static KeyStore createKeyStoreFromCert(InputStream in, String type) throws Exception
  {
    Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(in);
    KeyStore ks = KeyStore.getInstance(type);
    ks.load(null, null);
    ks.setCertificateEntry("temp", cert);
    return ks;
  }

  private static KeyStore loadKeyStore(char[] password, String type, String provider, InputStream in) throws Exception
  {
    KeyStore ks = StringUtils.isBlank(provider) ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider);
    char[] storePass = StringUtils.isBlank(provider) ? null : password;
    ks.load(in, storePass);
    return ks;
  }

  public static SSLSocketFactory getIvySSLSocketFactory(SslClientSettings settings)
  {
    ISslConfig ivySslConfig = new SSLConfig(settings);
    IvySslContext ivyContext = new IvySslContext(ivySslConfig);
    return ivyContext.getSocketFactory();
  }

  public static SSLSocketFactory getSSLSocketFactory(String protocol, KeyStoreInfo customTS, KeyStoreInfo systemTS,
      KeyStoreInfo customKS, KeyStoreInfo systemKS) throws NoSuchAlgorithmException, KeyManagementException,
      KeyStoreException, UnrecoverableKeyException
  {
    MyTrustManager tm = new MyTrustManager();
    tm.addAll(createTrustManagers(systemTS));
    tm.addAll(createTrustManagers(customTS));

    List<KeyManager> kms = new ArrayList<>();
    kms.addAll(createKeyManagers(customKS));
    kms.addAll(createKeyManagers(systemKS));

    SSLContext sc = SSLContext.getInstance(protocol);
    sc.init(kms.toArray(new KeyManager[kms.size()]), new TrustManager[] { tm }, (SecureRandom) null);
    return sc.getSocketFactory();
  }

  private static List<X509TrustManager> createTrustManagers(KeyStoreInfo trustStoreInfo) throws KeyStoreException,
      NoSuchAlgorithmException
  {
    List<X509TrustManager> tms = new ArrayList<>();
    if (trustStoreInfo.getKeyStore() == null || trustStoreInfo.getKeyStore().size() == 0)
    {
      return tms;
    }
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(trustStoreInfo.getKeyStore());
    Arrays.asList(tmf.getTrustManagers()).forEach(tm -> {
      if (tm instanceof X509TrustManager)
      {
        tms.add((X509TrustManager) tm);
      }
    });
    return tms;
  }

  private static List<KeyManager> createKeyManagers(KeyStoreInfo keyStoreInfo) throws NoSuchAlgorithmException,
      UnrecoverableKeyException, KeyStoreException
  {
    if (keyStoreInfo == null || keyStoreInfo.getKeyStore() == null || keyStoreInfo.getKeyStore().size() == 0)
    {
      return Collections.emptyList();
    }
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(keyStoreInfo.getKeyStore(), keyStoreInfo.getPassword());
    return Arrays.asList(kmf.getKeyManagers());
  }

  private static boolean isCertificate(String filename)
  {
    return filename.toLowerCase().matches(".*\\.(cer|crt|der)$");
  }

  private static boolean isKeystore(String filename)
  {
    return filename.toLowerCase().matches(".*\\.(p12|pfx|pkcs12|jks)$");
  }

  private static String getStoretype(String filename, String storeType)
  {
    if (!StringUtils.isBlank(storeType))
    {
      return storeType;
    }
    String type = "JKS";
    String lowerFileName = filename.toLowerCase();
    if (lowerFileName.matches(".*\\.(p12|pfx|pkcs12)$"))
    {
      type = "PKCS12";
    }
    return type;
  }

  private static class MyTrustManager implements X509TrustManager
  {
    private final List<X509TrustManager> trustManagers = new ArrayList<>();

    private void addAll(List<X509TrustManager> trustManagers)
    {
      this.trustManagers.addAll(trustManagers);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
      for (X509TrustManager tm : trustManagers)
      {
        try
        {
          tm.checkClientTrusted(chain, authType);
          return;
        } catch (Exception e)
        {
          // try next trust manager.
        }
      }
      throw new CertificateException("Could not find trusted certificate in chain.");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
      for (X509TrustManager tm : trustManagers)
      {
        try
        {
          tm.checkServerTrusted(chain, authType);
          return;
        } catch (Exception e)
        {
          // try next trust manager.
        }
      }
      throw new CertificateException("Could not find trusted certificate in chain.");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
      List<X509Certificate> certs = new ArrayList<X509Certificate>();
      for (X509TrustManager tm : trustManagers)
      {
        try
        {
          Collections.addAll(certs, tm.getAcceptedIssuers());
        } catch (Exception e)
        {
          // try next trust manager.
        }
      }
      return certs.toArray(new X509Certificate[certs.size()]);
    }
  }

  static class KeyStoreInfo
  {
    static final KeyStoreInfo EMPTY = new KeyStoreInfo(null, null);
    private final KeyStore keyStore;
    private final char[] password;

    KeyStoreInfo(KeyStore keyStore, char[] password) {
      this.keyStore = keyStore;
      this.password = password;
    }

    KeyStore getKeyStore()
    {
      return this.keyStore;
    }

    char[] getPassword()
    {
      return this.password;
    }
  }

  static class SSLConfig implements ISslConfig
  {

    private final SslClientSettings settings;

    SSLConfig(SslClientSettings settings) {
      this.settings = settings;
    }

    @Override
    public String getKeyAlias()
    {
      return "";
    }

    @Override
    public SslClientSettings getSettings()
    {
      return settings;
    }

    @Override
    public String getProtocol()
    {
      return SSLUtil.DEFAULT_PROTOCOL;
    }

    @Override
    public boolean useKey()
    {
      return !StringUtils.isBlank(getKeyAlias());
    }
  }
}
