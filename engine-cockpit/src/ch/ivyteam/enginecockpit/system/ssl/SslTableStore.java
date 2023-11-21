package ch.ivyteam.enginecockpit.system.ssl;

import java.security.cert.Certificate;
import java.util.List;

import org.primefaces.event.FileUploadEvent;

public interface SslTableStore {

  void deleteCertificate(String alias);
  Certificate handleUploadCertificate(FileUploadEvent event) throws Exception;
  List<StoredCert> getCertificats();

}
