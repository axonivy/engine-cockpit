package ch.ivyteam.enginecockpit.security.directory;

import java.util.List;

public interface DirectoryBrowser {
  // we can discuss this, that we only have one root node.
  List<? extends DirectoryNode> root();
  List<? extends DirectoryNode> children(DirectoryNode node);

  List<Property> getNodeAttributes(DirectoryNode node);
  
  boolean isSubNode(DirectoryNode node, String id);
}
