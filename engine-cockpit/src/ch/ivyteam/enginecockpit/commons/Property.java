package ch.ivyteam.enginecockpit.commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Property
{
  private String name;
  private String value;

  public Property()
  {
  }

  public Property(String name, String value)
  {
    this.name = name;
    this.value = value;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof Property))
    {
      return false;
    }
    if (obj == this)
    {
      return true;
    }
    Property other = (Property) obj;
    return new EqualsBuilder()
            .append(name, other.getName())
            .append(value, other.getValue())
            .isEquals();
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder()
            .append(name)
            .append(value)
            .toHashCode();
  }
}
