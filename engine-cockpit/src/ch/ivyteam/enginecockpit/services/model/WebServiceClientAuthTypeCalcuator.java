package ch.ivyteam.enginecockpit.services.model;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.Feature;

public class WebServiceClientAuthTypeCalcuator {

  private final List<Feature> features;

  public WebServiceClientAuthTypeCalcuator(List<Feature> features) {
    this.features = features;
  }

  public String get() {
    return features.stream().filter(f -> StringUtils.contains(f.getClazz(), "AuthenticationFeature"))
            .map(f -> StringUtils.substringBetween(f.getClazz(), "cxf.feature.", "AuthenticationFeature"))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
  }
}
