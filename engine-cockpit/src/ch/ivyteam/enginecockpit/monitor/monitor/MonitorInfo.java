package ch.ivyteam.enginecockpit.monitor.monitor;

public final class MonitorInfo {
  final String title;
  final String name;
  final String icon;
  final String yAxisLabel;
  final boolean reverseColors;

  public MonitorInfo(String title, String name, String icon, String yAxisLabel, boolean reverseColors) {
    this.title = title;
    this.name = name;
    this.icon = icon;
    this.yAxisLabel = yAxisLabel;
    this.reverseColors = reverseColors;
  }

  public static Builder build() {
    return new Builder();
  }

  public static final class Builder {
    private String title;
    private String name;
    private String icon;
    private String yAxisLabel;
    private boolean reverseColors = false;

    public Builder title(String t) {
      this.title = t;
      return this;
    }

    public Builder name(String nm) {
      this.name = nm;
      return this;
    }

    public Builder icon(String icn) {
      this.icon = icn;
      return this;
    }

    public Builder yAxisLabel(String label) {
      this.yAxisLabel = label;
      return this;
    }

    public Builder setReverseColors() {
      this.reverseColors = true;
      return this;
    }

    public MonitorInfo toInfo() {
      if (name == null) {
        throw new IllegalStateException("Name must be specified");
      }
      if (icon == null) {
        throw new IllegalStateException("Icon must be specified");
      }
      if (title == null) {
        title = name;
      }
      return new MonitorInfo(title, name, icon, yAxisLabel, reverseColors);
    }
  }

}
