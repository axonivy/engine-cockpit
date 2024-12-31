package ch.ivyteam.enginecockpit.services.notification;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.ivy.notification.impl.NotificationRepository;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@ManagedBean
@ViewScoped
public class NotificationBean {

  private String system;
  private String id;

  private NotificationDto notification;
  private NotificationDataModel dataModel;

  public void onload() {
    var securityContext = ISecurityContextRepository.instance().get(system);
    if (securityContext == null) {
      ResponseHelper.notFound("Could not find security context " + system);
      return;
    }

    var n = NotificationRepository.of(securityContext).query()
        .where().notificationId().isEqual(id)
        .executor().firstResult();
    if (n == null) {
      ResponseHelper.notFound("Could not find notification " + id);
      return;
    }
    this.notification = new NotificationDto(n);
    this.dataModel = new NotificationDataModel(notification);
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NotificationDto getNotification() {
    return notification;
  }

  public NotificationDataModel getDataModel() {
    return dataModel;
  }
}
