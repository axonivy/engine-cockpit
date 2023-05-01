package ch.ivyteam.enginecockpit.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.restricted.ApplicationConfigurationDumper;
import ch.ivyteam.ivy.error.restricted.ErrorReport;
import ch.ivyteam.ivy.log.provider.LogFile;
import ch.ivyteam.ivy.log.provider.LogProvider;
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
    var tempDirectory = collectReportData(errorReport);
    var out = new ByteArrayOutputStream();
    DownloadUtil.zipDir(tempDirectory, out);
    FileUtils.deleteDirectory(tempDirectory.toFile());
    return DefaultStreamedContent
        .builder()
        .stream(() ->new ByteArrayInputStream(out.toByteArray()))
        .contentType("application/zip")
        .name("support-engine-report.zip")
        .build();
  }

  private String createSupportReport() {
    var dumpers = ErrorReport.addStandardDumpers(false,
            new ApplicationConfigurationDumper(IApplicationRepository.instance()),
            new PersistencyDumper(systemDbService));
    return ErrorReport.createErrorReport(dumpers);
  }

  private Path collectReportData(String errorReport) throws IOException {
    var tempDirectory = Files.createTempDirectory("SupportReport");
    Files.writeString(Files.createFile(tempDirectory.resolve("report.txt")), errorReport);
    LogProvider.all().stream()
      .flatMap(l -> l.logs().stream())
      .filter(LogFile::isLog)
      .forEach(log -> copyLogFile(log.path(), tempDirectory));
    return tempDirectory;
  }

  private void copyLogFile(Path log, Path tempDirectory) {
    try {
      Files.copy(log, tempDirectory.resolve(log.getFileName()));
    } catch (IOException ex) {
      LOGGER.info("Couldn't copy file '" + log + "' to tempDir '" + tempDirectory + "': ", ex);
    }
  }
}
