package ch.ivyteam.enginecockpit.fileupload;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class UploadHelperBean {
  public static final String REST_SERVLET_ENABLED = "REST.Servlet.Enabled";
  public static final String REST_DEPLOYMENT_ENABLED = "REST.Servlet.Deployment";

  public boolean isDeploymentPossible() {
    return isRestEnabled() && isDeploymentEnabled();
  }

  public String getDeploymentPossibleReason() {
    return "The system configuration '" + REST_SERVLET_ENABLED + "' or '"
            + REST_DEPLOYMENT_ENABLED + "' is disabled.<br/>"
            + "Please enable them or use the file system for the deployment.";
  }

  private static boolean isRestEnabled() {
    return IConfiguration.instance().getOrDefault(REST_SERVLET_ENABLED, Boolean.class);
  }

  private static boolean isDeploymentEnabled() {
    return IConfiguration.instance().getOrDefault(REST_DEPLOYMENT_ENABLED, Boolean.class);
  }
}
