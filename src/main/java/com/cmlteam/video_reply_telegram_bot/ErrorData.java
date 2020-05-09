package com.cmlteam.video_reply_telegram_bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class ErrorData {
  private final int errorCode;
  private final String description;
  private final String request;
  private final Exception exception;
}
