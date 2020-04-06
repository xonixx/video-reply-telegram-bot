package com.cmlteam.video_reply_telegram_bot;

import com.cmlteam.util.Util;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class VideosBackupper {
  private final String backupFolder;
  private final TelegramBotWrapper telegramBot;
  private final VideosListProperties videosListProperties;

  @Async
  public void startBackup(long userToInform) {
    List<Video> videos = videosListProperties.getList();
    long t0 = System.currentTimeMillis();
    telegramBot.execute(
        new SendMessage(userToInform, "Starting backup for " + videos.size() + " videos"));

    telegramBot.execute(
        new SendMessage(userToInform, "Done in " + Util.renderDurationFromStart(t0)));
  }
}
