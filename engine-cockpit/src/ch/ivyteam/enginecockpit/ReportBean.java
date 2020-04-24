package ch.ivyteam.enginecockpit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.restricted.ApplicationConfigurationDumper;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.error.restricted.ErrorReport;
import ch.ivyteam.ivy.error.restricted.IDumper;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.persistence.restricted.PersistencyDumper;

@SuppressWarnings("restriction")
@ManagedBean
@RequestScoped
public class ReportBean
{
  
  @Inject
  private IApplicationConfigurationManager applicationConfigurationManager;
  
  @Inject
  private ISystemDatabasePersistencyService systemDatabasePersistencyService;

  public ReportBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
  }
  
  public StreamedContent getErrorReport() throws IOException
  {
    String errorReport = createErrorReport();
    Path tempDirectory = collectReportData(errorReport);
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DownloadUtil.zipDir(tempDirectory, out);
    FileUtils.deleteDirectory(tempDirectory.toFile());
    return new DefaultStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/zip", "errorReport.zip");
  }

  private String createErrorReport()
  {
    IDumper[] dumpers = ErrorReport.addStandardDumpers(false,
            new ApplicationConfigurationDumper(applicationConfigurationManager),
            new PersistencyDumper(systemDatabasePersistencyService));

    return ErrorReport.createErrorReport(dumpers);
  }

  private Path collectReportData(String errorReport) throws IOException
  {
    Path tempDirectory = Files.createTempDirectory("errorReport");
    Files.writeString(Files.createFile(tempDirectory.resolve("report.txt")), errorReport);
    Files.walk(UrlUtil.getLogDir().toPath())
            .filter(Files::isRegularFile)
            .filter(log -> log.toString().endsWith(".log"))
            .forEach(log -> copyLogFile(log, tempDirectory));
    return tempDirectory;
  }

  private void copyLogFile(Path log, Path tempDirectory)
  {
    try
    {
      Files.copy(log, tempDirectory.resolve(log.getFileName()));
    }
    catch (IOException ex)
    {
      Ivy.log().info("Couldn't copy file '" + log + "' to tempDir '" + tempDirectory + "': ", ex);
    }
  }
}
