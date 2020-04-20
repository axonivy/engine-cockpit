package ch.ivyteam.enginecockpit.monitor;

import java.util.Calendar;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;

public abstract class Monitor
{
  protected boolean running;
  protected long startMilliSec;
  protected long actualSec;
  protected long pausedMilliSec;
  protected final LineChartModel model;
  protected static final int MAX_TIME_VIEW = 600;

  public Monitor()
  {
    running = true;
    model = new LineChartModel();
    model.setDatatipFormat("%2$d");
    model.setSeriesColors("607D8B,FFC107,FF5722");
    model.setExtender("skinChart");
    startMilliSec = Calendar.getInstance().getTimeInMillis();
    actualSec = 0;
    pausedMilliSec = 0;
  }
  
  protected long newTime()
  {
    return (Calendar.getInstance().getTimeInMillis() - startMilliSec) / 1000;
  }
  
  public LineChartModel getModel()
  {
    calcNewValues();
    return model;
  }
  
  abstract public String getInfo();
  
  abstract protected void calcNewValues();
  
  protected void setXAxis(long max)
  {
    long min = max - MAX_TIME_VIEW;
    Axis xAxis = model.getAxis(AxisType.X);
    xAxis.setMax(max);
    xAxis.setMin(min);
    if (min < 0)
    {
      xAxis.setMin(0);
    }
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
    if (data.size() > MAX_TIME_VIEW)
    {
      data.remove(data.keySet().iterator().next());
    }
  }
}
