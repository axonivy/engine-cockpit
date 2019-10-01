package ch.ivyteam.enginecockpit.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import ch.ivyteam.enginecockpit.fileupload.HttpFile;
import ch.ivyteam.licence.InvalidLicenceException;
import ch.ivyteam.licence.LicenceConstants;
import ch.ivyteam.licence.SignedLicence;

public class LicenceUtil
{
  private static final File CONFIG_DIR = new File("configuration");

  private static void backup(File license) throws IOException
  {
    File backupFile = new File(license.getAbsolutePath().concat(".bak"));
    Files.deleteIfExists(backupFile.toPath());
    Files.move(license.toPath(), backupFile.toPath());
    Files.deleteIfExists(license.toPath());
  }

  public static File verifyAndInstall(HttpFile uploadedFile) throws Exception
  {
    File tempLicence = uploadFile(uploadedFile);
    try
    {
      verify(tempLicence);
      return install(tempLicence, uploadedFile.getSubmittedFileName());
    }
    finally
    {
      Files.deleteIfExists(tempLicence.toPath());
    }
  }

  private static File install(File tempLicence, String fileName) throws InvalidLicenceException, IOException
  {
    File newLicence = new File(CONFIG_DIR, fileName);
    if (newLicence.exists())
    {
      backup(newLicence);
    }
    Files.copy(tempLicence.toPath(), newLicence.toPath());
    SignedLicence.installLicence(newLicence);
    return newLicence;
  }

  public static void verify(File newLicence) throws Exception
  {
    SignedLicence.verifyLicence(newLicence);
  }

  private static File uploadFile(HttpFile uploadedFile) throws IOException, FileNotFoundException
  {
    File tempLicence = File.createTempFile("temp", uploadedFile.getSubmittedFileName());
    try (FileOutputStream fos = new FileOutputStream(tempLicence);
            InputStream is = uploadedFile.getStream())
    {
      IOUtils.copy(is, fos);
    }
    return tempLicence;
  }

  public static boolean isCluster()
  {
    return LicenceConstants.VAL_LICENCE_TYPE_ENTERPRISE
            .equals(SignedLicence.getParam(ch.ivyteam.licence.LicenceConstants.PARAM_LICENCE_TYPE));
  }

  public static boolean isDemo()
  {
    if (StringUtils.isNotEmpty(System.getProperty("ch.ivyteam.ivy.server.configuration.development")))
    {
      return false;
    }
    if (SignedLicence.isDemo())
    {
      return true;
    }
    return false;
  }
}
