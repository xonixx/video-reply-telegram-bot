package com.cmlteam.video_reply_telegram_bot;

import com.cmlteam.util.Util;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class VideosBackupper {
  private final String backupFolder;
  private final String token;
  private final TelegramBotWrapper telegramBot;
  private final VideosListProperties videosListProperties;

  @Async
  public void startBackup(long userToInform) {
    List<Video> videos = videosListProperties.getList();
    telegramBot.execute(
        new SendMessage(userToInform, "Starting backup for " + videos.size() + " videos..."));

    long t0 = System.currentTimeMillis();

    for (Video video : videos) {
      backupVideo(video);
    }

    telegramBot.execute(
        new SendMessage(userToInform, "Done in " + Util.renderDurationFromStart(t0)));
  }

  private void backupVideo(Video video) {
    GetFileResponse fileResponse = telegramBot.execute(new GetFile(video.getFileId()));

    File file = fileResponse.file();

    String filePath = file.filePath();

    log.info(formFileDlUrl(filePath));
  }

  private String formFileDlUrl(String filePath) {
    return String.format("https://api.telegram.org/file/bot%s/%s", token, filePath);
  }
}
