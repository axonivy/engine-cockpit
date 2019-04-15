package ch.ivyteam.enginecockpit.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Level;

import ch.ivyteam.ivy.security.synch.SynchronizationListener;
import ch.ivyteam.ivy.security.synch.UpdateEvent;

public class SynchronizationLogger implements SynchronizationListener
{

  private List<String> synchronizationLogMessages = new ArrayList<>();
  private boolean newLogAwailable = false;

  public List<String> getSynchronizationLogMessages()
  {
    return synchronizationLogMessages;
  }
  
  public boolean isNewLogAwailable()
  {
    return newLogAwailable;
  }

  @Override
  public void handleLog(Level level, String message, Throwable exception)
  {
    if (level.equals(Level.DEBUG))
    {
      return;
    }
    StringBuilder messageBuilder = new StringBuilder(message);
    if (exception != null)
    {
      messageBuilder.append("\n").append(exception.getMessage());
      for (StackTraceElement elt : exception.getStackTrace())
      {
        messageBuilder.append("\n\t").append(elt.toString());
      }
    }
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    synchronizationLogMessages.add("\n" + sdf.format(Calendar.getInstance().getTime()));
    synchronizationLogMessages.add(messageBuilder.toString());
    newLogAwailable = true;
  }

  @Override
  public void handleUpdate(UpdateEvent updateEvent)
  {
  }

  @Override
  public void handleFinished(UpdateEvent finalEvent)
  {
  }

}
