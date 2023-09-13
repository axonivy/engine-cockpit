package ch.ivyteam.enginecockpit.security.model;

import ch.ivyteam.ivy.notification.channel.impl.NotificationSubscription;

public class NotificationChannelSubscriptionDto {

  private State state;
  private final boolean isSubscribedByDefault;
  private String icon;
  private String title;

  public NotificationChannelSubscriptionDto(NotificationSubscription.State state,
          boolean isSubscribedByDefault) {
    this.state = State.fromDbState(state);
    this.isSubscribedByDefault = isSubscribedByDefault;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public String getStateAsString() {
    return state.toString();
  }

  public void setStateAsString(String value) {
    this.state = State.of(value);
  }

  public boolean isSubscribedByDefault() {
    return isSubscribedByDefault;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public static enum State {
    USE_DEFAULT("0"), SUBSCRIBED("1"), NOT_SUBSCRIBED("2");

    private String value;

    private State(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }

    public static State of(String value) {
      for(State state: values()) {
        if (state.value.equals(value)) {
          return state;
        }
      }
      throw new IllegalArgumentException("Unexpected value: " + value);
    }

    public static NotificationChannelSubscriptionDto.State fromDbState(NotificationSubscription.State state) {
      switch (state) {
        case USE_DEFAULT -> {
          return USE_DEFAULT;
        }
        case SUBSCRIBED -> {
          return SUBSCRIBED;
        }
        case NOT_SUBSCRIBED -> {
          return NOT_SUBSCRIBED;
        }
        default -> throw new IllegalArgumentException("Unexpected value: " + state);
      }
    }

    public NotificationSubscription.State toDbState() {
      switch (value) {
        case "0" -> {
          return NotificationSubscription.State.USE_DEFAULT;
        }
        case "1" -> {
          return NotificationSubscription.State.SUBSCRIBED;
        }
        case "2" -> {
          return NotificationSubscription.State.NOT_SUBSCRIBED;
        }
        default -> throw new IllegalArgumentException("Unexpected value: " + value);
      }
    }
  }
}
