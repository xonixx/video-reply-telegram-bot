package com.cmlteam.video_reply_telegram_bot;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Video {
  private String fileId;
  private String fileUniqueId;
  private List<String> keywords;

  public boolean matches(String query) {
    String queryLc = query.toLowerCase();
    return keywords.stream()
        .anyMatch(s -> queryLc.contains(s) || s.contains(queryLc) || "*".equals(queryLc));
  }
}
