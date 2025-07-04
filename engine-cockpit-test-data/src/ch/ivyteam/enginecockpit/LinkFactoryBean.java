package ch.ivyteam.enginecockpit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.engine.cockpit.CockpitLinkFactory;
import ch.ivyteam.ivy.model.value.WebLink;

@ManagedBean
@RequestScoped
public class LinkFactoryBean {

  private static final String DESIGNER_APP_CONTEXT = "/" + IApplication.DESIGNER_APPLICATION_NAME;

  public List<Link> getLinks() {
    var links = new ArrayList<>(Stream.of(CockpitLinkFactory.class.getMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
        .filter(m -> m.getParameterCount() == 0)
        .filter(m -> m.getReturnType().equals(WebLink.class))
        .map(this::toLink)
        .toList());
    links.add(externalDatabase());
    links.add(restService());
    return links;
  }

  private Link toLink(Method method) {
    try {
      var link = (WebLink) method.invoke(null);
      return toLink(method.getName(), link);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private Link toLink(String title, WebLink link) {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    var appContext = externalContext.getRequestContextPath();
    String path;
    if (!DESIGNER_APP_CONTEXT.equals(appContext)) {
      path = "/system/engine-cockpit/faces/" + link.getRelative();
    } else {
      path = appContext + externalContext.getRequestServletPath() + externalContext.getRequestPathInfo();
      path = path.replace("engine-cockpit-test-data", "engine-cockpit").replace("link-factory.xhtml", link.getRelative());
    }
    link = WebLink.of(path);
    return new Link(title, link.getAbsolute());
  }

  private Link externalDatabase() {
    return toLink("External Database realDb", CockpitLinkFactory.externalDatabase(applicationName(), "realdb"));
  }

  private Link restService() {
    return toLink("Rest Client test-rest", CockpitLinkFactory.restClient(applicationName(), "test-rest"));
  }

  private String applicationName() {
    return IApplicationRepository.instance().designer()
        .map(IApplication::getName)
        .orElse("test");
  }

  public static class Link {

    private final String title;
    private final String href;

    public Link(String title, String href) {
      this.title = title;
      this.href = href;
    }

    public String getTitle() {
      return title;
    }

    public String getHref() {
      return href;
    }
  }
}
