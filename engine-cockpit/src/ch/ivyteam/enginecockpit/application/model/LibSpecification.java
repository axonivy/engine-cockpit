package ch.ivyteam.enginecockpit.application.model;

import ch.ivyteam.ivy.application.ILibrarySpecification;
import ch.ivyteam.ivy.application.value.VersionRange;
import ch.ivyteam.ivy.environment.Ivy;

public class LibSpecification {

  private final String id;
  private final boolean resolved;
  private final String version;
  private ProcessModelVersion resolvedPmv;
  private String resolvedPmvName;

  public LibSpecification(ILibrarySpecification spec) {
    id = spec.getId();
    resolved = spec.isResolved();
    version = VersionRange.of(spec).toString();
    var resolvedLib = spec.getResolvedLibrary();
    if (resolvedLib != null) {
      resolvedPmv = new ProcessModelVersion(resolvedLib.getProcessModelVersion());
      resolvedPmvName = resolvedPmv.getName() + " (" + resolvedPmv.getQualifiedVersion() + " / " +
          resolvedPmv.getState().getOperation() + " / " + resolvedPmv.getState().getReleaseState() + ")";
    }
  }

  public String getId() {
    return id;
  }

  public boolean isResolved() {
    return resolved;
  }

  public String getVersion() {
    return version;
  }

  public String getResolvedPmvName() {
    return resolvedPmvName;
  }

  public ProcessModelVersion getResolvedPmv() {
    return resolvedPmv;
  }

  public String getResolvedTooltip() {
    if (isResolved()) {
      return Ivy.cm().content("/pmvDetail/PMVResolvedForSpecificationMessage").replace("name", getResolvedPmvName())
          .get();
    }
    return Ivy.cm().co("/pmvDetail/NoProjectFoundForSpecificationMessage");
  }

}
