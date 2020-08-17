package ch.ivyteam.enginecockpit.monitor.mbeans;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

public class MTrace extends MSeries
{
  private final MName name;
  private final MAttribute attribute;
  private final String compositeName;
  private Number lastValue;

  public MTrace(MName name, MAttribute attribute)
  {
    this(name, attribute, null);
  }
  
  public MTrace(MName name, MAttribute attribute, String compositeName)
  {
    super(createValueProvider(name, attribute, compositeName), createLabel(name, attribute, compositeName));
    this.name = name;
    this.attribute = attribute;
    this.compositeName = compositeName;
  }

  private static MValueProvider createValueProvider(MName name, MAttribute attribute, String compositeName)
  {
    var provider = MValueProvider.attribute(name, attribute);
    if (compositeName != null)
    {
      provider = MValueProvider.composite(provider, compositeName);
    }
    return provider;
  }

  private static String createLabel(MName name, MAttribute attribute, String compositeName)
  {
    return getAttributeName(attribute, compositeName) +" ("+name.getDisplayName()+")";
  }

  public String getAttributeName()
  {
    return getAttributeName(attribute, compositeName);
  }
  
  private static String getAttributeName(MAttribute attribute, String compositeName)
  {
    String attributeName = attribute.getName();
    if (StringUtils.isNotBlank(compositeName))
    {
      attributeName+= "."+compositeName;
    }
    return attributeName;
  }

  public String getObjectName()
  {
    return name.getFullDisplayName();
  }
  
  public String getDescription()
  {
    return attribute.getTooltip();
  }
  
  public Number getLastValue()
  {
    return lastValue;
  }

  @Override
  protected Number nextValue()
  {
    lastValue = super.nextValue();
    return lastValue;
  }
}
