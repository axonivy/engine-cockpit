package ch.ivyteam.enginecockpit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import ch.ivyteam.ivy.engine.cockpit.CockpitLinkFactory;
import ch.ivyteam.ivy.model.value.WebLink;

@ManagedBean
@RequestScoped
public class LinkFactoryBean {

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
    var path = "/system/engine-cockpit/faces/" + link.getRelative();
    link = WebLink.of(path);
    return new Link(title, link.getAbsolute());
  }

  private Link externalDatabase() {
    return toLink("External Database realDb", CockpitLinkFactory.externalDatabase("test", "realdb"));
  }

  private Link restService() {
    return toLink("Rest Client test-rest", CockpitLinkFactory.restClient("test", "test-rest"));
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
