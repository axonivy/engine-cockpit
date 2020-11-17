package ch.ivyteam.enginecockpit.monitor.unit;

import java.util.Objects;

class Scale
{
  private final String symbol;
  private final String name;
  private long downScaleFactor = 0;
  private Scale downScale = null;
  private long upScaleFactor = 0;
  private Scale upScale =null;
  private boolean useAsFullUnitSymbol;
  
  Scale(String symbol, String name)
  {
    this.symbol = symbol;
    this.name = name;
  }

  void setDownScale(long downScaleFactor, Scale downScale)
  {
    this.downScaleFactor = downScaleFactor;
    this.downScale = downScale;
  }

  void setUpScale(long upScaleFactor, Scale upScale)
  {
    this.upScaleFactor = upScaleFactor;
    this.upScale = upScale;
  }

  void useAsFullUnitSymbol()
  {
    this.useAsFullUnitSymbol = true;
  }

  public Scale scaleUp()
  {
    return upScale;
  }

  public Scale scaleDown()
  {
    return downScale;
  }

  public String enhanceSymbol(String baseSymbol)
  {
    if (useAsFullUnitSymbol)
    {
      return symbol;
    }
    return symbol+baseSymbol;
  }
  
  public String enhanceName(String baseName)
  {
    if (useAsFullUnitSymbol)
    {
      return name;
    }
    if (name.isEmpty())
    {
      return baseName;
    }      
    return name+" "+baseName;
  }


  public long converTo(long value, Scale toScale)
  {
    if (this == toScale)
    {
      return value;
    }
    long scaleUpFactor = getUpFactorTo(toScale);
    if (scaleUpFactor > 0)
    {
      return value / scaleUpFactor;
    }

    long scaleDownFactor = getDownFactorTo(toScale);
    if (scaleDownFactor > 0)
    {
      return value * scaleDownFactor;
    }
    throw new IllegalArgumentException("The parameter toScale "+toScale+" does not belong to the current scaling "+this);
  }
  
  public double convertTo(double value, Scale toScale)
  {
    if (this == toScale)
    {
      return value;
    }
    long scaleUpFactor = getUpFactorTo(toScale);
    if (scaleUpFactor > 0)
    {
      return value / scaleUpFactor;
    }

    long scaleDownFactor = getDownFactorTo(toScale);
    if (scaleDownFactor > 0)
    {
      return value * scaleDownFactor;
    }
    throw new IllegalArgumentException("The parameter toScale "+toScale+" does not belong to the current scaling "+this);
  }


  private long getUpFactorTo(Scale toScale)
  {
    if (this == toScale)
    {
       return 1;
    }
    if (upScale == null)
    {
      return -1;
    }
    long upUpScaleFactor = upScale.getUpFactorTo(toScale);
    if (upUpScaleFactor < 0)
    {
      return -1;
    }
    return upUpScaleFactor * upScaleFactor;
  }
  
  private long getDownFactorTo(Scale toScale)
  {
    if (this == toScale)
    {
       return 1;
    }
    if (downScale == null)
    {
      return -1;
    }
    long downDownScaleFactor = downScale.getDownFactorTo(toScale);
    if (downDownScaleFactor < 0)
    {
      return -1;
    }
    return downDownScaleFactor * downScaleFactor;
  }
  
  @Override
  public String toString()
  {
    return symbol+" ("+name+")";
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (obj.getClass() != Scale.class)
    {
      return false;
    }
    Scale other = (Scale)obj;
    return Objects.equals(symbol, other.symbol) && 
           Objects.equals(name, other.name) && 
           downScaleFactor == other.downScaleFactor &&
           upScaleFactor == other.upScaleFactor &&
           useAsFullUnitSymbol == other.useAsFullUnitSymbol;
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(symbol, name);
  }
}
