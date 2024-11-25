package ch.ivyteam.enginecockpit.configuration.model;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.TableFilter;
import ch.ivyteam.ivy.configuration.internal.Configuration;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class ConfigViewImpl implements TableFilter, ConfigView {
  private static final String DEFINED_FILTER = "defined";
  private List<ConfigProperty> configs;
  private List<ConfigProperty> filteredConfigs;
  private String filter;
  private ConfigProperty activeConfig;
  private IConfiguration configuration;
  private List<String> selectedContentFilters;
  private Function<ConfigProperty, ConfigProperty> propertyEnricher;
  private List<ContentFilter<ConfigProperty>> contentFilters;

  public ConfigViewImpl(List<ContentFilter<ConfigProperty>> contentFilters) {
    this(IConfiguration.instance(), c -> c, contentFilters);
  }

  public ConfigViewImpl(IConfiguration configuration,
          Function<ConfigProperty, ConfigProperty> propertyEnricher,
          List<ContentFilter<ConfigProperty>> contentFilters) {
    this.configuration = configuration;
    this.propertyEnricher = propertyEnricher;
    this.contentFilters = contentFilters;
    reloadConfigs();
  }

  @SuppressWarnings("deprecation")
  private void reloadConfigs() {
    ((Configuration) configuration).blockUntilReloaded();
    configs = configuration.getProperties().stream()
            .filter(property -> !property.key().equals("SecuritySystem"))
            .map(property -> new ConfigProperty(property))
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
    this.activeConfig = configs.stream().filter(c -> StringUtils.equals(c.getKey(), configKey)).findFirst()
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
    reloadAndUiMessage("reset");
  }

  @Override
  public void saveConfig() {
    configuration.set(activeConfig.getKey(), activeConfig.getValue());
    reloadAndUiMessage("saved");
  }

  private void reloadAndUiMessage(String message) {
    reloadConfigs();
    FacesContext.getCurrentInstance().addMessage("msgs",
            new FacesMessage("'" + activeConfig.getKey() + "' " + message));
    triggerTableFilter();
  }

  public static ContentFilter<ConfigProperty> defaultFilter() {
    return new ContentFilter<ConfigProperty>(DEFINED_FILTER, "Show only defined values", c -> !c.isDefault());
  }
}
