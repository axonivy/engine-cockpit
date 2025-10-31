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

  public static HttpAssert assertThat(String url, String sessionId) {
    return new HttpAssert(url, sessionId);
  }

  public static class HttpAssert {

    private final String testUrl;
    private final Set<String> crawled = new HashSet<>();
    private final Set<String> checked = new HashSet<>();
    private final int maxDepth = 10;
    private final String sessionId;

    private HttpAssert(String testUrl, String sessionId) {
      this.testUrl = testUrl;
      this.sessionId = sessionId;
    }

    public void hasNoDeadLinks() {
      crawled.clear();
      checked.clear();
      hasNoDeadLinks(0);
    }

    private void hasNoDeadLinks(int currentDepth) {
      hasNoDeadLinksForUrl(testUrl, currentDepth);
    }

    private void hasNoDeadLinksForUrl(String url, int currentDepth) {
      if (maxDepth <= currentDepth) {
        return;
      }
      if (crawled.contains(url)) {
        return;
      }

      var linksFound = parseLinks(url);
      crawled.add(url);

      var deadLinks = findDeadLinks(linksFound);
      Assertions.assertThat(deadLinks).as("found dead links on " + url).isEmpty();
      checked.addAll(linksFound);

      currentDepth += 1;
      for (var link : linksFound) {
        if (link.contains("/faces/") && link.contains("/engine-cockpit/")) {
          hasNoDeadLinksForUrl(link, currentDepth);
        }
      }
    }

    private Set<String> findDeadLinks(Set<String> linksToCheck) {
      return linksToCheck.stream()
          .filter(link -> !checked.contains(link))
          .filter(link -> !check(link))
          .collect(Collectors.toSet());
    }

    private Set<String> parseLinks(String url) {
      System.out.println("crawl " + url);
      var result = new HashSet<String>();
      try {
        var con = Jsoup.connect(url);
        con.cookie("JSESSIONID", sessionId);
        var doc = con.get();
        var links = doc.select("a[href]");
        for (var link : links) {
          var href = link.attr("href");
          href = StringUtils.substringBefore(href, "#");
          if (StringUtils.isEmpty(href)) {
            continue;
          }
          var u = URI.create(href.replace(' ', '+'));
          if (u.isAbsolute()) {
            result.add(href);
          } else {
            var absolute = URI.create(url).resolve(u).toString();
            result.add(absolute);
          }
        }
        System.out.println("Found " + result.size() + " links");
        return result;
      } catch (IOException ex) {
        throw new RuntimeException("Could not crawl " + url, ex);
      }
    }

    private boolean check(String url) {
      System.out.println("check " + url);
      try {
        var con = Jsoup.connect(url);
        con.cookie("JSESSIONID", sessionId);
        con.get();
        return true;
      } catch (IOException ex) {
        return false;
      }
    }
  }
}
