package ch.ivyteam.enginecockpit.commons;

import java.io.Serializable;
import java.util.function.LongSupplier;

import ch.ivyteam.log.Logger;

public class LazyCount implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(LazyCount.class);

  private boolean initialized;
  private Long value;

  public Long getCount(String description, LongSupplier query) {
    if (!initialized) {
      initialized = true;
      try {
        value = query.getAsLong();
      } catch (RuntimeException ex) {
        LOGGER.error("Could not load " + description, ex);
      }
    }
    return value;
  }
}
