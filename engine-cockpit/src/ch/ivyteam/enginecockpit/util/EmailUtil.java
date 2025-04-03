package ch.ivyteam.enginecockpit.util;

import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

public class EmailUtil {

  private static final Pattern EMAIL_REGEX = Pattern
      .compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

  public static boolean validateEmailAddress(String email) {
    if (email == null) {
      return false;
    }
    return EMAIL_REGEX.matcher(email).matches();
  }

  public static String gravatarHash(String email) {
    if (EmailUtil.validateEmailAddress(email)) {
      return DigestUtils.md5Hex(email.toLowerCase()).toString();
    }
    return "";
  }
}
