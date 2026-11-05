package ch.ivyteam.enginecockpit.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.ivyteam.ivy.application.app.Application;
import ch.ivyteam.ivy.application.app.ApplicationRepository;
import ch.ivyteam.ivy.application.project.Project;
import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
class TestDeploymentBean {

  private ApplicationRepository applications;
  private Application application;
  private DeploymentBean bean;

  @BeforeEach
  void beforeEach() {
    var context = Project.current().app().securityContext();
    applications = ApplicationRepository.of(context);
    application = applications.create("deployment-warning");
    bean = new DeploymentBean();
    bean.setAppNameAndVersion(context.name(), application.name(), String.valueOf(application.version()));
  }

  @AfterEach
  void afterEach() {
    if (application.state().active()) {
      application.state().deactivate();
    }
    application.projects().all().forEach(application.projects()::delete);
    applications.delete(application);
  }

  @Test
  void createdVersionWithProjectsDoesNotRequireRestart(@TempDir Path projectDirectory) {
    application.projects().create("project", projectDirectory);

    assertThat(bean.isApplicationRestartRequired()).isFalse();
  }

  @Test
  void restartWarningFollowsApplicationLifecycle() {
    application.state().prepare();
    assertThat(bean.isApplicationRestartRequired()).isFalse();

    application.state().release();
    assertThat(bean.isApplicationRestartRequired()).isTrue();

    application.state().activate();
    assertThat(bean.isApplicationRestartRequired()).isTrue();

    application.state().deactivate();
    assertThat(bean.isApplicationRestartRequired()).isTrue();

    application.state().deprecate();
    assertThat(bean.isApplicationRestartRequired()).isTrue();

    application.state().archive();
    assertThat(bean.isApplicationRestartRequired()).isFalse();
  }
}
