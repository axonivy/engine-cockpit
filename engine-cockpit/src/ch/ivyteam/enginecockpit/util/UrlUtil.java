package ch.ivyteam.enginecockpit.util;

import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.io.FileUtil;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.application.restricted.ApplicationConstants;
import ch.ivyteam.ivy.config.IFileAccess;

@SuppressWarnings("restriction")
public class UrlUtil {
  private static final Pattern URL_PATTERN = Pattern.compile(
          "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                  + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                  + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
          Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
  private static final String ENGINE_GUIDE_URL_PATTERN = "@engine.guide.url@";
  private static final String DESIGNER_GUIDE_URL_PATTERN = "@designer.guide.url@";

  public static String getEngineGuideBaseUrl() {
    return Advisor.instance().getDocBaseUrl() + "/engine-guide";
  }

  public static String getDesignerGuideBaseUrl() {
    return Advisor.instance().getDocBaseUrl() + "/designer-guide";
  }

  public static String getCockpitEngineGuideUrl() {
    return getEngineGuideBaseUrl() + "/reference/engine-cockpit/";
  }

  public static String getAppBaseUrl(String appName) {
    return ApplicationConstants.baseContextPath(appName);
  }

  public static String getApiBaseUrl(String appName) {
    return ApplicationConstants.baseContextPath(appName + "/api");
  }

  public static String replaceLinks(String text) {
    text = RegExUtils.replaceAll(text, ENGINE_GUIDE_URL_PATTERN, getEngineGuideBaseUrl());
    text = RegExUtils.replaceAll(text, DESIGNER_GUIDE_URL_PATTERN, getDesignerGuideBaseUrl());
    var matcher = URL_PATTERN.matcher(text);
    while (matcher.find()) {
      var matchStart = matcher.start(1);
      var matchEnd = matcher.end();
      var link = StringUtils.substring(text, matchStart, matchEnd);
      text = matcher.replaceAll("\n<a href='" + link + "' target='_blank'>" + link + "</a>");
    }
    return text;
  }

  public static Path getLogFile(String logFile) {
    return getLogDir().resolve(logFile);
  }

  public static Path getConfigFile(String configFile) {
    return IFileAccess.instance().getConfigFile(configFile);
  }

  public static Path getLogDir() {
    return FileUtil.getWorkingDirectory().toPath().resolve("logs");
  }
}
