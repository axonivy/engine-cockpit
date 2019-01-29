package ch.ivyteam.enginecockpit.model;

public abstract class AbstractActivity
{
  private String name;
  private long id;
  
  public AbstractActivity()
  {
    
  }
  
  public AbstractActivity(String name, long id)
  {
    this.name = name;
    this.id = id;
  }
  
  abstract String getIcon();
  
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

  public void setId(long id)
  {
    this.id = id;
  }
}
