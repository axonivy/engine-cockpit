package ch.ivyteam.enginecockpit.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.ivy.application.internal.app.move.ApplicationMover;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISecurityManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class MoveApplicationBean {

  public enum ValidationState {
    VALID,
    INVALID,
    UNKOWN
  }

  private Application app;
  private ValidationState state = ValidationState.UNKOWN;
  private String validationMessage = "";
  private String targetSecuritySystem;

  public void move() {
    mover().move();
  }

  public void validate() {
    if (targetSecuritySystem == null || targetSecuritySystem.isEmpty()) {
      state = ValidationState.INVALID;
      validationMessage = "No target security system selected";
      return;
    }
    var result = mover().validate();
    if (result.ok()) {
      state = ValidationState.VALID;
      validationMessage = "Application can be moved to target security system";
    } else {
      state = ValidationState.INVALID;
      validationMessage = result.errors().stream().collect(Collectors.joining(", "));
    }
  }

  private ApplicationMover mover() {
    var securityContext = ISecurityContextRepository.instance().get(targetSecuritySystem);
    return new ApplicationMover(app.app(), securityContext, ISystemDatabasePersistencyService.instance());
  }

  public String getValidationMessage() {
    return validationMessage;
  }

  public String getValidationSeverity() {
    return isValid() ? "info" : "error";
  }

  public void setValidationMessage(String validationMessage) {
    this.validationMessage = validationMessage;
  }

  public boolean showValidationMessage() {
    return state != ValidationState.UNKOWN;
  }

  public boolean isValid() {
    return state == ValidationState.VALID;
  }

  public void setApp(Application app) {
    state = ValidationState.UNKOWN;
    validationMessage = "";
    this.app = app;
  }

  public String getAppName() {
    if (app == null) {
      return null;
    }
    return app.getName();
  }

  public String getSecuritySystemName() {
    if (app == null) {
      return null;
    }
    return app.getSecuritySystemName();
  }

  public List<String> getSecuritySystems() {
    return ISecurityManager.instance().securityContexts().all().stream()
            .filter(s -> !ISecurityContext.SYSTEM.equals(s.getName()))
            .filter(s -> app != null && !s.equals(app.getSecurityContext()))
            .map(ISecurityContext::getName)
            .collect(Collectors.toList());
  }

  public void setTargetSecuritySystem(String targetSecuritySystem) {
    this.targetSecuritySystem = targetSecuritySystem;
  }

  public String getTargetSecuritySystem() {
    return targetSecuritySystem;
  }
}
