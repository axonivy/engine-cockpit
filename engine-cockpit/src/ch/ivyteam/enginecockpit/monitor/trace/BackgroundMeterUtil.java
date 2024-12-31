package ch.ivyteam.enginecockpit.monitor.trace;

public class BackgroundMeterUtil {
  public static String background(double value, double max) {
    return background(percentage(value, max));
  }

  public static String background(long value, long max) {
    return background(percentage(value, max));
  }

  private static int percentage(long value, long max) {
    var percentage = value * 100.0f / max;
    return (int) percentage;
  }

  private static int percentage(double value, double max) {
    var percentage = value * 100.0f / max;
    return (int) percentage;
  }

  private static String background(int percentage) {
    var maxPercentage = Math.min(percentage + 20, 100);
    var color = 120 - percentage * 120 / 100;
    return "linear-gradient(90deg, hsl(" + color + ", 100%, 70%) " + percentage + "%, var(--surface-a, #FFFFFF) " + maxPercentage + "%)";
  }

}
