package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.Advisor;

class TestUrlUtil {

  private String version;

  @BeforeEach
  void beforeEach() {
    var v = Advisor.instance().getVersion();
    version = v.getMajorVersion() + "." + v.getMinorVersion(); 
  }

  @Test
  void docBaseUrls() {
    assertThat(UrlUtil.getEngineGuideBaseUrl())
            .isEqualTo("https://developer.axonivy.com/doc/"+version+"/engine-guide");
    assertThat(UrlUtil.getCockpitEngineGuideUrl())
            .isEqualTo("https://developer.axonivy.com/doc/"+version+"/engine-guide/reference/engine-cockpit/");
    assertThat(UrlUtil.getDesignerGuideBaseUrl())
            .isEqualTo("https://developer.axonivy.com/doc/"+version+"/designer-guide");
  }

  @Test
  void replaceEngineGuide() {
    var plainText = "@engine.guide.url@/configuration/advanced-configuration.html#overriding-configuration";
    var outputText = "\n" +
            "<a href='https://developer.axonivy.com/doc/"+version+"/engine-guide/configuration/advanced-configuration.html#overriding-configuration' target='_blank'>https://developer.axonivy.com/doc/"+version+"/engine-guide/configuration/advanced-configuration.html#overriding-configuration</a>";
    assertThat(UrlUtil.replaceLinks(plainText)).isEqualTo(outputText);
  }

  @Test
  void replaceDesignerGuide() {
    var plainText = "@designer.guide.url@/user-interface/standard-processes";
    var outputText = "\n" +
            "<a href='https://developer.axonivy.com/doc/"+version+"/designer-guide/user-interface/standard-processes' target='_blank'>https://developer.axonivy.com/doc/"+version+"/designer-guide/user-interface/standard-processes</a>";
    assertThat(UrlUtil.replaceLinks(plainText)).isEqualTo(outputText);
  }

  @Test
  void replaceHtmlLinks() {
    var plainText = "To install your own Elasticsearch server follow these steps\n" +
            "https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html";
    var outputText = "To install your own Elasticsearch server follow these steps\n" +
            "<a href='https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html' target='_blank'>https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html</a>";
    assertThat(UrlUtil.replaceLinks(plainText)).isEqualTo(outputText);
  }
}
