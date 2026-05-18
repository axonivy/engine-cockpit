package ch.ivyteam.enginecockpit.monitor.session;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

@Named
@ViewScoped
public class SessionBean {

  private final SessionDataModel dataModel = new SessionDataModel();

  public SessionDataModel getDataModel() {
    return dataModel;
  }

  public void killSession(SessionDto session) {
    var ses = session.session;
    ses.getSecurityContext().sessions().destroy(ses.getIdentifier(), "ENGINE-COCKPIT");
  }
}
