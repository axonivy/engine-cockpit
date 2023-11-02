package ch.ivyteam.enginecockpit.system.ssl;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

class SecurityProviders {

  private interface Key {
    String STORE = "KeyStore";
  }

  static List<String> getTypes(String selectedProvider) {
    Provider[] providers = Security.getProviders();
    return Arrays.stream(providers)
            .filter(provider -> StringUtils.isEmpty(selectedProvider)
                    || provider.getName().equals(selectedProvider))
            .flatMap(provider -> provider.getServices().stream())
            .filter(service -> service.getType().equals(Key.STORE))
            .map(service -> service.getAlgorithm())
            .collect(Collectors.toList());
  }

  static List<String> getAlgorithms(String type) {
    Provider[] providers = Security.getProviders();
    Stream<String> algorithmsStream = Arrays.stream(providers)
            .flatMap(provider -> provider.getServices().stream())
            .filter(service -> service.getType().equals(type))
            .map(service -> service.getAlgorithm());
    return Stream.concat(algorithmsStream, Stream.of("")).collect(Collectors.toList());
  }

}