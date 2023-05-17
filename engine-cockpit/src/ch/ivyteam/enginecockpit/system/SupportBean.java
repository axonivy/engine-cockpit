package ch.ivyteam.enginecockpit.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.restricted.ApplicationConfigurationDumper;
import ch.ivyteam.ivy.error.restricted.ErrorReport;
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
      try (var walker = Files.walk(UrlUtil.getLogDir().toRealPath())) {
        walker.filter(Files::isRegularFile)
          .filter(log -> log.toString().endsWith(".log"))
          .forEach(log -> addLogToZip(zos, log.toFile()));
      }
    }
    return zipFile;
  }

  private void addLogToZip(ZipOutputStream zos, File log) {
    try {
      byte[] logContent = Files.readAllBytes(log.toPath());
      addEntryToZip(zos, log.getName(), logContent);
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
            .stream(() -> getZipFileStream(zipFile))
            .contentType("application/zip")
            .name("support-engine-report.zip")
            .build();
  }

  private InputStream getZipFileStream(File file) {
    try {
      return new FileInputStream(file) {
        @Override
        public void close() throws IOException {
          super.close();
          try {
            file.delete();
          } catch (Exception ex) {
            LOGGER.info("Could not delete temporary support zip file '" + file.getName() + "' : ", ex);
          }
        }
      };
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  private String createSupportReport() {
    var dumpers = ErrorReport.addStandardDumpers(false,
            new ApplicationConfigurationDumper(IApplicationRepository.instance()),
            new PersistencyDumper(systemDbService));
    return ErrorReport.createErrorReport(null, dumpers);
  }
}
