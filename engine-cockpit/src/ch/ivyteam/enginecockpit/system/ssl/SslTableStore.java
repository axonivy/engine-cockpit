package ch.ivyteam.enginecockpit.system.ssl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import org.primefaces.event.FileUploadEvent;

public interface SslTableStore {

  void deleteCertificate(String alias) throws Exception;
  void handleUploadCertificate(FileUploadEvent event) throws Exception;
  List<StoredCert> getCertificats();

  boolean isKeystore();
  String getFile();
  void setFile(String file);
  String getPassword();
  void setPassword(String password);
  String getProvider();
  void setProvider(String provider);
  List<String> getProviders();
  List<String> getTypes();
  String getAlgorithm();
  List<String> getAlgorithms();
  void setAlgorithm(String algorithm);
  String getType();
  void setType(String type);
  void save();
  String getStorePassword();
  String getStoreKeyPassword();
  void confirmPassword() throws CertificateException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException;

  String getCertificateLoadError();
  void setCertificateLoadError(String certificateLoadError);
}
