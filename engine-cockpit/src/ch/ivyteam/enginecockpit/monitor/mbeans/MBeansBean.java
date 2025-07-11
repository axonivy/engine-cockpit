package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.log.Logger;

@ManagedBean(name = "mBeansBean")
@ViewScoped
public class MBeansBean {

  private final static Logger LOGGER = Logger.getLogger(MBeansBean.class);

  private final MBeanTreeNode root;
  private List<MAttribute> attributes = Collections.emptyList();
  private MName selected;
  private final MTraceMonitor monitor = new MTraceMonitor();

  public MBeansBean() {
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    Set<ObjectName> beanNames = server.queryNames(null, null);
    Set<MName> names = beanNames
        .stream()
        .map(MName::new)
        .collect(Collectors.toSet());
    root = new MBeanTreeNode(MName.ROOT, names);
  }

  public TreeNode<MName> getRoot() {
    return root;
  }

  public void addTrace(MAttribute attribute) {
    if (attribute.isComposite()) {
      attribute.getCompositeNames().forEach(name -> addTrace(attribute, name));
    } else {
      var trace = new MTrace(selected, attribute);
      monitor.addTrace(trace);
    }
  }

  private void addTrace(MAttribute attribute, String compositeName) {
    var trace = new MTrace(selected, attribute, compositeName);
    monitor.addTrace(trace);
  }

  public void removeTrace(MTrace trace) {
    monitor.removeTrace(trace);
  }

  public Monitor getTracesMonitor() {
    return monitor;
  }

  public List<MTrace> getTraces() {
    return monitor.getTraces();
  }

  public List<MAttribute> getAttributes() {
    if (selected != null) {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      try {
        MBeanInfo info = server.getMBeanInfo(selected.getObjectName());
        MBeanAttributeInfo[] attributeInfos = info.getAttributes();
        String[] attributeNames = Arrays
            .stream(attributeInfos)
            .map(MBeanAttributeInfo::getName)
            .toArray(String[]::new);
        attributes = server.getAttributes(selected.getObjectName(), attributeNames)
            .asList()
            .stream()
            .map(attr -> createAttribute(attr, attributeInfos))
            .sorted()
            .collect(Collectors.toList());
      } catch (InstanceNotFoundException | ReflectionException | IntrospectionException ex) {
        LOGGER.error("Could not get attributes of MBean " + selected.getObjectName(), ex);
      }
    }
    return attributes;
  }

  private MAttribute createAttribute(Attribute attribute, MBeanAttributeInfo[] infos) {
    MBeanAttributeInfo info = Arrays
        .stream(infos)
        .filter(inf -> Objects.equals(inf.getName(), attribute.getName()))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(
            "Attribute info for attribute " + attribute.getName() + " not found"));
    return new MAttribute(attribute, info);
  }

  public void onSelectNode(NodeSelectEvent event) {
    var node = event.getTreeNode();
    if ("bean".equals(node.getType())) {
      selected = (MName) node.getData();
    } else {
      selected = null;
    }
  }
}
