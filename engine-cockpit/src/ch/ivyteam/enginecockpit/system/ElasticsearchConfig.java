package ch.ivyteam.enginecockpit.system;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.io.FileUtil;
import ch.ivyteam.ivy.elasticsearch.IElasticsearchManager;

public class ElasticsearchConfig {
  public final static String ELASTICSEARCH = "elasticsearch";

  public static Path getLogDir() {
    var elasticsearchDir = Path.of(FileUtil.getWorkingDirectory().getAbsolutePath(), ELASTICSEARCH);
    if (elasticsearchDir.toFile().exists()) {
      return elasticsearchDir.resolve("logs");
    }
    return null;
  }

  public static Path getLogFile(String logFile) {
    logFile = StringUtils.substringAfter(logFile, ElasticsearchConfig.ELASTICSEARCH);
    var esClusterName = IElasticsearchManager.instance().info().clusterName();
    return getLogDir().resolve(esClusterName + logFile);
  }

}
