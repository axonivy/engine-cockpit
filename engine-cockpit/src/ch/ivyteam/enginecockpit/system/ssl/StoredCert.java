package ch.ivyteam.enginecockpit.system.ssl;

import java.security.cert.X509Certificate;

public class StoredCert {

  private final String alias;
  private final X509Certificate cert;
  private final String invalidityMessage;

  public StoredCert(String alias, X509Certificate cert) {
    this.alias = alias;
    this.cert = cert;
    this.invalidityMessage = validate(cert);
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

  public boolean isValid() {
    return invalidityMessage == null;
  }

  public String getInvalidityMessage() {
    return invalidityMessage;
  }

  private static String validate(X509Certificate cert) {
    try {
      cert.checkValidity();
      return null;
    } catch (Exception ex) {
      return ex.getMessage();
    }
  }

}
