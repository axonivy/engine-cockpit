package ch.ivyteam.enginecockpit.system.ssl;

import java.security.cert.X509Certificate;
import java.util.Date;

public class StoredCert {

  private final String alias;
  private final X509Certificate cert;
  private String invalidityMessage;

  public StoredCert(String alias, X509Certificate cert) {
    this.alias = alias;
    this.cert = cert;
  }

  public String getAlias() {
    return alias;
  }

  public X509Certificate getCert() {
    return cert;
  }

  public String getSubject() {
    if (cert == null) {
      return "";
    }
    String cName = cert.getSubjectX500Principal().getName();
    return shortSubject(cName);
  }

  public static String shortSubject(String fullName) {
    var nameParts = fullName.split(",");
    String cName = nameParts[0];
    var assign = cName.indexOf('=');
    if (assign != -1) {
      return cName.substring(assign+1);
    }
    return cName;
  }

  public boolean isExpired() {
    return cert != null && !cert.getNotAfter().before(new Date());
  }

  public boolean isValid() {
    try {
        cert.checkValidity();
        invalidityMessage = null;
        return true;
    } catch (Exception ex) {
        invalidityMessage = ex.getMessage();
        return false;
    }
  }

  public String getInvalidityMessage() {
    return invalidityMessage;
  }
}