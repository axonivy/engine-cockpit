package ch.ivyteam.enginecockpit.monitor.memory.histogram;

import static java.lang.Long.parseLong;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.monitor.trace.BackgroundMeterUtil;

public class ClassHisto {

  private final ClassHistogramBean classHistogram;
  private final String name;
  private final String module;
  private long instances;
  private long maxInstances;
  private long minInstances;
  private long bytes;

  public ClassHisto(ClassHistogramBean classHistogram, String name, String module, long instances, long bytes) {
    this.classHistogram = classHistogram;
    this.name=name;
    this.module=module;
    this.instances = instances;
    this.maxInstances = instances;
    this.minInstances = instances;
    this.bytes = bytes;
  }

  public String getId() {
    if (module.isBlank()) {
      return name;
    }
    return name + " "+ module;
  }

  public String getName() {
    return name;
  }

  public String getModule() {
    return module;
  }

  public long getInstances() {
    return instances;
  }

  public String getInstancesBackground() {
    return BackgroundMeterUtil.background(instances, classHistogram.getMaxInstances());
  }

  public long getMaxInstances() {
    return maxInstances;
  }

  public long getMinInstances() {
    return minInstances;
  }

  public long getDeltaToMaxInstances() {
    return maxInstances - instances;
  }

  public long getDeltaToMinInstances() {
    return instances - minInstances;
  }

  public String getDeltaMinBackground() {
    return BackgroundMeterUtil.background(getDeltaToMinInstances(), classHistogram.getMaxDeltaToMinInstances());
  }

  public long getBytes() {
    return bytes;
  }

  public String getBytesBackground() {
    return BackgroundMeterUtil.background(bytes, classHistogram.getMaxBytes());
  }

  public void update(ClassHisto newClass) {
    this.instances = newClass.instances;
    this.bytes = newClass.bytes;
    if (instances > maxInstances) {
      maxInstances = instances;
    }
    if (instances < minInstances) {
      minInstances = instances;
    }
  }

  static Map<String, ClassHisto> parse(ClassHistogramBean classHistogram, String dump) {
    return dump
            .lines()
            .dropWhile(line -> line.startsWith(" num") || line.startsWith("---"))
            .takeWhile(line -> line.contains(":"))
            .map(line -> toClassInfo(classHistogram, line))
            .collect(Collectors.<ClassHisto, String, ClassHisto>toMap(ClassHisto::getId, info -> info, (info1, info2) -> info1.merge(info2)));
  }

  private static ClassHisto toClassInfo(ClassHistogramBean classHistogram, String line) {
    line = line.trim();
    var columns = line.split("\\s+");
    var module = "";
    if (columns.length > 4) {
      module = toUserFriendlyModule(columns[4]);
    }
    return new ClassHisto(classHistogram, toUserFriendlyName(columns[3]), module, parseLong(columns[1]), parseLong(columns[2]));
  }

  private static String toUserFriendlyName(String name) {
    if (name.startsWith("[")) {
      if (name.startsWith("[L")) {
        return toUserFriendlyName(name.substring(2, name.length()-1)) + "[]";
      }
      return toUserFriendlyName(name.substring(1)) + "[]";
    } else if (name.length() == 1) {
      return switch(name) {
        case "B" -> "byte";
        case "C" -> "char";
        case "S" -> "short";
        case "I" -> "int";
        case "J" -> "long";
        case "F" -> "float";
        case "D" -> "double";
        case "Z" -> "boolean";
        default -> name;
      };
    }
    return name.replace('$', '.');
  }

  private static String toUserFriendlyModule(String module) {
    module = StringUtils.removeEnd(module, ")");
    module = StringUtils.removeStart(module, "(");
    return module;
  }

  private ClassHisto merge(ClassHisto other) {
    return new ClassHisto(
        this.classHistogram,
        this.name,
        this.module,
        this.instances + other.instances,
        this.bytes + other.bytes);
  }
}
