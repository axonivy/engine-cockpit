package ch.ivyteam.enginecockpit.monitor.unit;

class Scaling
{
  private Scale normalScale;
  
  private Scaling(Scale normalScale)
  {
    this.normalScale = normalScale;
  }
  
  Scale getNormal()
  {
    return normalScale;
  }

  public static final Scaling NONE = Scaling
      .startWithNormal("", "")
      .toScaling();

  static Scaling METRIC = Scaling
      .startWith("a", "atto")
      .upTo(1000, "n", "nano")
      .upTo(1000, "f", "femto")
      .upTo(1000, "p", "pico")
      .upTo(1000, "n", "nano")
      .upTo(1000, "u", "micro")
      .upTo(1000, "m", "milli")
      .upToNormal(1000, "", "")
      .upTo(1000, "k", "kilo")
      .upTo(1000, "M", "mega")
      .upTo(1000, "G", "giga")
      .upTo(1000, "T", "tera")
      .upTo(1000, "P",  "peta")
      .upTo(1000, "E", "exa")
      .toScaling();
      
  static Scaling TIME = Scaling
      .startWith("a", "atto")
      .upTo(1000, "n", "nano")
      .upTo(1000, "f", "femto")
      .upTo(1000, "p", "pico")
      .upTo(1000, "n", "nano")
      .upTo(1000, "u", "micro")
      .upTo(1000, "m", "milli")
      .upToNormal(1000, "s", "seconds").useAsFullUnitSymbol()
      .upTo(60, "m", "minutes").useAsFullUnitSymbol()
      .upTo(60, "h", "hours").useAsFullUnitSymbol()
      .upTo(24, "d", "days").useAsFullUnitSymbol()
      .upTo(365, "y", "years").useAsFullUnitSymbol()
      .toScaling();

  static Scaling MEMORY = Scaling
      .startWithNormal("", "") 
      .upTo(1024, "Ki", "kilo")
      .upTo(1024, "Mi", "mega")
      .upTo(1024, "Gi", "giga")
      .upTo(1024, "Ti", "tera")
      .upTo(1024, "Pi",  "peta")
      .upTo(1024, "Ei", "exa")
      .toScaling();
      
  static Scaling DAY_TIME = Scaling.startWithNormal("", "").toScaling();

  private static ScalingBuilder startWith(String symbol, String name)
  {
    return new ScalingBuilder(symbol, name, false);
  }
  
  private static ScalingBuilder startWithNormal(String symbol, String name)
  {
    return new ScalingBuilder(symbol, name, true);
  }

  private static class ScalingBuilder
  {
    private Scale normalScale;
    private Scale currentScale;
    
    private ScalingBuilder(String symbol, String name, boolean isNormal)
    {
      currentScale = new Scale(symbol, name);
      if (isNormal)
      {
        normalScale = currentScale;
      }
    }

    public ScalingBuilder useAsFullUnitSymbol()
    {
      currentScale.useAsFullUnitSymbol();
      return this;
    }

    public ScalingBuilder upTo(int factor, String symbol, String name)
    {
      Scale scale = new Scale(symbol, name);
      scale.setDownScale(factor, currentScale);
      currentScale.setUpScale(factor, scale);
      currentScale = scale;
      return this;
    }
    
    public ScalingBuilder upToNormal(int factor, String symbol, String name)
    {
      upTo(factor, symbol, name);
      normalScale = currentScale;
      return this;
    }
    
    public Scaling toScaling()
    {
      return new Scaling(normalScale);
    }
  }
}
