package ch.ivyteam.enginecockpit.util;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.jsoup.Jsoup;

public class HttpAsserter {

  public static HttpAssert assertThat(String url) {
    return new HttpAssert(url);
  }

  public static class HttpAssert {

    private String url;

    private HttpAssert(String url) {
      this.url = url;
    }

    public void hasNoDeadLinks(int maxDepth, String sessionId) {
      hasNoDeadLinks(maxDepth, 0, new HashSet<>(), new HashSet<>(), sessionId);
    }

    private void hasNoDeadLinks(int maxDepth, int currentDepth, Set<String> crawled, Set<String> checked, String sessionId) {
      if (maxDepth <= currentDepth) {
        return;
      }
      if (crawled.contains(url)) {
        return;
      }

      var linksFound = parseLinks(url, sessionId);
      System.out.println(url + " found " + linksFound.size() + " links");
      crawled.add(url);

      var deadLinks = findDeadLinks(linksFound, checked, sessionId);
      Assertions.assertThat(deadLinks).as("found dead links on " + url).isEmpty();
      checked.addAll(linksFound);

      currentDepth += 1;
      for (var link : linksFound) {
        if (link.contains("faces/view/engine-cockpit")) {
          assertThat(link).hasNoDeadLinks(maxDepth, currentDepth, crawled, checked, sessionId);
        }
      }
    }

    private Set<String> findDeadLinks(Set<String> linksToCheck, Set<String> linksAlreadyChecked, String sessionId) {
      return linksToCheck.stream()
              .filter(link -> !linksAlreadyChecked.contains(link))
              .filter(link -> !check(link, sessionId))
              .collect(Collectors.toSet());
    }

    private static Set<String> parseLinks(String url, String sessionId) {
      System.out.println(url + " crawl");
      var result = new HashSet<String>();
      try {
        var con = Jsoup.connect(url);
        con.cookie("JSESSIONID", sessionId);
        var doc = con.get();
        var links = doc.select("a[href]");
        for (var link : links) {
          var href = link.attr("href");
          href = StringUtils.removeEnd(href, "#");
          if (StringUtils.isEmpty(href)) {
            continue;
          }
          var u = URI.create(href);
          if (u.isAbsolute()) {
            result.add(href);
          } else {
            var absolute = URI.create(url).resolve(u).toString();
            result.add(absolute);
          }
        }
        return result;
      } catch (IOException ex) {
        throw new RuntimeException("Could not crawl " + url, ex);
      }
    }

    private boolean check(String urlToCheck, String sessionId) {
        System.out.println("check " + urlToCheck);
        try {
          var con = Jsoup.connect(urlToCheck);
          con.cookie("JSESSIONID", sessionId);
          con.get();
          return true;
        } catch (IOException ex) {
          return false;
        }
    }
  }
}
