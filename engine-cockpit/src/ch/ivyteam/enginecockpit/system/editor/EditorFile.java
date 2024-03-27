package ch.ivyteam.enginecockpit.system.editor;

import java.io.InputStream;
import java.nio.file.Path;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.configuration.file.provider.ConfigFile;

public class EditorFile {

  private final ConfigFile file;
  private String content;
  private boolean migrated;

  public EditorFile(ConfigFile file) {
    this.file = file;
  }

  public String getFileName() {
    return file.name();
  }

  public Path getPath() {
    return file.file();
  }

  public boolean isMigrated() {
    return migrated;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    var read = file.read();
    this.migrated = file.isMigrated(read);
    return read;
  }

  public void save() {
    try {
      file.write(content);
      Message.info()
              .clientId("editorMessage")
              .summary("Saved " + file.name() + " successfully")
              .show();
    } catch (Exception ex) {
      Message.error()
              .clientId("editorMessage")
              .exception(ex)
              .show();
    }
  }

  public String getProvider() {
    return file.provider();
  }

  public void setBinary(InputStream is) {
    file.setBinaryFile(is);
  }

  public boolean isBinary() {
    return file.isBinaryFile();
  }

  public boolean isReadOnly() {
    return file.isReadOnly();
  }

  public long size() {
    return file.size();
  }

  public boolean isOriginalFile() {
    return file.isOriginalFile();
  }

  public Path getOriginalPath() {
    return file.originalFile();
  }
}
