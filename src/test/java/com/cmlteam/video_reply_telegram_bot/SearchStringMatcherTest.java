package com.cmlteam.video_reply_telegram_bot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchStringMatcherTest {
  private final SearchStringMatcher searchStringMatcher = new SearchStringMatcher();

  @Test
  void testMatchesEmpty() {
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", ""));
  }

  @Test
  void basicTests() {
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полномочия"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полномочия всё"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полномочия все"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "полном"));

    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полномочия"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полномочия всё"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полномочия все"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полном"));

    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полноМочИЯ"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "полномочия всЁ"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "ПОЛНомочия все"));
    assertTrue(searchStringMatcher.matches("наши полномочия всё окончены", "ПОЛНом"));
  }

  @Test
  void testMatchesWrongKeyboard() {
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "gjkyjvjxbz"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "gjKYjVjXBz"));
    assertTrue(searchStringMatcher.matches("наши полномочия все окончены", "gjKYjVjXBz DCt"));
    assertTrue(searchStringMatcher.matches("wtf", "цеа"));
  }
}
