package com.cmlteam.video_reply_telegram_bot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchStringMatcherTest {
  private final SearchStringMatcher searchStringMatcher = new SearchStringMatcher();

  @Test
  void tests() {
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полномочия"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полномочия всё"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полномочия все"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полном"));
    
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полномочия"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полномочия всё"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полномочия все"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полном"));
  }
}
