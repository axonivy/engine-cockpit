package ch.ivyteam.enginecockpit.monitor.session;

import jakarta.inject.Named;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;

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
