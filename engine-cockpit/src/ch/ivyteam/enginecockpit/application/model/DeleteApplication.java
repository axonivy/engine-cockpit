package ch.ivyteam.enginecockpit.application.model;

import ch.ivyteam.enginecockpit.application.ApplicationBean.ApplicationVersionRow;

public class DeleteApplication {

  private ApplicationVersionRow app;
  private boolean isLastVersion;

  public DeleteApplication(ApplicationVersionRow app, boolean isLastVersion) {
    this.app = app;
    this.isLastVersion = isLastVersion;
  }  

  public int getVersion() {
    return Integer.parseInt(app.getVersion());
  }

  public long getOpenCases() {
    return app.getOpenCases();
  }

  public long getDoneCases() {
    return app.getDoneCases();
  }

  public String getExistingCasesSeverity() {
    return hasExistingCases() ? "warn" : "info";
  }

  public String getExistingCasesMessage() {
    if (hasExistingCases()) {
      return "There are " + getOpenCases() + " open cases and " + getDoneCases() + " done cases which will be deleted from the database.";
    }
    return "Deleting this version will not delete any cases from the database.";
  }

  private boolean hasExistingCases() {
    return getOpenCases() > 0 || getDoneCases() > 0;
  }

  public boolean isLastVersion() {
    return isLastVersion;
  }

  public String getLastVersionMessage() {
    return "This is the last version of the application. Deleting it will remove the application from the system.";
  }
}
