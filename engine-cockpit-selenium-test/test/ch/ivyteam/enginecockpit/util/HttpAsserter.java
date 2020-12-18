package ch.ivyteam.enginecockpit.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;

public class HttpAsserter
{
  final static Pattern A_TAG = Pattern.compile("(?i)<a([^>]+)>");
  final static Pattern A_LINK = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
  final static Pattern BUTTON_TAG = Pattern.compile("(?i)<button([^>]+)>");
  final static Pattern BUTTON_LINK = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");

  public static HttpAssert assertThat(String url)
  {
    return new HttpAssert(url);
  }

  public static class HttpAssert
  {
    private String url;

    private HttpAssert(String url)
    {
      this.url = url;
    }

    public void exists()
    {
      Assertions.assertThat(getResponse(url).statusCode()).isEqualTo(200);
    }

    private static HttpResponse<Void> getResponse(String url)
    {
      return getResponse(url, false);
    }

    private static HttpResponse<Void> getResponseFollowRedirects(String url)
    {
      return getResponse(url, true);
    }

    private static String getContent(String url)
    {
      try
      {
        System.out.println("Crawling (GET): " + url);
        var client = HttpClient.newBuilder().followRedirects(Redirect.NEVER).build();
        var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        var response = client.send(request, BodyHandlers.ofString());
        return response.body();
      }
      catch (Exception ex)
      {
        throw new RuntimeException("Could not crawl: " + url, ex);
      }
    }

    private static HttpResponse<Void> getResponse(String url, boolean followRedirects)
    {
      try
      {
        var method = "HEAD";
        if (url.contains("developer.axonivy.com") || url.contains("file.axonivy.rocks"))
        {
          method = "GET";
        }
        System.out.println("Crawling (" + method + " - Drop Body): " + url);

        var redirectPolicy = followRedirects ? Redirect.ALWAYS : Redirect.NEVER;
        var client = HttpClient.newBuilder().followRedirects(redirectPolicy).build();
        var request = HttpRequest.newBuilder()
                .method(method, BodyPublishers.noBody())
                .uri(URI.create(url))
                .build();
        return client.send(request, BodyHandlers.discarding());
      }
      catch (Exception ex)
      {
        throw new RuntimeException("Could not crawl: " + url, ex);
      }
    }

    private static final Set<String> DO_NOT_CHECK_LINK_WHICH_STARTS_WITH = Set.of(
            "mailto",
            "javascript");

    public void hasNoDeadLinks()
    {
      assertThat(url).exists();

      Set<String> links = parseAllLinksOfPage(url, A_TAG, A_LINK);
      links.addAll(parseAllLinksOfPage(url, BUTTON_TAG, BUTTON_LINK));
      Assertions.assertThat(links).as("Found no external links on " + url).isNotEmpty();
      System.out.println("Found " + links.size() + " links on page " + url);

      Set<String> failingLinks = getDeadLinks(links);
      Assertions.assertThat(failingLinks).as("Found dead links on " + url).isEmpty();
    }

    private static Set<String> getDeadLinks(Set<String> links)
    {
      return links.stream()
              .filter(link -> getResponseFollowRedirects(link).statusCode() != 200)
              .collect(Collectors.toSet());
    }

    private static Set<String> parseAllLinksOfPage(String baseUrl, Pattern pTag, Pattern pLink)
    {
      var content = getContent(baseUrl);
      var result = new HashSet<String>();
      var matcherTag = pTag.matcher(content);
      while (matcherTag.find())
      {
        var href = matcherTag.group(1);
        var matcherLink = pLink.matcher(href);
        while (matcherLink.find())
        {
          var link = matcherLink.group(1);
          var quote = StringUtils.substring(link, 0, 1);
          link = StringUtils.substringBetween(link, quote, quote);
          link = StringUtils.substringBeforeLast(link, "#");

          if (ignoreLink(link) || !link.startsWith("http"))
          {
            continue;
          }
          result.add(link);
        }
      }
      return result;
    }

    private static boolean ignoreLink(String link)
    {
      if (StringUtils.isBlank(link))
      {
        return true;
      }
      if (DO_NOT_CHECK_LINK_WHICH_STARTS_WITH.stream().anyMatch(pattern -> link.startsWith(pattern)))
      {
        return true;
      }

      return false;
    }

  }
}
