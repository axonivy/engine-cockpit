package ch.ivyteam.enginecockpit;

import java.nio.charset.StandardCharsets;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.restricted.ApplicationConfigurationDumper;
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
  
  public StreamedContent getErrorReport()
  {
    IDumper[] dumpers = ErrorReport.addStandardDumpers(false,
            new ApplicationConfigurationDumper(applicationConfigurationManager),
            new PersistencyDumper(systemDatabasePersistencyService));

    String errorReport = ErrorReport.createErrorReport(dumpers);
    return new DefaultStreamedContent(IOUtils.toInputStream(errorReport, StandardCharsets.UTF_8), "text/plain"/*"application/zip"*/, "errorReport");
  }
}
