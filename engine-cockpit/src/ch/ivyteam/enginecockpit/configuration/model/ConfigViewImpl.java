package ch.ivyteam.enginecockpit.configuration.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.TableFilter;
import ch.ivyteam.ivy.configuration.internal.Configuration;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;

public class ConfigViewImpl implements TableFilter, ConfigView {
  private static final String DEFINED_FILTER = "defined";
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private String filter;
  private ConfigProperty activeConfig;
  private final IConfiguration configuration;
  private List<String> selectedContentFilters;
  private final UnaryOperator<ConfigProperty> propertyEnricher;
  private final List<ContentFilter<ConfigProperty>> contentFilters;

  public ConfigViewImpl(List<ContentFilter<ConfigProperty>> contentFilters) {
    this(IConfiguration.instance(), c -> c, contentFilters);
  }

  public ConfigViewImpl(IConfiguration configuration,
      UnaryOperator<ConfigProperty> propertyEnricher,
      List<ContentFilter<ConfigProperty>> contentFilters) {
    this.configuration = configuration;
    this.propertyEnricher = propertyEnricher;
    this.contentFilters = contentFilters;
    reloadConfigs();
  }

  private void reloadConfigs() {
    ((Configuration) configuration).blockUntilReloaded();
    configs = configuration.getProperties().stream()
        .filter(property -> !"SecuritySystem".equals(property.key()))
        .map(ConfigProperty::new)
        .map(propertyEnricher)
        .collect(Collectors.toList());
  }

  @Override
  public List<ConfigProperty> getConfigs() {
    return filter(configs);
  }

  private List<ConfigProperty> filter(List<ConfigProperty> properties) {
    if (properties == null) {
      return properties;
    }
    var props = properties.stream();
    for (var contentFilter : contentFilters) {
      if (contentFilter.isActive()) {
        props = props.filter(contentFilter.filter());
      }
    }
    return props.collect(Collectors.toList());
  }

  @Override
  public List<ConfigProperty> getFilteredConfigs() {
    if (StringUtils.isNotBlank(filter)) {
      return filter(filteredConfigs);
    }
    return filteredConfigs;
  }

  @Override
  public void setFilteredConfigs(List<ConfigProperty> filteredConfigs) {
    this.filteredConfigs = filteredConfigs;
  }

  @Override
  public String getFilter() {
    return filter;
  }

  @Override
  public void setFilter(String filter) {
    this.filter = filter;
  }

  @Override
  public List<SelectItem> getContentFilters() {
    return contentFilters.stream().map(ContentFilter::selectItem).collect(Collectors.toList());
  }

  @Override
  public List<String> getSelectedContentFilters() {
    return selectedContentFilters;
  }

  @Override
  public void setSelectedContentFilters(List<String> selectedContentFilters) {
    this.selectedContentFilters = selectedContentFilters;
    contentFilters.forEach(contentFilter -> {
      contentFilter.enabled(selectedContentFilters.contains(contentFilter.name()));
    });
    triggerTableFilter();
  }

  @Override
  public void resetSelectedContentFilters() {
    setSelectedContentFilters(Collections.emptyList());
    triggerTableFilter();
  }

  @Override
  public String getContentFilterText() {
    var text = contentFilters.stream()
        .filter(ContentFilter::enabled)
        .map(ContentFilter::name)
        .collect(Collectors.joining(", "));
    if (StringUtils.isBlank(text)) {
      return "none";
    }
    return text;
  }

  @Override
  public void setActiveConfig(String configKey) {
    this.activeConfig = configs.stream().filter(c -> Objects.equals(c.getKey(), configKey)).findFirst()
        .orElse(new ConfigProperty());
  }

  @Override
  public ConfigProperty getActiveConfig() {
    return activeConfig;
  }

  @Override
  public void resetConfig() {
    configuration.remove(activeConfig.getKey());
    if (filteredConfigs != null) {
      filteredConfigs.remove(activeConfig);
    }
    reloadAndUiMessage("/configuration/ConfigReset", "/configuration/ConfigResetOverridden");
  }

  @Override
  public void saveConfig() {
    configuration.set(activeConfig.getKey(), activeConfig.getValue());
    reloadAndUiMessage("/configuration/ConfigSaved", "/configuration/ConfigSavedOverridden");
  }

  public String getActiveConfigOverriddenMessage() {
    if (activeConfig == null) {
      return null;
    }
    return overriddenMessage("/configuration/EditConfigurationOverriddenMessage", activeConfig.getKey());
  }

  private void reloadAndUiMessage(String messageUri, String overriddenMessageUri) {
    reloadConfigs();
    var key = activeConfig.getKey();
    var message = new FacesMessage(Ivy.cm().content(messageUri).replace("config", key).get());
    var overridden = overriddenMessage(overriddenMessageUri, key);
    if (overridden != null) {
      message = new FacesMessage(FacesMessage.SEVERITY_WARN, overridden, null);
    }
    FacesContext.getCurrentInstance().addMessage("msgs", message);
    triggerTableFilter();
  }

  private String overriddenMessage(String messageUri, String key) {
    if (!isOverridden(key)) {
      return null;
    }
    return Ivy.cm().content(messageUri)
        .replace("config", key)
        .replace("target", writeTarget(key).getFileName().toString())
        .replace("source", config(key).getShortSource())
        .get();
  }

  public boolean isOverridden(String key) {
    var config = config(key);
    if (config == null || config.isDefault()) {
      return false;
    }
    var target = writeTarget(key);
    return target != null && !target.equals(config.getFile());
  }

  private ConfigProperty config(String key) {
    return configs.stream().filter(c -> Objects.equals(c.getKey(), key)).findFirst().orElse(null);
  }

  private Path writeTarget(String key) {
    try {
      return configuration.topSource(key).file().toAbsolutePath();
    } catch (NoSuchElementException _) {
      return null;
    }
  }

  public static ContentFilter<ConfigProperty> defaultFilter() {
    return new ContentFilter<>(DEFINED_FILTER, Ivy.cm().co("/configuration/ShowOnlyDefinedValuesMessage"), c -> !c.isDefault());
  }
}
