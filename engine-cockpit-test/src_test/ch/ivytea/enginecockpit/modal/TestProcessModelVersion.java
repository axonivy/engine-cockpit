package ch.ivytea.enginecockpit.modal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.model.ProcessModelVersion;

public class TestProcessModelVersion
{

  @Test
  void emptyLibrary()
  {
    ProcessModelVersion pmv = new ProcessModelVersion(new FakeProcessModelVersion());
    assertThat(pmv.getQualifiedVersion()).isEqualTo("Unknown version");
  }
  
}
