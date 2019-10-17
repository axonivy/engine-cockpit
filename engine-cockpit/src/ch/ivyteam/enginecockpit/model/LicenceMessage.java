package ch.ivyteam.enginecockpit.model;

import java.text.SimpleDateFormat;

import ch.ivyteam.licence.LicenceEvent;
import ch.ivyteam.licence.LicenceEvent.Level;

public class LicenceMessage
{

  private Level level;
  private String message;
  private String timestamp;
  private LicenceEvent event;

  public LicenceMessage(LicenceEvent event)
  {
    level = event.getLevel();
    message = event.getMessage();
    timestamp = new SimpleDateFormat("dd MMMMM yyyy").format(event.getTimestamp());
    this.event = event;
  }

  public Level getLevel()
  {
    return level;
  }

  public String getMessage()
  {
    return message;
  }

  public String getTimestamp()
  {
    return timestamp;
  }
  
  public void confirm(String user)
  {
    event.confirm(user);
  }
  
}
