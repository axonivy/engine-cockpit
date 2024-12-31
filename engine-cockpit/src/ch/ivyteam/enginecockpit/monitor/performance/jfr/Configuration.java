package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import java.nio.charset.StandardCharsets;

import javax.management.openmbean.CompositeData;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.file.UploadedFile;

import ch.ivyteam.ivy.scripting.objects.Xml;

public record Configuration(String name, String label, String description, String provider, String contents) {

  static Configuration from(CompositeData data) {
    return new Configuration(
        (String) data.get("name"),
        (String) data.get("label"),
        (String) data.get("description"),
        (String) data.get("provider"),
        null);
  }

  static Configuration from(UploadedFile file) throws Exception {
    try (var is = file.getInputStream()) {
      var contents = IOUtils.toString(is, StandardCharsets.UTF_8);
      Xml xml = new Xml(contents);
      return new Configuration(
          file.getFileName(),
          xml.getString("/configuration/@label"),
          xml.getString("/configuration/@description"),
          xml.getString("/configuration/@provider"),
          contents);
    }
  }

  public String getName() {
    return name();
  }

  public String getLabel() {
    return label();
  }

  public String getDescription() {
    return description();
  }
}
