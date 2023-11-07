package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class RestClientAuthTypeCalculator {

  private final List<String> features;

  public RestClientAuthTypeCalculator(List<String> features) {
    this.features = features;
  }

  public String get() {
    return features.stream().filter(f -> StringUtils.contains(f, "authentication"))
            .map(f -> StringUtils.substringBetween(f, "authentication.", "AuthenticationFeature"))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
  }
}
