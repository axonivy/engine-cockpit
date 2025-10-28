package ch.ivyteam.enginecockpit.application.model;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.application.ReleaseState;

public interface AppTreeItem {

  String getName();
  String getDetailView();
  List<String> isDeletable();

  default String getNotDeletableMessage() {
    return isDeletable().stream().collect(Collectors.joining("\n"));
  }

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

  default boolean isNotStartable() {
    return true;
  }

  default boolean isNotStopable() {
    return true;
  }

  default boolean isNotLockable() {
    return true;
  }

  default boolean isReleasable() {
    return false;
  }

  default void release() {}

  default void activate() {}

  default void deactivate() {}

  default void lock() {}

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
