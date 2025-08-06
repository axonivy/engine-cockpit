package ch.ivyteam.enginecockpit.configuration.model;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.configuration.restricted.Property;
import ch.ivyteam.ivy.vars.Variable;

@SuppressWarnings("restriction")
public class ConfigProperty {
  private String key;
  private String value;
  private String defaultValue;
  private boolean isDefault;
  private String source;
  private boolean password;
  private ConfigValueFormat configValueFormat;
  private List<String> enumerationValues;
  private boolean restartRequired;
  private String description;
  private Path file;
  private String fileExtension;
  private Supplier<List<String>> enumerationValuesSupplier;

  public ConfigProperty() {
    configValueFormat = ConfigValueFormat.STRING;
  }

  public ConfigProperty(Property property) {
    this.key = property.getKey();
    this.value = property.getValue();
    this.isDefault = property.isDefault();
    this.source = property.getSource();

    var metaData = property.getMetaData();
    this.defaultValue = metaData.defaultValue();
    this.password = metaData.isPassword();
    this.configValueFormat = metaData.format();
    this.enumerationValues = metaData.enumerationValues();
    this.restartRequired = metaData.isRestartRequired();
    this.description = metaData.description();
    this.fileExtension = metaData.fileExtension();
    this.file = getFile(source);
    correctValuesIfDaytimeFormat();
  }

  public ConfigProperty(Variable variable) {
    this.key = variable.name();
    this.value = variable.value();
    this.defaultValue = variable.defaultValue();
    this.isDefault = variable.isDefault();
    this.source = variable.source();
    this.configValueFormat = ConfigValueFormat.of(variable.type());
    this.password = configValueFormat == ConfigValueFormat.PASSWORD;
    this.enumerationValues = variable.enumerationValues();
    this.restartRequired = false;
    this.description = variable.description();
    this.fileExtension = "";
    this.file = getFile(source);
    correctValuesIfDaytimeFormat();
  }

  public String getEditValue() {
    if (configValueFormat == ConfigValueFormat.EXPRESSION) {
      return defaultValue; // scripted value rather than resolved
    }
    return value;
  }

  public void setEditValue(String edited) {
    setValue(edited);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getShortSource() {
    return StringUtils.substring(source, StringUtils.lastIndexOf(source, '/') + 1,
        getIndexOfSourceSuffix(source));
  }

  public boolean isPassword() {
    return password;
  }

  public String getIcon() {
    if (isPassword()) {
      return "password-lock-2";
    }
    if (configValueFormat == ConfigValueFormat.FILE) {
      return "common-file-text";
    }
    if (configValueFormat == ConfigValueFormat.EXPRESSION) {
      return "archive-folder";
    }
    return "cog";
  }

  public String getConfigValueFormat() {
    return configValueFormat.name();
  }

  public void setConfigValueFormat(ConfigValueFormat format) {
    this.configValueFormat = format;
  }

  public List<String> getEnumerationValues() {
    if (enumerationValuesSupplier != null) {
      enumerationValues = enumerationValuesSupplier.get();
      enumerationValuesSupplier = null;
    }
    return enumerationValues;
  }

  public void setEnumerationValues(Supplier<List<String>> enumerationValuesSupplier) {
    this.enumerationValuesSupplier = enumerationValuesSupplier;
  }

  public boolean isRestartRequired() {
    return restartRequired;
  }

  public String getDescription() {
    return description;
  }

  public String getHtmlDescription() {
    return "<pre>" + UrlUtil.replaceLinks(description) + "</pre>";
  }

  public boolean hasDescription() {
    return StringUtils.isNotBlank(description);
  }

  public String getEditorMode() {
    return Objects.equals(fileExtension, "json") ? "javascript" : "";
  }

  public boolean fileExist() {
    return file != null;
  }

  public String getFileContent() {
    if (!fileExist()) {
      return "";
    }
    try {
      return Files.readString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return "Could not read file (" + e.getMessage() + ")";
    }
  }

  public StreamedContent downloadFile() {
    try {
      var newInputStream = Files.newInputStream(file);
      return DefaultStreamedContent
          .builder()
          .stream(() -> newInputStream)
          .contentType("application/x-yaml")
          .name(file.getFileName().toString())
          .build();
    } catch (IOException e) {
      FacesContext.getCurrentInstance().addMessage("msgs",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load file: " + source));
      return null;
    }
  }

  private static Path getFile(String source) {
    try {
      return Path.of(new URI(StringUtils.substring(source, 0, getIndexOfSourceSuffix(source))));
    } catch (Exception ex) {
      return null;
    }
  }

  private static int getIndexOfSourceSuffix(String source) {
    int prefixString = StringUtils.lastIndexOf(source, ',');
    if (prefixString == -1) {
      prefixString = StringUtils.length(source);
    }
    return prefixString;
  }

  private void correctValuesIfDaytimeFormat() {
    if (Objects.equals(configValueFormat, ConfigValueFormat.DAYTIME)) {
      if ("0".equals(defaultValue)) {
        this.defaultValue = "00:00";
      }
      if ("0".equals(value)) {
        this.value = "00:00";
      }
    }
  }
}
