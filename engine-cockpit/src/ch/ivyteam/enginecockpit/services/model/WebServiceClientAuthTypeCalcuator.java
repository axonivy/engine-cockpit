package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class WebServiceClientAuthTypeCalcuator {

  private final List<String> features;

  public WebServiceClientAuthTypeCalcuator(List<String> features) {
    this.features = features;
  }

  public String get() {
    return features.stream().filter(f -> StringUtils.contains(f, "AuthenticationFeature"))
            .map(f -> StringUtils.substringBetween(f, "cxf.feature.", "AuthenticationFeature"))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
  }
}
