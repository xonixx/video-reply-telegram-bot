package com.cmlteam.video_reply_telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotPollingJob {
  private TelegramBot telegramBot;

  @Scheduled(fixedRate = 400)
  public void processUpdates() {
    System.out.println("processUpdates...");
  }
}
