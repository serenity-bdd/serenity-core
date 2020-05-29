package net.serenitybdd.core;

import java.util.Locale;
public class CurrentOS {

  public enum OSType {
    windows, mac, linux, other
  };

  private static OSType detectedOS;

  /**
   * detect the operating system from the os.name System property and cache
   * the result
   * 
   * @returns - the operating system detected
   */
  public static OSType getType() {
    if (detectedOS == null) {
      String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      if ((OS.contains("mac")) || (OS.contains("darwin"))) {
        detectedOS = OSType.mac;
      } else if (OS.contains("win")) {
        detectedOS = OSType.windows;
      } else if (OS.contains("nux")) {
        detectedOS = OSType.linux;
      } else {
        detectedOS = OSType.other;
      }
    }
    return detectedOS;
  }

  public static boolean isWindows() {
    return getType() == OSType.windows;
  }
}