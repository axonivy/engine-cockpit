package ch.ivyteam.enginecockpit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class DownloadUtil
{

  public static void compressDirectory(File sourceDirectory, OutputStream out) throws IOException
  {
    if (sourceDirectory.exists())
    {
      try (ZipOutputStream zipOut = new ZipOutputStream(out))
      {
        compressDirectory(sourceDirectory.getAbsoluteFile(), sourceDirectory, zipOut);
      }
    }
  }

  private static void compressDirectory(File rootDir, File sourceDir, ZipOutputStream out) throws IOException
  {
    for (File file : sourceDir.listFiles())
    {
      if (file.isDirectory())
      {
        compressDirectory(rootDir, new File(sourceDir, file.getName()), out);
      }
      else
      {
        String zipEntryName = getRelativeZipEntryName(rootDir, file);
        compressFile(out, file, zipEntryName);
      }
    }
  }

  private static String getRelativeZipEntryName(File rootDir, File file)
  {
    return StringUtils.removeStart(file.getAbsolutePath(), rootDir.getAbsolutePath());
  }

  private static void compressFile(ZipOutputStream out, File file, String zipEntityName) throws IOException
  {
    ZipEntry entry = new ZipEntry(zipEntityName);
    out.putNextEntry(entry);

    try (FileInputStream in = new FileInputStream(file))
    {
      IOUtils.copy(in, out);
    }
  }
}
