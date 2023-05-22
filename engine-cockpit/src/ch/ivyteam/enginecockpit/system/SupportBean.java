package ch.ivyteam.enginecockpit.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.restricted.ApplicationConfigurationDumper;
import ch.ivyteam.ivy.error.restricted.ErrorReport;
import ch.ivyteam.ivy.log.provider.LogFile;
import ch.ivyteam.ivy.log.provider.LogFileRepository;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.persistence.restricted.PersistencyDumper;
import ch.ivyteam.log.Logger;

@SuppressWarnings("restriction")
@ManagedBean
@RequestScoped
public class SupportBean {

  private final static Logger LOGGER = Logger.getLogger(SupportBean.class);
  private ISystemDatabasePersistencyService systemDbService = ISystemDatabasePersistencyService.instance();

  public StreamedContent getSupportReport() throws IOException {
    var errorReport = createSupportReport();
    var zippedFile = createZippedFile(errorReport);
    return createStreamedContent(zippedFile);
  }

  private File createZippedFile(String errorReport) throws IOException {
    var tempSupportDir = Files.createTempDirectory("SupportReport");
    var zipFile = tempSupportDir.resolve("support-engine-report.zip").toFile();
    try (var fos = new FileOutputStream(zipFile);
            var zos = new ZipOutputStream(fos)) {
      addEntryToZip(zos, "report.txt", errorReport.getBytes());
      LogFileRepository.instance().all().forEach(log -> addLogToZip(zos, log));
    }
    return zipFile;
  }

  private void addLogToZip(ZipOutputStream zos, LogFile log) {
    try {
      byte[] logContent = Files.readAllBytes(log.path());
      addEntryToZip(zos, log.name(), logContent);
    } catch (IOException ex) {
      LOGGER.info("Could not read log file '" + log + "': ", ex);
    }
  }

  private void addEntryToZip(ZipOutputStream zos, String entryName, byte[] content) throws IOException {
    ZipEntry entry = new ZipEntry(entryName);
    zos.putNextEntry(entry);
    zos.write(content);
    zos.closeEntry();
  }

  private StreamedContent createStreamedContent(File zipFile) {
    return DefaultStreamedContent.builder()
            .stream(() -> DownloadUtil.getFileStream(zipFile))
            .contentType("application/zip")
            .name("support-engine-report.zip")
            .build();
  }

  private String createSupportReport() {
    var dumpers = ErrorReport.addStandardDumpers(false,
            new ApplicationConfigurationDumper(IApplicationRepository.instance()),
            new PersistencyDumper(systemDbService));
    return ErrorReport.createErrorReport(null, dumpers);
  }
}
