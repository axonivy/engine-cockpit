package ch.ivyteam.enginecockpit.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.ivy.application.restricted.ProcessModelVersionAdapter;

@SuppressWarnings("restriction")
public class TestProcessModelVersion
{

  /** ISSUE XIVY-4159 Application overview not displayed due to NPE */
  @Test
  void emptyLibrary()
  {
    ProcessModelVersion pmv = new ProcessModelVersion(new ProcessModelVersionAdapter());
    assertThat(pmv.getQualifiedVersion()).isEqualTo("Unknown version");
    assertThat(pmv.getLibraryId()).isEqualTo("Unknown id");
  }
  
}
