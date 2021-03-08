package ch.ivyteam.enginecockpit.commons;

import java.util.function.Predicate;

import javax.faces.model.SelectItem;

public class ContentFilter<T>
{

  private String name;
  private final SelectItem selectItem;
  private final Predicate<T> filter;
  private final boolean inverted;
  private boolean enabled;
  
  public ContentFilter(String name, String label, Predicate<T> filter)
  {
    this(name, label, filter, false);
  }

  public ContentFilter(String name, String label, Predicate<T> filter, boolean inverted)
  {
    this.name = name;
    this.selectItem = new SelectItem(name, label);
    this.filter = filter;
    this.inverted = inverted;
    this.enabled = false;
  }
  
  public String name()
  {
    return name;
  }
  
  public SelectItem selectItem()
  {
    return selectItem;
  }
  
  public boolean inverted()
  {
    return inverted;
  }
  
  public boolean enabled()
  {
    return enabled;
  }
  
  public void enabled(boolean enable)
  {
    this.enabled = enable;
  }
  
  public boolean isActive()
  {
    return inverted ^ enabled;
  }
  
  public Predicate<T> filter()
  {
    return filter;
  }
  
}
