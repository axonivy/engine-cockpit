package ch.ivyteam.enginecockpit.application.model;

import ch.ivyteam.ivy.application.ReleaseState;

public interface AppTreeItem {

  String getName();
  String getDetailView();

  // PM stuff
  default boolean isPm() {
    return false;
  }

  default String getReleaseStateIcon() {
    return null;
  }

  default ReleaseState getReleaseState() {
    return null;
  }

  // PMV stuff
  default boolean isPmv() {
    return false;
  }

  default String getVersion() {
    return null;
  }

  default String getLastChangeDate() {
    return null;
  }
}
