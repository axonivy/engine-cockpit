package ch.ivyteam.enginecockpit.util;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.jsoup.Jsoup;

public class HttpAsserter {

  public static HttpAssert assertThat(String url, String sessionId, List<String> ignorePages) {
    return new HttpAssert(url, sessionId, ignorePages);
  }

  public static class HttpAssert {

    private final String testUrl;
    private final Set<String> processed = new HashSet<>();
    private final Set<String> deadLinks = new HashSet<>();
    private final String sessionId;
    private final List<String> ignorePages;

    private HttpAssert(String testUrl, String sessionId, List<String> ignorePages) {
      this.testUrl = testUrl;
      this.sessionId = sessionId;
      this.ignorePages = ignorePages;
    }

    public void hasNoDeadLinks() {
      processed.clear();
      processed.addAll(ignorePages);
      deadLinks.clear();
      crawlAndCheckLinks(testUrl);

      Assertions.assertThat(deadLinks).as("found dead links").isEmpty();
    }

    private void crawlAndCheckLinks(String url) {
      crawlAndCheckLinks(url, null);
    }

    private void crawlAndCheckLinks(String url, String sourcePage) {
      if (processed.contains(url)) {
        return;
      }

      processed.add(url);
      if (sourcePage != null) {
        System.out.println("Processing " + url + " (found on: " + sourcePage + ")");
      } else {
        System.out.println("Processing " + url);
      }

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

          var resolvedUrl = resolveUrl(url, href);
          if (resolvedUrl == null || processed.contains(resolvedUrl)) {
            continue;
          }

          if (isCockpitLink(resolvedUrl)) {
            crawlAndCheckLinks(resolvedUrl, url);
          } else {
            checkLinkAvailabilityOnly(resolvedUrl, url);
          }
        }

      } catch (IOException ex) {
        if (sourcePage != null) {
          System.err.println("Could not crawl " + url + ": " + ex.getMessage() + " (found on: " + sourcePage + ")");
        } else {
          System.err.println("Could not crawl " + url + ": " + ex.getMessage());
        }
        deadLinks.add(url);
      }
    }

    private String resolveUrl(String baseUrl, String href) {
      try {
        var u = URI.create(href.replace(' ', '+'));
        if (u.isAbsolute()) {
          return href;
        } else {
          return URI.create(baseUrl).resolve(u).toString();
        }
      } catch (Exception ex) {
        System.err.println("Could not resolve URL: " + href + " from base: " + baseUrl);
        return null;
      }
    }

    private boolean isCockpitLink(String url) {
      return url.contains("/faces/") && url.contains("/engine-cockpit/");
    }

    private void checkLinkAvailabilityOnly(String url, String sourcePage) {
      if (processed.contains(url)) {
        return;
      }

      processed.add(url);
      System.out.println("Checking " + url + " (found on: " + sourcePage + ")");

      try {
        var con = Jsoup.connect(url);
        con.cookie("JSESSIONID", sessionId);
        con.method(org.jsoup.Connection.Method.HEAD);
        con.execute();
      } catch (IOException ex) {
        System.err.println("Dead link found: " + url + " - " + ex.getMessage() + " (found on: " + sourcePage + ")");
        deadLinks.add(url);
      }
    }
  }
}
