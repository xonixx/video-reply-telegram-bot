package com.cmlteam.video_reply_telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.GetMeResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
  @Bean
  TelegramBotWrapper telegramBotWrapper(BotProperties botProperties, JsonHelper jsonHelper) {
    TelegramBot telegramBot = new TelegramBot(botProperties.getToken());
    GetMeResponse response = telegramBot.execute(new GetMe());
    if (response.user() == null) {
      throw new IllegalArgumentException("bot token is incorrect");
    }
    return new TelegramBotWrapper(telegramBot, jsonHelper);
  }

  @Bean
  VideosBackupper videosBackupper(
      BotProperties botProperties,
      TelegramBotWrapper telegramBotWrapper,
      VideosListProperties videosListProperties) {
    return new VideosBackupper(
        botProperties.getBackupFolder(),
        botProperties.getToken(),
        telegramBotWrapper,
        videosListProperties);
  }

  @Bean
  public BotPollingJob botPollingJob(
      BotProperties botProperties,
      TelegramBotWrapper telegramBotWrapper,
      VideosListService videosListService,
      VideosBackupper videosBackupper,
      JsonHelper jsonHelper) {
    return new BotPollingJob(
        telegramBotWrapper,
        videosListService,
        videosBackupper,
        jsonHelper,
        botProperties.getAdminUser());
  }
}
