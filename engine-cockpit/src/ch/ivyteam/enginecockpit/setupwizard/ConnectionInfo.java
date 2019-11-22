package ch.ivyteam.enginecockpit.setupwizard;

import ch.ivyteam.ivy.persistence.db.connection.ConnectionTestResult;

@SuppressWarnings("restriction")
public class ConnectionInfo
{
  private String label;
  private String advise;
  private String messageLevel;
  private String icon;
  private boolean mustConvert;
  private boolean mustCreate;
  private boolean successful;

  public ConnectionInfo()
  {
    label = "Connection state unknown";
    advise = "Please check the connection to the Database.";
    messageLevel = "ui-message-info";
    icon = "fa fa-plug fa-fw";
  }
  
  public ConnectionInfo(ConnectionTestResult result)
  {
    label = result.getConnectionState().getLabel();
    advise = result.getConnectionState().getAdvise();
    messageLevel = getMessageLevel(result);
    icon = "fa fa-plug fa-fw";
    mustConvert = result.mustConvert();
    mustCreate = result.mustCreate();
    successful = result.isSuccessful();
  }
  
  public String getLabel()
  {
    return label;
  }

  public String getAdvise()
  {
    return advise;
  }

  public String getMessageLevel()
  {
    return messageLevel;
  }

  public String getIcon()
  {
    return icon;
  }
  
  public boolean isMustConvert()
  {
    return mustConvert;
  }
  
  public boolean isMustCreate()
  {
    return mustCreate;
  }
  
  public boolean isSuccessful()
  {
    return successful;
  }

  private static String getMessageLevel(ConnectionTestResult result)
  {
    if (result.isSuccessful())
    {
      return "ui-message-success";
    }
    else if (result.isFailed())
    {
      return "ui-message-error";
    }
    return "ui-message-warn";
  }
  
}
