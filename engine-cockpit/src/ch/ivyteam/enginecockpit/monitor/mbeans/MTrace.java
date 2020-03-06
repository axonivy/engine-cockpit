package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

public class MTrace
{
  private final MName name;
  private final MAttribute attribute;
  private final String compositeName;
  private final LineChartSeries series;
  private final Map<Object, Number> data = new LinkedHashMap<>();
  private Number lastValue;

  public MTrace(MName name, MAttribute attribute)
  {
    this(name, attribute, null);
  }
  
  public MTrace(MName name, MAttribute attribute, String compositeName)
  {
    this.name = name;
    this.attribute = attribute;
    this.compositeName = compositeName;
    series = new LineChartSeries();
    series.setSmoothLine(true);
    series.setData(data);    
    series.setLabel(getAttributeName() +" ("+name.getDisplayName()+")");
  }

  public String getAttributeName()
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

  public ChartSeries getSeries()
  {
    return series;
  }

  public void calcNewValue(long actualSec)
  {
    data.put(actualSec, evaluateLastValue());
  }

  private Number evaluateLastValue()
  {
    try
    {
      var server = ManagementFactory.getPlatformMBeanServer();
      var value = server.getAttribute(name.getObjectName(), attribute.getName());
      if (value instanceof CompositeData)
      {
        value = ((CompositeData)value).get(compositeName);
      }
      lastValue = (Number)value;
      return lastValue;
    }
    catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex)
    {
      return -1;
    }
  }

  public Map<Object, Number> getData()
  {
    return data;
  }
}
