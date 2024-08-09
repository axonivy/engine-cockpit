package ch.ivyteam.enginecockpit.security.permission;

public abstract class AbstractPermission {
  private String name;
  private boolean grant;
  private boolean deny;
  private String state;
  private String initialState;

  protected AbstractPermission(String name, boolean grant, boolean deny) {
    this.name = name;
    this.grant = grant;
    this.deny = deny;
  }

  private interface State {
    String DEFAULT = "0";
    String GRANTED = "1";
    String DENIED = "2";
    String SOMEGRANTED = "3";
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
      this.state = state;
  }

  public void initialState() {
    if (isGrant())
      initialState = State.GRANTED;
    else if (isDeny())
      initialState = State.DENIED;
    else if (isSomeGrant())
      initialState = State.SOMEGRANTED;
  }

  public void defineState() {
    switch (state) {
      case State.DEFAULT:
          switch (initialState) {
              case State.GRANTED: grant(); break;
              case State.DENIED: deny(); break;
              case State.SOMEGRANTED: deny(); undeny(); break;
              case null: grant(); ungrant(); break;
            default: grant(); ungrant(); break;
          }
          break;
      case State.GRANTED: grant(); break;
      case State.DENIED: deny(); break;
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

  public abstract boolean isSomeGrant();

  public abstract boolean isSomeDeny();

  public abstract boolean isGrantDisabled();

  public abstract boolean isUnGrantDisabled();

  public abstract boolean isDenyDisabled();

  public abstract boolean isUnDenyDisabled();

  public abstract void grant();

  public abstract void ungrant();

  public abstract void deny();

  public abstract void undeny();

  public abstract boolean isGroup();
}
