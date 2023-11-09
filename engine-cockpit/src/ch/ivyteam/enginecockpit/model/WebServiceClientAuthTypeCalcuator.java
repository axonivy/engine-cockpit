package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.util.Property;

public class WebServiceClientAuthTypeCalcuator {

  private final List<String> features;
  private final List<Property> properties;

  public WebServiceClientAuthTypeCalcuator(List<String> features, List<Property> properties) {
    this.features = features;
    this.properties = properties;
  }

  public String get() {
    Optional<String> first = features.stream().filter(f -> StringUtils.contains(f, "AuthenticationFeature"))
            .map(f -> StringUtils.substringBetween(f, "cxf.feature.", "AuthenticationFeature"))
            .filter(Objects::nonNull)
            .findFirst();
    if (first.isPresent()) {
      return first.get();
    }
    return properties.stream()
    		.filter(p -> "authType".equals(p.getName()))
    		.map(p -> p.getValue())
    		.findFirst()
    		.orElse("");
  }
}
