package ch.ivyteam.enginecockpit.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.io.FileUtil;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.application.restricted.ApplicationConstants;
import ch.ivyteam.ivy.config.IFileAccess;

@SuppressWarnings("restriction")
public class UrlUtil
{
  private static final Pattern urlPattern = Pattern.compile(
          "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                  + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                  + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
          Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
  private static final String ENGINE_GUIDE_URL_PATTERN = "@engine.guide.url@";
  
  public static String getEngineGuideBaseUrl()
  {
    return Advisor.get().getDocBaseUrl() + "/engine-guide";
  }
  
  public static String getDesignerGuideBaseUrl()
  {
	return Advisor.get().getDocBaseUrl() + "/designer-guide";
  }
  
  public static String getCockpitEngineGuideUrl()
  {
    return getEngineGuideBaseUrl() + "/tool-reference/engine-cockpit/";
  }
  
  public static String getApiBaseUrl()
  {
    return ApplicationConstants.baseContextPath("system/api");
  }
  
  public static String replaceLinks(String text)
  {
    text = RegExUtils.replaceAll(text, ENGINE_GUIDE_URL_PATTERN, getEngineGuideBaseUrl());
    Matcher matcher = urlPattern.matcher(text);
    while (matcher.find()) {
        int matchStart = matcher.start(1);
        int matchEnd = matcher.end();
        String link = StringUtils.substring(text, matchStart, matchEnd);
        text = matcher.replaceAll("\n<a href='" + link + "' target='_blank'>" + link + "</a>");
    }
    return text;
  }
  
  public static File getLogFile(String logFile)
  {
    return new File(getLogDir() + File.separator + logFile);
  }
  
  public static File getConfigFile(String configFile)
  {
    return IFileAccess.get().getConfigurationFile(configFile);
  }
  
  public static File getLogDir()
  {
    return new File(FileUtil.getWorkingDirectory() + File.separator + "logs");
  }
}
