package com.cmlteam.video_reply_telegram_bot.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class FileName {
  private final String base;
  private final String ext;
  private final int lenLimit;

  /**
   * @param name filename
   * @param lenLimit 0 = no limit
   */
  public FileName(String name, int lenLimit) {
    if (lenLimit < 3 && lenLimit != 0) {
      throw new IllegalArgumentException("lenLimit must be greater or equal 3 or 0");
    }
    this.base = FilenameUtils.removeExtension(name);
    this.ext = FilenameUtils.getExtension(name);
    this.lenLimit = lenLimit;
  }

  public String getName() {
    return getName("");
  }

  public String getName(String suffix) {
    return crop(base + suffix + (ext.length() > 0 ? '.' + ext : ""));
  }

  private String crop(String newName) {
    if (lenLimit > 0 && newName.length() > lenLimit) {
      String newNameBase = FilenameUtils.removeExtension(newName);
      String newNameExt = FilenameUtils.getExtension(newName);
      int extLimit = lenLimit - 2;
      if (newNameExt.length() > extLimit) {
        newNameExt = newNameExt.substring(0, extLimit);
      }
      int baseLimit = lenLimit - newNameExt.length() - 1;
      newNameBase = newNameBase.substring(0, baseLimit);
      if (StringUtils.isNotBlank(newNameExt)) {
        return newNameBase + "." + newNameExt;
      } else {
        return newNameBase;
      }
    }
    return newName;
  }
}
