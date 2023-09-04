package ch.ivyteam.enginecockpit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.restricted.ApplicationConfigurationDumper;
import ch.ivyteam.ivy.error.restricted.ErrorReport;
import ch.ivyteam.ivy.error.restricted.IDumper;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.persistence.restricted.PersistencyDumper;
import ch.ivyteam.log.Logger;

@SuppressWarnings("restriction")
@ManagedBean
@RequestScoped
public class SupportBean {

  @Inject
  private IApplicationConfigurationManager applicationConfigurationManager;
  @Inject
  private ISystemDatabasePersistencyService systemDatabasePersistencyService;
  private final static Logger LOGGER = Logger.getLogger(SupportBean.class);

  public SupportBean() {
    DiCore.getGlobalInjector().injectMembers(this);
  }

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
      try (var walker = Files.walk(UrlUtil.getLogDir().toPath(), FileVisitOption.FOLLOW_LINKS)) {
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
    return new DefaultStreamedContent(
            DownloadUtil.getFileStream(zipFile),
            "application/zip",
            "support-engine-report.zip");
  }

  private String createSupportReport() {
    IDumper[] dumpers = ErrorReport.addStandardDumpers(false,
            new ApplicationConfigurationDumper(applicationConfigurationManager),
            new PersistencyDumper(systemDatabasePersistencyService));
    return ErrorReport.createErrorReport(dumpers);
  }
}
