package ch.ivyteam.enginecockpit.system.ssl;

import java.util.List;

import org.primefaces.event.FileUploadEvent;

public interface SslTableStore {

  void deleteCertificate(String alias) throws Exception;
  void handleUploadCertificate(FileUploadEvent event) throws Exception;
  List<StoredCert> getCertificats();

}
