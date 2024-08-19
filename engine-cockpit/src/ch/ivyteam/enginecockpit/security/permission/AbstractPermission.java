package ch.ivyteam.enginecockpit.security.permission;

public abstract class AbstractPermission {

  private String name;
  private boolean grant;
  private boolean deny;
  private boolean someGrant;
  private boolean someDeny;
  private boolean group;
  private Integer state;
  private Integer initialState;
  private boolean isGroup = false;

  protected AbstractPermission(String name, boolean grant, boolean deny) {
    this.name = name;
    this.grant = grant;
    this.deny = deny;
  }

  private interface State {
    int DEFAULT = 0;
    int GRANTED = 1;
    int DENIED = 2;
    int SOMEGRANTED = 3;
    int SOMEDENIED = 4;
    int STATELESS = 5;
  }

  public Integer getState() { 
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
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
        initialState = State.SOMEGRANTED;
    }
    if (isGroup()) {
    isGroup = true;
  }
    else {
    initialState = State.STATELESS;
  }
  }

  public void defineState() {
      switch (state) {
      case State.DEFAULT:
        resetToInitialState();
        break;
      case State.GRANTED:
        grant();
        break;
      case State.DENIED:
        deny();
        break;
      default:
        break;
    }
  }

  private void resetToInitialState() {
    if (isGroup) {
    group();
  }
      switch (initialState) {
      case State.GRANTED:
        grant();
        break;
      case State.DENIED:
        deny();
        break;
      case State.SOMEGRANTED:
        someGrant();
        break;
      case State.SOMEDENIED:
        someDeny();
        break;
      case State.STATELESS:
          grant();
          ungrant();
          break;
      default:
        break;
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

  public abstract void ungrant() ;
}
