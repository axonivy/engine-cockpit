package ch.ivyteam.enginecockpit.monitor.trace;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import ch.ivyteam.ivy.trace.Tracer;

public class TracerAccess implements BeforeEachCallback, AfterEachCallback {

  private static final ReentrantLock ACCESS_LOCK = new ReentrantLock();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    ACCESS_LOCK.lock();
    start();
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    stop();
    ACCESS_LOCK.unlock();
  }


  private void start() {
    Tracer.instance().start();
    Tracer.instance().slowTraces().clear();
    Tracer.instance().systemOverview().clear();
  }

  private void stop() {
    Tracer.instance().start();
    Tracer.instance().slowTraces().clear();
    Tracer.instance().systemOverview().clear();
  }
}
