package ch.ivyteam.enginecockpit.monitor.session;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
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
