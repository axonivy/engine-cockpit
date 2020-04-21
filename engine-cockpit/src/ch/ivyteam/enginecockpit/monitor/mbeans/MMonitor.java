package ch.ivyteam.enginecockpit.monitor.mbeans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

public class MMonitor extends Monitor
{
  private final List<MSeries> series = new ArrayList<>();
  private final List<MValueProvider> infoValues = new ArrayList<>();
  
  protected MMonitor(MonitorInfo info)
  {
    super(info);
    initMonitor();
  }

  private void initMonitor()
  {
    model.setLegendPosition("ne");
  }
  
  @Override
  public String getInfo()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(getName());
    if (!infoValues.isEmpty())
    {
      builder.append(": ");
      builder.append(infoValues
          .stream()
          .map(MValueProvider::nextValue)
          .map(Object::toString)
          .collect(Collectors.joining(", ")));
    }
    return builder.toString();
  }

  @Override
  protected void calcNewValues(long time)
  {
    series.forEach(serie -> serie.calcNewValue(time));
    series.forEach(serie -> cleanUpOldData(serie.getData()));
  }

  public void addSeries(MSeries mSeries)
  {    
    series.add(mSeries);
    model.addSeries(mSeries.getSeries());
    if (series.size() == 1)
    {
      startMonitor();
    }
  }

  public void removeSeries(MSeries mSeries)
  {
    series.remove(mSeries);
    model.getSeries().remove(mSeries.getSeries());
  }
  
  public void addInfoValue(MValueProvider valueProvider)
  {
    infoValues.add(valueProvider);
  }

  public List<MSeries> getSeries()
  {
    return series;
  }
  
  @Override
  public boolean isRunning()
  {
    return !series.isEmpty();
  }
  
  public static MBuilder build()
  {
    return new MBuilder();
  }
  
  public static final class MBuilder
  {
    private final Builder builder = MonitorInfo.build();
    
    public MBuilder title(String t)
    {
      builder.title(t);
      return this;
    }
    
    public MBuilder name(String nm)
    {
      builder.name(nm);
      return this;
    }
    
    public MBuilder icon(String icn)
    {
      builder.icon(icn);
      return this;
    }
    
    public MBuilder yAxisLabel(String label)
    {
      builder.yAxisLabel(label);
      return this;
    }
    
    public MMonitor toMonitor()
    {
      return new MMonitor(builder.toInfo());
    }
  }

}
