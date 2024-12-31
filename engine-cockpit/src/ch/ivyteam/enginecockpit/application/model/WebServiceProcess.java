package ch.ivyteam.enginecockpit.application.model;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.workflow.IWebServiceProcess;
import ch.ivyteam.ivy.workflow.IWebServiceProcessStartElement;

public class WebServiceProcess {

  private final String link;
  private final String processName;
  private final String name;

  public WebServiceProcess(IWebServiceProcess ws) {
    link = ws.getWsdlUri().toASCIIString();
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
    String processIdentifier = ws.getProcessIdentifier();
    return ws.getWebServiceProcessStartElements().stream().findFirst()
        .map(start -> toProcessName(start, processIdentifier))
        .orElse(processIdentifier);
  }

  private static String toProcessName(IWebServiceProcessStartElement start, String processIdentifier) {
    String path = start.getUserFriendlyRequestPath();
    return StringUtils.substring(path, 0, path.indexOf(processIdentifier) - 1);
  }

}
