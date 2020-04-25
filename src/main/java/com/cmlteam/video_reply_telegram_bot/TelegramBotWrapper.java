package com.cmlteam.video_reply_telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TelegramBotWrapper {
  private final TelegramBot telegramBot;
  private final JsonHelper jsonHelper;

  public <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
    R response = telegramBot.execute(request);
    if (!response.isOk()) {
      log.error(
          "ERROR #{}: {} for request:\n{}",
          response.errorCode(),
          response.description(),
          jsonHelper.toPrettyString(request.toWebhookResponse()));
    }
    return response;
  }
}
