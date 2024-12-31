package ch.ivyteam.enginecockpit.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.IProcessModelVersionInternal;

public class TestProcessModelVersion {

  /** ISSUE XIVY-4159 Application overview not displayed due to NPE */
  @Test
  void emptyLibrary() {
    ProcessModelVersion pmv = new ProcessModelVersion(createPmvProxy());
    assertThat(pmv.getQualifiedVersion()).isEqualTo("Unknown version");
    assertThat(pmv.getLibraryId()).isEqualTo("Unknown id");
  }

  private IProcessModelVersion createPmvProxy() {
    var cl = TestProcessModelVersion.class.getClassLoader();
    var interfaces = new Class[] {IProcessModelVersion.class, IProcessModelVersionInternal.class};
    return (IProcessModelVersion) Proxy.newProxyInstance(cl, interfaces, TestProcessModelVersion::invoke);
  }

  @SuppressWarnings("unused")
  private static Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("getId".equals(method.getName())) {
      return 0l;
    }
    return null;
  }
}
