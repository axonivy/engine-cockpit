package ch.ivyteam.enginecockpit.fileupload;

import java.util.Arrays;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;

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
    return Ivy.cms().co("/applications/DeploymentPossibleReason",
        Arrays.asList(REST_SERVLET_ENABLED, REST_DEPLOYMENT_ENABLED));
  }

  private static boolean isRestEnabled() {
    return IConfiguration.instance().getOrDefault(REST_SERVLET_ENABLED, Boolean.class);
  }

  private static boolean isDeploymentEnabled() {
    return IConfiguration.instance().getOrDefault(REST_DEPLOYMENT_ENABLED, Boolean.class);
  }
}
