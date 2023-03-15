package ch.ivyteam.enginecockpit.services.model;

import java.util.concurrent.Callable;

import com.google.common.base.Objects;

import ch.ivyteam.log.Logger;

public class ConnectionTestWrapper {

  private static final Logger LOGGER = Logger.getLogger(ConnectionTestWrapper.class);

  private Callable<?> callable;
  private Object defaultValue;

  public ConnectionTestWrapper() {
    this(null);
  }

  public ConnectionTestWrapper(Object defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Object test(Callable<?> connectionTestCallable) {
    callable = connectionTestCallable;
    var result = call();
    return Objects.equal(connectionTestCallable, callable) ? result : defaultValue;
  }

  public void stop() {
    callable = null;
  }

  private Object call() {
    try {
      return callable.call();
    } catch (Exception ex) {
      LOGGER.error("Can not test rest connection", ex);
      return defaultValue;
    }
  }
}
