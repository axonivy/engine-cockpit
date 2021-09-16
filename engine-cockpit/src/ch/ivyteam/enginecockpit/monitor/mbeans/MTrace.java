package ch.ivyteam.enginecockpit.monitor.mbeans;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.Value;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

public class MTrace extends Series {
  private final MName name;
  private final MAttribute attribute;
  private final String compositeName;
  private Value lastValue = Value.NO_VALUE;

  public MTrace(MName name, MAttribute attribute) {
    this(name, attribute, null);
  }

  public MTrace(MName name, MAttribute attribute, String compositeName) {
    super(build(createValueProvider(name, attribute, compositeName),
            createLabel(name, attribute, compositeName)));
    this.name = name;
    this.attribute = attribute;
    this.compositeName = compositeName;
  }

  private static ValueProvider createValueProvider(MName name, MAttribute attribute, String compositeName) {
    var provider = ValueProvider.attribute(name, attribute, Unit.ONE);
    if (compositeName != null) {
      provider = ValueProvider.composite(provider, compositeName, Unit.ONE);
    }
    return provider;
  }

  private static String createLabel(MName name, MAttribute attribute, String compositeName) {
    return getAttributeName(attribute, compositeName) + " (" + name.getDisplayName() + ")";
  }

  public String getAttributeName() {
    return getAttributeName(attribute, compositeName);
  }

  private static String getAttributeName(MAttribute attribute, String compositeName) {
    String attributeName = attribute.getName();
    if (StringUtils.isNotBlank(compositeName)) {
      attributeName += "." + compositeName;
    }
    return attributeName;
  }

  public String getObjectName() {
    return name.getFullDisplayName();
  }

  public String getDescription() {
    return attribute.getTooltip();
  }

  public Number getLastValue() {
    return lastValue.numberValue();
  }

  @Override
  protected Value nextValue() {
    lastValue = super.nextValue();
    return lastValue;
  }
}
