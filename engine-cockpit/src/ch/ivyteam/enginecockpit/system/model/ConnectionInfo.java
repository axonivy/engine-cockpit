package ch.ivyteam.enginecockpit.system.model;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.persistence.db.connection.ConnectionTestResult;

public class ConnectionInfo {
  private final String label;
  private final String advise;
  private final String messageLevel;
  private final String icon;
  private boolean mustConvert;
  private boolean mustCreate;
  private boolean successful;
  private Exception error;

  public ConnectionInfo() {
    label = Ivy.cm().co("/systemDb/ConnectionInfoLabel");
    advise = Ivy.cm().co("/systemDb/ConnectionInfoAdvise");
    messageLevel = "ui-message-info";
    icon = "si si-question-circle";
  }

  public ConnectionInfo(ConnectionTestResult result) {
    label = result.getConnectionState().getLabel();
    advise = result.getConnectionState().getAdvise();
    messageLevel = getMessageLevel(result);
    icon = getIcon(result);
    mustConvert = result.mustConvert();
    mustCreate = result.mustCreate();
    successful = result.isSuccessful();
    error = result.getError();
  }

  public String getLabel() {
    return label;
  }

  public String getAdvise() {
    return advise;
  }

  public boolean hasError() {
    return error != null && !error.getMessage().isBlank();
  }

  public Exception getError() {
    return error;
  }

  public String getErrorMessage() {
    return hasError() ? Ivy.cm().co("/systemDb/Error") + error.getMessage() : "";
  }

  public String getMessageLevel() {
    return messageLevel;
  }

  public String getIcon() {
    return icon;
  }

  public boolean isMustConvert() {
    return mustConvert;
  }

  public boolean isMustCreate() {
    return mustCreate;
  }

  public boolean isSuccessful() {
    return successful;
  }

  private static String getMessageLevel(ConnectionTestResult result) {
    if (result.isSuccessful()) {
      return "ui-message-success";
    } else if (result.isFailed()) {
      return "ui-message-error";
    }
    return "ui-message-warn";
  }

  private static String getIcon(ConnectionTestResult result) {
    if (result.isSuccessful()) {
      return "si si-check-circle";
    } else if (result.isFailed()) {
      return "si si-delete-1";
    }
    return "si si-question-circle";
  }

}
