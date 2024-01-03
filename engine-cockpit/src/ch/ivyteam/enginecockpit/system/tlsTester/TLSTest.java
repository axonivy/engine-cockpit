package ch.ivyteam.enginecockpit.system.tlsTester;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.system.tlsTester.TLSUtils.KeyStoreInfo;
import ch.ivyteam.ivy.ssl.restricted.SslClientSettings;

public final class TLSTest
{
  private static final String STORE_SETTING_TXT = "%1$s client %2$s set to %3$s.";
  private static final String PROP_KEYSTORE = "javax.net.ssl.keyStore";
  private static final String PROP_KEYSTORE_PWD = "javax.net.ssl.keyStorePassword";
  private static final String PROP_KEYSTORE_TYPE = "javax.net.ssl.keyStoreType";
  private static final String PROP_KEYSTORE_PROV = "javax.net.ssl.keyStoreProvider";

  private static final String PROP_TRUSTSTORE = "javax.net.ssl.trustStore";
  private static final String PROP_TRUSTSTORE_PWD = "javax.net.ssl.trustStorePassword";
  private static final String PROP_TRUSTSTORE_TYPE = "javax.net.ssl.trustStoreType";
  private static final String PROP_TRUSTSTORE_PROV = "javax.net.ssl.trustStoreProvider";

  private static final String PROP_HTTPS_PROTOCOLS = "https.protocols";
  private static final String PROP_TLS_PROTOCOLS = "jdk.tls.client.protocols";

  private static final String DEFAULT_STORE_TYPE = KeyStore.getDefaultType();

  private final List<TLSTestData> logs;
  private final String targetUri;
  private final SslClientSettings sslClientSettings;

  private KeyStoreInfo systemKeyStore = null;
  private KeyStoreInfo systemTrustStore = null;
  private KeyStoreInfo customKeyStore = KeyStoreInfo.EMPTY;
  private KeyStoreInfo customTrustStore = KeyStoreInfo.EMPTY;

  public TLSTest(List<TLSTestData> logs, String targetUri) {
    this.logs = logs;
    this.targetUri = targetUri;
    this.sslClientSettings = SslClientSettings.instance();
  }

  public void runTLSTests()
  {
    checkJavaSystemProperties();
    checkIvySystemProperties();
    loadSystemKeystore();
    loadClientTruststore();
    loadCustomClientKeystore();
    loadCustomClientTruststore();
    testTLSConnectWithSystemTruststore();
    testTLSConnectNoClientKeystore();
    testTLSConnectWithClientKeystore();
    testTLSConnectWithIvySslContext();
    testTLSConnectWithProtocol(TLSTestGroup.CONN_TLS_SSLV3, "SSLv3");
    testTLSConnectWithProtocol(TLSTestGroup.CONN_TLS_TLSV10, "TLSv1");
    testTLSConnectWithProtocol(TLSTestGroup.CONN_TLS_TLSV11, "TLSv1.1");
    testTLSConnectWithProtocol(TLSTestGroup.CONN_TLS_TLSV12, "TLSv1.2");
    testTLSConnectWithProtocol(TLSTestGroup.CONN_TLS_TLSV13, "TLSv1.3");
  }

  public void runTLSTestsWithSpecificTruststore(File trustFile, char[] password)
  {
    runTLSTests();
    testTLSConnectWithUploadedTruststore(trustFile, password);
  }

  private void checkJavaSystemProperties()
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.JAVA_SYSTEM_PROPERTIES);

    data.addEntry(getPropInfo(PROP_KEYSTORE, "", false));
    data.addEntry(getPropInfo(PROP_KEYSTORE_PWD, "", true));
    data.addEntry(getPropInfo(PROP_KEYSTORE_TYPE, DEFAULT_STORE_TYPE, false));
    data.addEntry(getPropInfo(PROP_KEYSTORE_PROV, "", false));
    data.addEntry(getPropInfo(PROP_TRUSTSTORE, "", false));
    data.addEntry(getPropInfo(PROP_TRUSTSTORE_PWD, "", true));
    data.addEntry(getPropInfo(PROP_TRUSTSTORE_TYPE, DEFAULT_STORE_TYPE, false));
    data.addEntry(getPropInfo(PROP_TRUSTSTORE_PROV, "", false));
    data.addEntry(getPropInfo(PROP_HTTPS_PROTOCOLS, "", false));
    data.addEntry(getPropInfo(PROP_TLS_PROTOCOLS, "", false));
    data.setResult(2);

    logs.add(data);
  }

  private void checkIvySystemProperties()
  {
    var keystore = sslClientSettings.getKeyStore();
    TLSTestData data = new TLSTestData(TLSTestGroup.IVY_SYSTEM_PROPERTIES);
    String notBeUsed = "NOT be used - skipping";
    String toBeUsed = "be used";

    String usage = keystore.useSystemKeyStore() ? toBeUsed : notBeUsed;
    data.addEntry(STORE_SETTING_TXT, "System", "KeyStore", usage);
    usage = keystore.useCustomKeyStore() ? toBeUsed : notBeUsed;
    data.addEntry(STORE_SETTING_TXT, "Custom", "KeyStore", usage);
    usage = keystore.useSystemKeyStore() ? toBeUsed : notBeUsed;
    data.addEntry(STORE_SETTING_TXT, "System", "TrustStore", usage);
    usage = toBeUsed;
    data.addEntry(STORE_SETTING_TXT, "Custom", "TrustStore", usage);
    data.setResult(2);

    logs.add(data);
  }

  private void loadSystemKeystore()
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.CLIENT_SYSTEM_KEYSTORE);
    String storeFilename = System.getProperty(PROP_KEYSTORE);
    if (StringUtils.isBlank(storeFilename))
    {
      data.addEntry("No client KeyStore file defined - skipping");
      data.setResult(2);
    } else
    {
      systemKeyStore = loadKeyStore(storeFilename, PROP_KEYSTORE_PWD, PROP_KEYSTORE_TYPE, PROP_KEYSTORE_PROV,
          "KeyStore", data);
    }
    logs.add(data);
  }

  private void loadClientTruststore()
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.CLIENT_SYSTEM_TRUSTSTORE);
    String storeFilename = System.getProperty(PROP_TRUSTSTORE);
    if (StringUtils.isBlank(storeFilename))
    {
      data.addEntry("No client TrustStore file defined - using default cacert");
      storeFilename = System.getProperty("java.home") + "/lib/security/cacerts";
    }
    systemTrustStore = loadKeyStore(storeFilename, PROP_TRUSTSTORE_PWD, PROP_TRUSTSTORE_TYPE, PROP_TRUSTSTORE_PROV,
        "TrustStore", data);

    logs.add(data);
  }

  private void loadCustomClientKeystore()
  {
    var keystore = sslClientSettings.getKeyStore();
    TLSTestData data = new TLSTestData(TLSTestGroup.CLIENT_CUSTOM_KEYSTORE);
    if (!keystore.useCustomKeyStore())
    {
      data.addEntry("Custom client KeyStore set to NOT be used - skipping");
      data.setResult(2);
    } else
    {
      customKeyStore = loadKeyStore(keystore.getFile(), keystore.getKeyPassword()
          , keystore.getType(), keystore.getProvider(),
          "CustomKeyStore", data);
    }
    logs.add(data);
  }

  private void loadCustomClientTruststore() {
    var trustStore = sslClientSettings.getTrustStore();
    TLSTestData data = new TLSTestData(TLSTestGroup.CLIENT_CUSTOM_TRUSTSTORE);
    customTrustStore = loadKeyStore(trustStore.getFile(), trustStore.getPassword(), trustStore.getType(),
            trustStore.getProvider(), "CustomTrustStore", data);
    logs.add(data);
  }

  private void testTLSConnectWithSystemTruststore()
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.CONN_WITH_SYSTEM_TRUSTSTORE);
    connectToTarget(KeyStoreInfo.EMPTY, systemTrustStore, null, null, data, "TLS");
    logs.add(data);
  }

  private void testTLSConnectNoClientKeystore()
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.CONN_NO_KEYSTORE);
    connectToTarget(customTrustStore, systemTrustStore, null, null, data, "TLS");
    logs.add(data);
  }

  private void testTLSConnectWithClientKeystore()
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.CONN_WITH_KEYSTORE);
    connectToTarget(customTrustStore, systemTrustStore, customKeyStore, systemKeyStore, data, "TLS");
    logs.add(data);
  }

  private void testTLSConnectWithUploadedTruststore(File truststore, char[] password)
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.CONN_UPLOADED_TRUSTSTORE);
    KeyStoreInfo customTrustStore = loadKeyStore(truststore.getAbsolutePath(), password, null, null,
        "UserTrustStore", data);
    connectToTarget(customTrustStore, KeyStoreInfo.EMPTY, null, null, data, "TLS");
    logs.add(data);
  }

  private void testTLSConnectWithIvySslContext()
  {
    TLSTestData data = new TLSTestData(TLSTestGroup.CONN_IVY_SSL_CONTEXT);
    connectToTargetWithIvySsl(data);
    logs.add(data);
  }

  private void testTLSConnectWithProtocol(TLSTestGroup testGroup, String protocol)
  {
    TLSTestData data = new TLSTestData(testGroup);
    connectToTarget(customTrustStore, systemTrustStore, customKeyStore, systemKeyStore, data, protocol);
    logs.add(data);
  }

  private void connectToTargetWithIvySsl(TLSTestData data)
  {
    try
    {
      SSLSocketFactory sslSocketFactory = TLSUtils.getIvySSLSocketFactory(sslClientSettings);
      connectWithTLS(data, sslSocketFactory);
    } catch (Exception ex)
    {
      handleConnectException(data, ex);
    }
  }

  private void handleConnectException(TLSTestData data, Exception ex)
  {
    data.addEntry("Connection to %1$s FAILED! Error message is: %2$s", targetUri, ex.getMessage());
    data.setResult(0);
//    Ivy.log().error("Error connecting to {0}.", ex, targetUri);
  }

  private void connectToTarget(KeyStoreInfo customTS, KeyStoreInfo systemTS, KeyStoreInfo customKS,
      KeyStoreInfo systemKS, TLSTestData data, String protocol)
  {
    try
    {
      SSLSocketFactory sslSocketFactory = TLSUtils
          .getSSLSocketFactory(protocol, customTS, systemTS, customKS, systemKS);
      data.addEntry("SSLSocketFactory created, setting protocol '%1$s'", protocol);
      connectWithTLS(data, sslSocketFactory);
    } catch (Exception ex)
    {
      handleConnectException(data, ex);
    }
  }

  private void connectWithTLS(TLSTestData data, SSLSocketFactory sslSocketFactory) throws Exception
  {
    URI uri = new URI(targetUri);
    int port = uri.getPort() < 1 ? 443 : uri.getPort();
    data.addEntry("Creating SSLSocket to host %1$s on port %2$d.", uri.getHost(), port);
    SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(uri.getHost(), port);
    data.addEntry("SSLSocket created.");
    socket.addHandshakeCompletedListener(new HandshakeCompletedListener()
    {
      @Override
      public void handshakeCompleted(HandshakeCompletedEvent event)
      {
        try
        {
          data.addEntry("SSLSocket handshake finished. Session Info is below.");
          data.addEntry("Protocol   : %1$s", event.getSession().getProtocol());
          data.addEntry("CipherSuite: %1$s", event.getCipherSuite());
          Certificate[] peerCerts = event.getPeerCertificates();
          for (int i = 0; i < peerCerts.length; i++)
          {
            data.addEntry("PeerCert [%1$d]: %2$s", i, getCertInfo(peerCerts[i]));
          }
          Certificate[] localCerts = event.getLocalCertificates();
          if (localCerts != null)
          {
            for (int i = 0; i < localCerts.length; i++)
            {
              data.addEntry("LocalCert [%1$d]: %2$s", i, getCertInfo(localCerts[i]));
            }
          } else
          {
            data.addEntry("No client certificates used.");
          }
        } catch (SSLPeerUnverifiedException e)
        {
          throw new RuntimeException(e);
        }
      }
    });
    socket.startHandshake();
    data.addEntry("Successfully connected to %1$s!", targetUri);
  }

  private static KeyStoreInfo loadKeyStore(String storeFilename, String propPwd, String propType, String propProv,
      String storeText, TLSTestData data)
  {
    char[] storePassword = System.getProperty(propPwd, "").toCharArray();
    String storeType = System.getProperty(propType, DEFAULT_STORE_TYPE);
    String storeProvider = System.getProperty(propProv);
    return loadKeyStore(storeFilename, storePassword, storeType, storeProvider, storeText, data);
  }

  private static KeyStoreInfo loadKeyStore(String storeFilename, char[] ksPwd, String ksType, String ksProv,
      String storeText, TLSTestData data)
  {
    KeyStore keyStore = null;
    try
    {
      keyStore = TLSUtils.loadKeyStore(storeFilename, ksPwd, ksType, ksProv);
      data.addEntry("Successfully loaded %1$s file %2$s.", storeText, storeFilename);
      data.addEntry("%1$s type: %2$s", storeText, keyStore.getType());
      data.addEntry("%1$s provider: %2$s", storeText, keyStore.getProvider().getName());
      addKeyStoreInfo(keyStore, ksPwd, data);
    } catch (Exception ex)
    {
      data.addEntry("Error loading client %1$s: %2$s - ignoring", storeText, ex.getMessage());
      data.setResult(0);
//      Ivy.log().error("Error loading {0} file {1}.", ex, storeText, storeFilename);
    }
    return new KeyStoreInfo(keyStore, ksPwd);
  }

  private static void addKeyStoreInfo(KeyStore store, char[] passwd, TLSTestData data) throws Exception
  {
    boolean hasEntries = false;
    for (Enumeration<String> aliases = store.aliases(); aliases.hasMoreElements();)
    {
      hasEntries = true;
      String alias = aliases.nextElement();
      if (store.isKeyEntry(alias))
      {
        try
        {
          data.addEntry("Key alias found: %1$s (alg=%2$s)", alias, store.getKey(alias, passwd).getAlgorithm());
        }
        catch (Exception ex)
        {
          data.addEntry("Error loading private key with alias '%1$s': %2$s - ignoring", alias, ex.getMessage());
        }
      }
      data.addEntry("Cert alias found: " + alias + " (" + getCertInfo(store.getCertificate(alias)) + ")");
    }
    if (!hasEntries)
    {
      data.addEntry("No cert aliases found - store seems to be empty");
      return;
    }
  }

  private static String getCertInfo(Certificate cert)
  {
    X509Certificate x509Cert = (X509Certificate) cert;
    PublicKey pubKey = x509Cert.getPublicKey();
    String issuerName = x509Cert.getIssuerDN().getName();
    String subjectName = x509Cert.getSubjectDN().getName();
    return String.format("alg=%1$s, length=%2$d, issuer=%3$s, subject=%4$s.", pubKey.getAlgorithm(),
        getBitLength(pubKey), issuerName.equals(subjectName) ? "selfSigned" : issuerName, subjectName);
  }

  private static int getBitLength(PublicKey pubKey)
  {
    int bitLength = -1;
    if (pubKey instanceof RSAPublicKey)
    {
      bitLength = ((RSAPublicKey) pubKey).getModulus().bitLength();
    } else if (pubKey instanceof DSAPublicKey)
    {
      bitLength = ((DSAPublicKey) pubKey).getParams().getP().bitLength();
    } else if (pubKey instanceof ECPublicKey)
    {
      bitLength = ((ECPublicKey) pubKey).getParams().getCurve().getField().getFieldSize();
    }
    return bitLength;
  }

  private static String getPropInfo(String propName, String defaultValue, boolean isPassword)
  {
    String propValue = System.getProperty(propName, defaultValue);
    return defaultValue.equals(propValue) ? propName + " is NOT set, using default '" + defaultValue + "'\n" : propName
        + " is set: '" + (isPassword ? "********" : propValue) + "'\n";
  }
}
