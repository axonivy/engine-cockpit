package ch.ivyteam.enginecockpit.monitor;

import java.time.Duration;
import java.util.Calendar;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;

public abstract class Monitor
{
  private boolean running;
  private long startMilliSec;
  private long lastTimestamp;
  private long pausedMilliSec;
  protected final LineChartModel model;
  private static final Duration MAX_DURATION = Duration.ofMinutes(10);
  private static final long MAX_DATA = MAX_DURATION.toSeconds();
  private final MonitorInfo info;
  
  public Monitor(MonitorInfo info)
  {
    this.info = info;
    running = true;
    model = new LineChartModel();
    model.setDatatipFormat("%2$d");
    model.setSeriesColors("607D8B,FFC107,FF5722");
    model.setExtender("skinChart");
    
    Axis timeAxis = new DateAxis("Time");
    timeAxis.setTickFormat("%H:%M:%S");
    model.getAxes().put(AxisType.X, timeAxis);
    
    if (info.yAxisLabel != null)
    {
      Axis yAxis = model.getAxis(AxisType.Y);
      yAxis.setLabel(info.yAxisLabel);
    }

    startMilliSec = Calendar.getInstance().getTimeInMillis();
    lastTimestamp = 0;
    pausedMilliSec = 0;
  }
  
  public String getTitle()
  {
    return info.title;
  }
  
  public String getIcon()
  {
    return info.icon;
  }
  
  public String getName()
  {
    return info.name;
  }
  
  protected long newTime()
  {
    return Calendar.getInstance().getTimeInMillis();
  }
  
  public LineChartModel getModel()
  {
    calcNewValues();
    return model;
  }
  
  abstract public String getInfo();
  abstract protected void calcNewValues(long time);
  
  protected void calcNewValues()
  {
    long time = newTime();
    if (lastTimestamp != 0 && time/1000 == lastTimestamp/1000)
    {
      return;
    }
    lastTimestamp = time;
    setXAxis(lastTimestamp);
    calcNewValues(time);
  }
  
  protected void setXAxis(long max)
  {
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setMax(max);
  }
  
  public boolean isRunning()
  {
    return running;
  }

  public void pauseMonitor()
  {
    pausedMilliSec = Calendar.getInstance().getTimeInMillis();
    
    running = false;
  }

  public void startMonitor()
  {
    if (pausedMilliSec == 0) 
    {
      startMilliSec = Calendar.getInstance().getTimeInMillis();
    }
    else
    {
      startMilliSec = Calendar.getInstance().getTimeInMillis() - (pausedMilliSec - startMilliSec);
    }
    running = true;
  }
  
  protected void cleanUpOldData(Map<Object, Number> data)
  {
    if (data.size() > MAX_DATA)
    {
      data.remove(data.keySet().iterator().next());
    }
    var keys = data.keySet().iterator();
    if (keys.hasNext())
    {
      Axis xAxis = model.getAxis(AxisType.X);
      xAxis.setMin(keys.next());
    }
  }
  
  public static final class MonitorInfo
  {
    private final String title;
    private final String name;
    private final String icon;
    private final String yAxisLabel;
    
    private MonitorInfo(String title, String name, String icon, String yAxisLabel)
    {
      this.title = title;
      this.name = name;
      this.icon = icon;
      this.yAxisLabel = yAxisLabel;
    }
    
    public static Builder build()
    {
      return new Builder();
    }
    
  }
  
  public static final class Builder
  {
    private String title;
    private String name;
    private String icon;
    private String yAxisLabel;
    
    public Builder title(String t)
    {
      this.title = t;
      return this;
    }
    
    public Builder name(String nm)
    {
      this.name = nm;
      return this;
    }
    
    public Builder icon(String icn)
    {
      this.icon = icn;
      return this;
    }
    
    public Builder yAxisLabel(String label)
    {
      this.yAxisLabel = label;
      return this;
    }
    
    public MonitorInfo toInfo()
    {
      if (name == null)
      {
        throw new IllegalStateException("Name must be specified");
      }
      if (icon == null)
      {
        throw new IllegalStateException("Icon must be specified");
      }
      if (title == null)
      {
        title = name;
      }
      return new MonitorInfo(title, name, icon, yAxisLabel);
    }
  }
}
