package ch.ivyteam.enginecockpit.security.directory;

public interface DirectoryNode {
  String getIcon();
  String getDisplayName();
  boolean isExpandable();
  String getId();
}
