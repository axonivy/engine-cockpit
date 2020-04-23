package ch.ivyteam.enginecockpit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.error.restricted.ErrorReport;
import ch.ivyteam.ivy.error.restricted.IDumper;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.persistence.restricted.PersistencyDumper;

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
    IDumper[] dumpers = ErrorReport.addStandardDumpers(false,
            new ApplicationConfigurationDumper(applicationConfigurationManager),
            new PersistencyDumper(systemDatabasePersistencyService));

    String errorReport = ErrorReport.createErrorReport(dumpers);
    Path tempDirectory = Files.createTempDirectory("errorReport");
    Ivy.log().info(tempDirectory);
    Files.writeString(Files.createFile(tempDirectory.resolve("report.txt")), errorReport);
    Files.walk(UrlUtil.getLogDir().toPath()).filter(Files::isRegularFile)
            .forEach(log -> {
              try
              {
                Ivy.log().info(log);
                Ivy.log().info(tempDirectory.resolve(log.getFileName()));
                Files.copy(log, tempDirectory.resolve(log.getFileName()));
              }
              catch (IOException ex)
              {
                // TODO Auto-generated catch block
                ex.printStackTrace();
              }
            });
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DownloadUtil.compressDirectory(tempDirectory.toFile(), out);
    return new DefaultStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/zip", "errorReport.zip");
  }
}
