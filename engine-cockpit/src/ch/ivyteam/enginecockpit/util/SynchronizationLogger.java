package ch.ivyteam.enginecockpit.util;

import java.util.ArrayList;
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
    newLogAwailable = false;
    return synchronizationLogMessages;
  }
  
  public boolean isNewLogAwailabe()
  {
    return newLogAwailable;
  }

  @Override
  public void handleLog(Level level, String message, Throwable exception)
  {
    StringBuilder messageBuilder = new StringBuilder(message);

    if (exception != null)
    {
      messageBuilder.append("\n").append(exception.getMessage());
      for (StackTraceElement elt : exception.getStackTrace())
      {
        messageBuilder.append("\n\t").append(elt.toString());
      }
    }

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
