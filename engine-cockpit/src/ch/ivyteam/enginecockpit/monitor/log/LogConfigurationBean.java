package ch.ivyteam.enginecockpit.monitor.log;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.log.management.LogAppender;
import ch.ivyteam.ivy.log.management.LogAppenderRoute;
import ch.ivyteam.ivy.log.management.LogConfiguration;
import ch.ivyteam.ivy.log.management.LogConfigurationService;
import ch.ivyteam.ivy.log.management.LogLogger;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named("logConfigurationBean")
@ViewScoped
public class LogConfigurationBean implements Serializable {

  private final LogConfigurationService logConfigurationService = LogConfigurationService.instance();
  private List<LoggerRow> allLoggers = List.of();
  private List<LoggerRow> loggers = List.of();
  private List<AppenderRow> appenders = List.of();
  private LogConfiguration configuration;
  private boolean showAllLoggers;

  public void refresh() {
    try {
      refreshConfiguration();
    } catch (RuntimeException ex) {
      showError(ex);
    }
  }

  private void refreshConfiguration() {
    configuration = logConfigurationService.snapshot();
    allLoggers = new ArrayList<>(configuration.loggers().stream()
        .map(LoggerRow::new)
        .sorted(Comparator.comparing(LoggerRow::isConfigured).reversed().thenComparing(LoggerRow::getName))
        .toList());
    appenders = configuration.appenders().stream().map(AppenderRow::new).toList();
    updateVisibleLoggers();
  }

  public void setLevel(LoggerRow logger) {
    try {
      logConfigurationService.setLevel(logger.getName(), logger.getSelectedLevel());
      refreshConfiguration();
      Message.info()
          .summary(Ivy.cm().co("/logs/LoggerLevelChanged"))
          .detail(Ivy.cm().content("/logs/LoggerLevelChangedDetail")
              .replace("logger", logger.getName())
              .replace("level", logger.getSelectedLevel()).get())
          .show();
    } catch (RuntimeException ex) {
      Message.error()
          .summary(Ivy.cm().co("/logs/LoggerLevelChangeFailed"))
          .detail(ex.getMessage())
          .show();
    }
  }

  public void resetLevel(LoggerRow logger) {
    try {
      logConfigurationService.resetLevel(logger.getName());
      refreshConfiguration();
      Message.info()
          .summary(Ivy.cm().co("/logs/LoggerLevelReset"))
          .detail(Ivy.cm().content("/logs/LoggerLevelResetDetail")
              .replace("logger", logger.getName()).get())
          .show();
    } catch (RuntimeException ex) {
      showError(ex);
    }
  }

  public List<LoggerRow> getLoggers() {
    return loggers;
  }

  public boolean isShowAllLoggers() {
    return showAllLoggers;
  }

  public void setShowAllLoggers(boolean showAllLoggers) {
    this.showAllLoggers = showAllLoggers;
    updateVisibleLoggers();
  }

  public List<String> getLevels() {
    return logConfigurationService.levels();
  }

  public List<AppenderRow> getAppenders() {
    return appenders;
  }

  public String getConfigurationFile() {
    if (configuration == null) {
      return "";
    }
    return configuration.configurationFile();
  }

  public String getLastLoadedAt() {
    if (configuration == null) {
      return "-";
    }
    return formatPretty(configuration.lastLoadedAt());
  }

  public String getLastLoadedAtTooltip() {
    if (configuration == null) {
      return "";
    }
    return formatExact(configuration.lastLoadedAt());
  }

  public String getConfigurationLastModified() {
    if (configuration == null) {
      return "-";
    }
    return formatPretty(configuration.configurationLastModified());
  }

  public String getConfigurationLastModifiedTooltip() {
    if (configuration == null) {
      return "";
    }
    return formatExact(configuration.configurationLastModified());
  }

  public List<String> getStatusMessages() {
    if (configuration == null) {
      return List.of();
    }
    return configuration.statusMessages();
  }

  private static void showError(RuntimeException ex) {
    Message.error()
        .summary(Ivy.cm().co("/common/Error"))
        .detail(ex.getMessage())
        .show();
  }

  private void updateVisibleLoggers() {
    if (showAllLoggers) {
      loggers = allLoggers;
      return;
    }
    loggers = new ArrayList<>(allLoggers.stream().filter(LoggerRow::isConfigured).toList());
  }

  private static String formatPretty(Instant instant) {
    if (instant == null) {
      return "-";
    }
    return new PrettyTime().format(instant);
  }

  private static String formatExact(Instant instant) {
    if (instant == null) {
      return "";
    }
    return DateUtil.formatInstantAsDateTime(instant);
  }

  public static final class LoggerRow {

    private final String name;
    private final String parent;
    private final String configuredLevel;
    private final String effectiveLevel;
    private final boolean instantiated;
    private final boolean additive;
    private final boolean explicitlyConfigured;
    private final boolean runtimeOverride;
    private final List<AppenderRouteRow> appenderRoutes;
    private String selectedLevel;

    private LoggerRow(LogLogger logger) {
      name = logger.name();
      parent = logger.parent();
      configuredLevel = logger.configuredLevel();
      effectiveLevel = logger.effectiveLevel();
      instantiated = logger.instantiated();
      additive = logger.additive();
      explicitlyConfigured = logger.explicitlyConfigured();
      runtimeOverride = logger.runtimeOverride();
      appenderRoutes = logger.appenderRoutes().stream().map(AppenderRouteRow::new).toList();
      selectedLevel = effectiveLevel;
    }

    private boolean isConfigured() {
      return explicitlyConfigured;
    }

    public String getName() {
      return name;
    }

    public String getParent() {
      return parent;
    }

    public String getConfiguredLevel() {
      return configuredLevel;
    }

    public String getEffectiveLevel() {
      return effectiveLevel;
    }

    public boolean isInstantiated() {
      return instantiated;
    }

    public boolean isAdditive() {
      return additive;
    }

    public List<AppenderRouteRow> getAppenderRoutes() {
      return appenderRoutes;
    }

    public boolean isRuntimeOverride() {
      return runtimeOverride;
    }

    public String getSelectedLevel() {
      return selectedLevel;
    }

    public void setSelectedLevel(String selectedLevel) {
      this.selectedLevel = selectedLevel;
    }
  }

  public static final class AppenderRow {

    private final String name;
    private final String type;
    private final String target;
    private final String state;

    private AppenderRow(LogAppender appender) {
      name = appender.name();
      type = appender.type();
      target = appender.target();
      state = appender.state();
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }

    public String getTarget() {
      return target;
    }

    public boolean isUnhealthy() {
      return !"STARTED".equals(state);
    }

    public String getStateWarning() {
      return Ivy.cm().content("/logs/AppenderStateWarning")
          .replace("state", state).get();
    }
  }

  public static final class AppenderRouteRow {

    private final String name;
    private final boolean inherited;
    private final String threshold;
    private final String inheritedFrom;

    private AppenderRouteRow(LogAppenderRoute route) {
      name = route.appenderName();
      inherited = route.inherited();
      threshold = route.threshold();
      inheritedFrom = route.inheritedFrom();
    }

    public String getName() {
      return name;
    }

    public boolean isInherited() {
      return inherited;
    }

    public String getThreshold() {
      return threshold;
    }

    public String getInheritedFrom() {
      return inheritedFrom;
    }
  }
}
