package ch.ivyteam.enginecockpit.test;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;

public class FileDownloadExtension implements BeforeAllCallback, AfterAllCallback {

  private boolean originalProxyEnabled;
  private FileDownloadMode originalFileDownload;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    Selenide.closeWebDriver();
    this.originalProxyEnabled = Configuration.proxyEnabled;
    this.originalFileDownload = Configuration.fileDownload;
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = FileDownloadMode.PROXY;
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    Configuration.proxyEnabled = originalProxyEnabled;
    Configuration.fileDownload = originalFileDownload;
    Selenide.closeWebDriver();
  }
}
