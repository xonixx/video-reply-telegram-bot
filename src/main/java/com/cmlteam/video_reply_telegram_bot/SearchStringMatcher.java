package com.cmlteam.video_reply_telegram_bot;

import org.springframework.stereotype.Component;

@Component
public class SearchStringMatcher {

  public boolean matches(String keyword, String query) {
    String keywordNormalized = normalize(keyword);
    String queryNormalized = normalize(query);

    return queryNormalized.contains(keywordNormalized)
        || keywordNormalized.contains(queryNormalized);
  }

  private String normalize(String str) {
    if (str == null) {
      return null;
    }
    return str.toLowerCase().replace("ё", "е");
  }
}
