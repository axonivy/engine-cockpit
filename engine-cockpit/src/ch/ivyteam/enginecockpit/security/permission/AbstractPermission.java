package ch.ivyteam.enginecockpit.security.permission;

public abstract class AbstractPermission {
  private String name;
  private String path;
  private long id;
  private boolean grant;
  private boolean deny;

  protected AbstractPermission(String name, String path, long id, boolean grant, boolean deny) {
    this.name = name;
    this.path = path + "/" + name;
    this.id = id;
    this.grant = grant;
    this.deny = deny;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public long getId() {
    return id;
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
