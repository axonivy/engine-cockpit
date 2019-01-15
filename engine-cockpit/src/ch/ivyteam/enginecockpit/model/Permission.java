package ch.ivyteam.enginecockpit.model;

public class Permission
{
  private String name;
  private long id;
  private boolean group;

  public Permission(String name, long id, boolean group)
  {
    this.name = name;
    this.id = id;
    this.group = group;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public long getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public boolean isGroup()
  {
    return group;
  }

  public void setGroup(boolean group)
  {
    this.group = group;
  }

}