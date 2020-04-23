package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.Set;
import java.util.stream.Collectors;

import org.primefaces.model.DefaultTreeNode;

public class MBeanTreeNode extends DefaultTreeNode implements Comparable<MBeanTreeNode>
{
  MBeanTreeNode(MName name, Set<MName> allNames)
  {
    setData(name);
    setChildren(name.getDirectChildren(allNames)
        .stream()
        .map(child -> new MBeanTreeNode(child, allNames))
        .sorted()
        .collect(Collectors.toList()));
    setType(this.isLeaf()?"bean":"folder");
    setSelectable(this.isLeaf());
  }
  
  @Override
  public void setSelected(boolean value)
  {
    super.setSelected(value);
  }

  @Override
  public int compareTo(MBeanTreeNode other)
  {
    MName name1 = (MName) getData();
    MName name2 = (MName) other.getData();
    return name1.getDisplayName().compareTo(name2.getDisplayName());
  }
}


