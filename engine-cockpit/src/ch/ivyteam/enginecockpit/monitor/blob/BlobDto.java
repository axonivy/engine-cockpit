package ch.ivyteam.enginecockpit.monitor.blob;

import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.ivy.blob.storage.core.Blob;
import ch.ivyteam.ivy.blob.storage.core.BlobService;
import ch.ivyteam.util.mime.MimeTypeEvaluator;

public class BlobDto {

  private final Blob blob;

  private final Date createdAt;
  private final String creatorLink;

  private final Date modifiedAt;
  private final String modifierLink;

  public BlobDto(Blob blob) {
    this.blob = blob;
    this.createdAt = Date.from(blob.createdAt());
    this.modifiedAt = Date.from(blob.modifiedAt());
    this.creatorLink = new User(blob.creator()).getViewUrl();
    this.modifierLink = new User(blob.modifier()).getViewUrl();
  }

  public String getId() {
    return blob.uuid();
  }

  public String getPath() {
    if (BlobService.of(blob.app()).isUsingLegacyPath()) {
      return blob.filePath();
    }
    return blob.path();
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public String getCreator() {
    return blob.creator().getName();
  }

  public String getCreatorLink() {
    return creatorLink;
  }

  public Date getModifiedAt() {
    return modifiedAt;
  }

  public String getModifier() {
    return blob.modifier().getName();
  }

  public String getModifierLink() {
    return modifierLink;
  }

  public String getSize() {
    return FileUtils.byteCountToDisplaySize(blob.size());
  }

  public String getMimeType() {
    return MimeTypeEvaluator.ofFilename(blob.fileName());
  }

  public StreamedContent download() {
    return DefaultStreamedContent.builder()
            .name(blob.fileName())
            .contentType(getMimeType())
            .stream(() -> BlobService.of(blob.app()).read(blob.uuid()))
            .build();
  }
}
