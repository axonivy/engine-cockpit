package ch.ivyteam.enginecockpit.configuration.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.ivyteam.ivy.configuration.restricted.FileConfigSource;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

class TestConfigViewImpl {

  @Test
  void isOverridden(@TempDir Path dir) throws IOException {
    var ivyYaml = Files.writeString(dir.resolve("ivy.yaml"), "Overridden: user\nUserOnly: user\n");
    var infraYaml = Files.writeString(dir.resolve("ivy-infra.yaml"), "Overridden: infra\n");
    var configuration = IConfiguration.create()
        .configSource(FileConfigSource.create().file(ivyYaml))
        .configSource(FileConfigSource.create().file(infraYaml).readOnly())
        .toConfiguration();

    var view = new ConfigViewImpl(configuration, c -> c, List.of());
    assertThat(view.isOverridden("Overridden")).isTrue();
    assertThat(view.isOverridden("UserOnly")).isFalse();
    assertThat(view.isOverridden("Unknown")).isFalse();

    configuration.set("Overridden", "infra");
    view = new ConfigViewImpl(configuration, c -> c, List.of());
    assertThat(Files.readString(ivyYaml)).contains("Overridden: infra");
    assertThat(view.isOverridden("Overridden")).isTrue();
  }
}
