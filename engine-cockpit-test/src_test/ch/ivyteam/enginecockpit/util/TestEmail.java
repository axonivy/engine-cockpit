package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestEmail
{
  @Test
  void validEmailAddresses()
  {
    assertThat(EmailUtil.validateEmailAddress("niceandsimple@example.com")).isTrue();
    assertThat(EmailUtil.validateEmailAddress("a.little.unusual@example.com")).isTrue();
    assertThat(EmailUtil.validateEmailAddress("a.little.more.unusual@dept.example.com")).isTrue();
  }

  @Test
  void invalidEmailAddresses()
  {
    assertThat(EmailUtil.validateEmailAddress("Abc.example.com")).isFalse();
    assertThat(EmailUtil.validateEmailAddress("A@b@c@example.com")).isFalse();
    assertThat(EmailUtil.validateEmailAddress("a\"b(c)d,e:f;g<h>i[j\\k]l@example.com")).isFalse();
  }
}
