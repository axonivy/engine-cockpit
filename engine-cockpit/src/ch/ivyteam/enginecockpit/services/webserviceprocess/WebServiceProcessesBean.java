package ch.ivyteam.enginecockpit.services.webserviceprocess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.workflow.IWebServiceProcess;
import ch.ivyteam.ivy.workflow.IWebServiceProcessStartElement;
import ch.ivyteam.ivy.workflow.IWorkflowProcessModelVersion;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class WebServiceProcessesBean implements Serializable {

  private List<WebServiceProcess> webServiceProcesses;

  @SuppressWarnings("deprecation")
  public void onload() {
   var app = ManagerBean.instance().getSelectedApplication();
   if (app == null) {
     webServiceProcesses = new ArrayList<>();
     return;
   }
   webServiceProcesses = app.getProcessModelVersions()
          .map(IWorkflowProcessModelVersion::of)
          .filter(Objects::nonNull)
          .flatMap(pmv -> pmv.getWebServiceProcesses().stream())
          .map(WebServiceProcess::new)
          .collect(Collectors.toList());
  }

  public List<WebServiceProcess> getWebServiceProcesses() {
    return webServiceProcesses;
  }
  
  public static class WebServiceProcess {

    private final String link;
    private final String processName;
    private final String name;

    public WebServiceProcess(IWebServiceProcess ws) {
      link = ws.getWsdlUri().toString();
      processName = toProcessName(ws);
      name = ws.getName();
    }

    public String getLink() {
      return link;
    }

    public String getProcessName() {
      return processName;
    }

    public String getName() {
      return name;
    }

    private static String toProcessName(IWebServiceProcess ws) {
      var processIdentifier = ws.getProcessIdentifier();
      return ws.getWebServiceProcessStartElements().stream().findFirst()
          .map(start -> toProcessName(start, processIdentifier))
          .orElse(processIdentifier);
    }

    private static String toProcessName(IWebServiceProcessStartElement start, String processIdentifier) {
      var path = start.getUserFriendlyRequestPath();
      return StringUtils.substring(path, 0, path.indexOf(processIdentifier) - 1);
    }
  }
}
