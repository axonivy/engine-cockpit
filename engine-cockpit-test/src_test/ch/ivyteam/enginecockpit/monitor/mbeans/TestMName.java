package ch.ivyteam.enginecockpit.monitor.mbeans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.jupiter.api.Test;

class TestMName {

  @Test
  void displayName() {
    var name = MName.parse("ivy Engine:\"type\"=Periodical Job,name=\"gugus [class=tata, pmv=gaga]\"");
    assertThat(name.getDisplayName()).isEqualTo("\"gugus [class=tata, pmv=gaga]\"");
  }

  @Test
  void fullDisplayName() {
    var name = MName.parse("ivy Engine:\"type\"=Periodical Job,name=\"gugus [class=tata, pmv=gaga]\"");
    assertThat(name.getFullDisplayName()).isEqualTo("ivy Engine/Periodical Job/\"gugus [class=tata, pmv=gaga]\"");
  }

  @Test
  void getObjectName() throws MalformedObjectNameException {
    var name = MName.parse("ivy Engine:\"type\"=Periodical Job,name=\"gugus [class=tata, pmv=gaga]\"");
    assertThat(name.getObjectName()).isEqualTo(new ObjectName("ivy Engine:\"type\"=Periodical Job,name=\"gugus [class=tata, pmv=gaga]\""));
  }

  @Test
  void getDirectChildren() {
    var name = MName.parse("ivy Engine:\"type\"=Periodical Job");
    var all = Set.of(
        MName.parse("ivy Engine:\"type\"=Periodical Job"),
        MName.parse("ivy Engine:\"type\"=Periodical Job,name=\"gugus [class=tata, pmv=gaga]\""),
        MName.parse("ivy Engine:\"type\"=Periodical Job,\"name\"=sugus"),
        MName.parse("ivy Engine:\"type\"=\"Cron Job\",\"name\"=sugus"));
    assertThat(name.getDirectChildren(all)).containsExactly(
        MName.parse("ivy Engine:\"type\"=Periodical Job,name=\"gugus [class=tata, pmv=gaga]\""),
        MName.parse("ivy Engine:\"type\"=Periodical Job,\"name\"=sugus"));
  }
}
