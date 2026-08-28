package ch.ivyteam.enginecockpit.monitor.log;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.log.admin.LogAppender;
import ch.ivyteam.ivy.log.admin.LogConfiguration;
import ch.ivyteam.ivy.log.admin.LogConfigurationAdmin;
import ch.ivyteam.ivy.log.admin.LogAppenderRoute;
import ch.ivyteam.ivy.log.admin.LogLogger;

@Named("logConfigurationBean")
@ViewScoped
public class LogConfigurationBean implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private transient LogConfigurationAdmin admin;
  private List<LoggerRow> allLoggers = List.of();
  private List<LoggerRow> loggers = List.of();
  private List<AppenderRow> appenders = List.of();
  private LogConfiguration configuration;
  private boolean showAllLoggers;

  public void onload() {
    refresh();
  }

  public void refresh() {
    try {
      refreshConfiguration();
    } catch (RuntimeException ex) {
      showError(ex);
    }
  }

  private void refreshConfiguration() {
    var nextConfiguration = admin().snapshot();
    var nextLoggers = new ArrayList<LoggerRow>(nextConfiguration.loggers().stream()
        .map(LoggerRow::new)
        .sorted(Comparator.comparing(LoggerRow::isConfigured).reversed().thenComparing(LoggerRow::getName))
        .toList());
    var nextAppenders = nextConfiguration.appenders().stream().map(AppenderRow::new).toList();

    configuration = nextConfiguration;
    allLoggers = nextLoggers;
    appenders = nextAppenders;
    updateVisibleLoggers();
  }

  public void setLevel(LoggerRow logger) {
    try {
      admin().setLevel(logger.getName(), logger.getSelectedLevel());
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
      admin().resetLevel(logger.getName());
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
    return admin().levels();
  }

  public List<AppenderRow> getAppenders() {
    return appenders;
  }

  public String getConfigurationFile() {
    return configuration == null ? "" : configuration.configurationFile();
  }

  public String getLastLoadedAt() {
    return format(configuration == null ? null : configuration.lastLoadedAt());
  }

  public String getConfigurationLastModified() {
    return format(configuration == null ? null : configuration.configurationLastModified());
  }

  public List<String> getStatusMessages() {
    return configuration == null ? List.of() : configuration.statusMessages();
  }

  private LogConfigurationAdmin admin() {
    if (admin == null) {
      admin = new LogConfigurationAdmin();
    }
    return admin;
  }

  private static void showError(RuntimeException ex) {
    Message.error()
        .summary(Ivy.cm().co("/common/Error"))
        .detail(ex.getMessage())
        .show();
  }

  private void updateVisibleLoggers() {
    loggers = showAllLoggers
        ? allLoggers
        : new ArrayList<>(allLoggers.stream().filter(LoggerRow::isConfigured).toList());
  }

  private static String format(Instant instant) {
    return instant == null ? "-" : DATE_TIME_FORMATTER.withZone(ZoneId.systemDefault()).format(instant);
  }


  public static final class LoggerRow implements Serializable {

    private static final long serialVersionUID = 1L;

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

  public static final class AppenderRow implements Serializable {

    private static final long serialVersionUID = 1L;

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


  public static final class AppenderRouteRow implements Serializable {

    private static final long serialVersionUID = 1L;

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
