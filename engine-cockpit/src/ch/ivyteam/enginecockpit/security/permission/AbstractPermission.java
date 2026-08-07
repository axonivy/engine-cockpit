package ch.ivyteam.enginecockpit.security.permission;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;

public abstract class AbstractPermission {

  private final String name;
  private boolean grant;
  private boolean deny;
  private boolean someGrant;
  private boolean someDeny;
  private boolean group;
  private Boolean state;
  private State initialState;
  private boolean isGroup = false;

  protected AbstractPermission(String name, boolean grant, boolean deny) {
    this.name = name;
    this.grant = grant;
    this.deny = deny;
  }

  private enum State {
    DEFAULT,
    GRANTED,
    DENIED,
    SOMEGRANTED,
    SOMEDENIED,
    STATELESS;
  }

  public Boolean getState() {
    return state;
  }

  public void setState(Boolean state) {
    // Ingore setState, will also be called with expand tree node
    // State will be set via defineState method
  }

  public void initialState() {
    if (isGrant()) {
      initialState = State.GRANTED;
    }
    if (isDeny()) {
      initialState = State.DENIED;
    }
    if (isSomeGrant()) {
      initialState = State.SOMEGRANTED;
    }
    if (isSomeDeny()) {
      initialState = State.SOMEDENIED;
    }
    if (isGroup()) {
      isGroup = true;
    } else {
      initialState = State.STATELESS;
    }
  }

  public void defineState(AjaxBehaviorEvent event) {
    var clientId = event.getComponent().getClientId(FacesContext.getCurrentInstance());
    var submittedValue = FacesContext.getCurrentInstance().getExternalContext()
        .getRequestParameterMap().get(clientId + "_input");
    if ("0".equals(submittedValue) || submittedValue != null && submittedValue.isBlank()) {
      state = null;
      resetToInitialState();
    } else if ("1".equals(submittedValue)) {
      state = true;
      grant();
    } else if ("2".equals(submittedValue)) {
      state = false;
      deny();
    }
  }

  private void resetToInitialState() {
    if (isGroup) {
      group();
    }
    switch (initialState) {
      case State.GRANTED -> grant();
      case State.DENIED -> deny();
      case State.SOMEGRANTED -> someGrant();
      case State.SOMEDENIED -> someDeny();
      case State.STATELESS -> {
        grant();
        ungrant();
      }
      default -> {}
    }
  }

  public String getName() {
    return name;
  }

  public boolean isGrant() {
    return grant;
  }

  public void setGrant(boolean grant) {
    this.grant = grant;
  }

  public boolean isDeny() {
    return deny;
  }

  public void setDeny(boolean deny) {
    this.deny = deny;
  }

  public boolean isSomeGrant() {
    return someGrant;
  }

  public void setSomeGrant(boolean someGrant) {
    this.someGrant = someGrant;
  }

  public boolean isSomeDeny() {
    return someDeny;
  }

  public void setSomeDeny(boolean someDeny) {
    this.someDeny = someDeny;
  }

  public boolean isGroup() {
    return group;
  }

  public void setGroup(boolean group) {
    this.group = group;
  }

  public abstract void someGrant();

  public abstract void someDeny();

  public abstract void grant();

  public abstract void deny();

  public abstract void group();

  public abstract void ungrant();
}
