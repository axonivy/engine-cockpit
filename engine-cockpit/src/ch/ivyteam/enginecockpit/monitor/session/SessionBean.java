package ch.ivyteam.enginecockpit.monitor.session;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class SessionBean implements Serializable {

  private final SessionDataModel dataModel = new SessionDataModel();

  public SessionDataModel getDataModel() {
    return dataModel;
  }

  public void killSession(SessionDto session) {
    var ses = session.session;
    ses.getSecurityContext().sessions().destroy(ses.getIdentifier(), "ENGINE-COCKPIT");
  }
}
