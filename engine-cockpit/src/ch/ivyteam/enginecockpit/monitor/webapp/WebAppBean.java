package ch.ivyteam.enginecockpit.monitor.webapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import ch.ivyteam.enginecockpit.application.ApplicationBean;
import ch.ivyteam.enginecockpit.application.ApplicationVersionBean;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.webserver.monitor.WebAppMonitor;
import ch.ivyteam.ivy.webserver.monitor.WebContext;
import ch.ivyteam.ivy.webserver.monitor.WebContext.Kind;
import ch.ivyteam.ivy.webserver.monitor.WebServlet;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.UriBuilder;

@Named
@ViewScoped
public class WebAppBean implements Serializable {

  private List<WebContext> contexts = List.of();
  private String path;
  private WebContext context;
  private List<WebServlet> servlets = List.of();

  public void refresh() {
    contexts = new ArrayList<>(WebAppMonitor.instance().contexts());
  }

  public void onload() {
    context = WebAppMonitor.instance().contexts().stream()
        .filter(webContext -> displayPath(webContext).equals(path))
        .findFirst()
        .orElse(null);
    if (context == null) {
      ResponseHelper.notFound("Web context not found: " + path);
      return;
    }
    servlets = new ArrayList<>(context.servlets());
  }

  public List<WebContext> getContexts() {
    return contexts;
  }

  public WebContext getContext() {
    return context;
  }

  public List<WebServlet> getServlets() {
    return servlets;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String displayPath(WebContext webContext) {
    if (webContext.path().isEmpty()) {
      return "/";
    }
    return webContext.path();
  }

  public String link(WebContext webContext) {
    return UriBuilder.fromPath("monitor-webapp.xhtml")
        .queryParam("path", displayPath(webContext))
        .build()
        .toString();
  }

  public String appLink(WebContext webContext) {
    if (webContext.version() == null) {
      return ApplicationBean.getLink(webContext.securitySystem(), webContext.app());
    }
    return ApplicationVersionBean.getLink(webContext.securitySystem(), webContext.app(), webContext.version());
  }

  public String securitySystemLink(WebContext webContext) {
    return UriBuilder.fromPath("security-detail.xhtml")
        .queryParam("securitySystemName", webContext.securitySystem())
        .build()
        .toString();
  }

  public String kindLabel(Kind kind) {
    return Ivy.cm().co("/monitor/webApp/kind/" + kind.name());
  }

  public String kindIcon(Kind kind) {
    return switch (kind) {
      case BASE -> "ti-home";
      case DESIGNER -> "ti-pencil";
      case APP -> "ti-layout-grid";
      case APP_REDIRECT -> "ti-arrow-forward-up";
      case SECURITY_SYSTEM -> "ti-shield-lock";
      case ENGINE_COCKPIT -> "ti-device-analytics";
      case DEV_WORKFLOW_UI -> "ti-list-check";
      case OTHER -> "ti-box";
    };
  }

  public String stateClass(WebContext webContext) {
    return "state-app-" + webContext.state().toLowerCase();
  }

  public String shortName(String name) {
    return ClassUtils.getShortClassName(name);
  }

  public String join(List<String> values) {
    return String.join(", ", values);
  }

  public String joinShortNames(List<String> names) {
    return join(names.stream().map(ClassUtils::getShortClassName).toList());
  }
}
