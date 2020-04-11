package com.cmlteam.video_reply_telegram_bot;

import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.replaceChars;

@Component
public class SearchStringMatcher {

  private final String RU_KEYBOARD = "йцукенгшщзхъфывапролджэячсмитьбюё";
  private final String EN_KEYBOARD = "qwertyuiop[]asdfghjkl;'zxcvbnm,.`";

  public boolean matches(String keyword, String query) {
    String keywordNormalized = normalize(keyword);
    String queryNormalized = normalize(query);

    return matches0(keywordNormalized, queryNormalized)
        || matches0(
            keywordNormalized, normalize(replaceChars(queryNormalized, EN_KEYBOARD, RU_KEYBOARD)))
        || matches0(
            keywordNormalized, normalize(replaceChars(queryNormalized, RU_KEYBOARD, EN_KEYBOARD)));
  }

  private boolean matches0(String keywordNormalized, String queryNormalized) {
    return queryNormalized.contains(keywordNormalized)
        || keywordNormalized.contains(queryNormalized);
  }

  private String normalize(String str) {
    if (str == null) {
      return null;
    }
    return replaceChars(str.toLowerCase(), "ё", "е");
  }
}
