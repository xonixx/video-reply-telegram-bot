package com.cmlteam.video_reply_telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
  @Bean
  public TelegramBot telegramBot(BotProperties botProperties) {
    return new TelegramBot(botProperties.getToken());
  }
}
