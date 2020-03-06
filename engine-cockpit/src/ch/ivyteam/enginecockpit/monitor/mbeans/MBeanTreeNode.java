package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.Set;
import java.util.stream.Collectors;

import org.primefaces.model.DefaultTreeNode;

import ch.ivyteam.ivy.environment.Ivy;

public class MBeanTreeNode extends DefaultTreeNode
{
  MBeanTreeNode(MName name, Set<MName> allNames)
  {
    setData(name);
    setChildren(name.getDirectChildren(allNames)
        .stream()
        .map(child -> new MBeanTreeNode(child, allNames))
        .collect(Collectors.toList()));
    setType(this.isLeaf()?"bean":"folder");
    setSelectable(this.isLeaf());
  }
  
  @Override
  public void setSelected(boolean value)
  {
    Ivy.log().info("selected "+ this);
    super.setSelected(value);
  }
}


