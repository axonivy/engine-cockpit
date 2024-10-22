package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.Feature;

public class RestClientAuthTypeCalculator {

  private final List<Feature> features;

  public RestClientAuthTypeCalculator(List<Feature> features) {
    this.features = features;
  }

  public String get() {
    return features.stream().filter(f -> StringUtils.contains(f.getClazz(), "authentication"))
            .map(f -> StringUtils.substringBetween(f.getClazz(), "authentication.", "AuthenticationFeature"))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
  }
}
