package com.cmlteam.video_reply_telegram_bot;

import com.cmlteam.util.Util;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class VideosBackupper {
  private final String backupFolder;
  private final String token;
  private final TelegramBotWrapper telegramBot;
  private final VideosListProperties videosListProperties;

  @PostConstruct
  void postConstruct() {
    if (!new java.io.File(backupFolder).isDirectory()) {
      throw new IllegalArgumentException("Not a folder: " + backupFolder);
    }
  }

  @Async
  public void startBackup(long userToInform) {
    List<Video> videos = videosListProperties.getList();
    telegramBot.execute(
        new SendMessage(userToInform, "Starting backup for " + videos.size() + " videos..."));

    long t0 = System.currentTimeMillis();

    try {
      for (Video video : videos) {
        backupVideo(video);
      }
    } catch (Exception ex) {
      log.error("", ex);
      telegramBot.execute(new SendMessage(userToInform, "Exception: " + ex.toString()));
    }

    telegramBot.execute(
        new SendMessage(userToInform, "Done in " + Util.renderDurationFromStart(t0)));
  }

  @SneakyThrows
  private void backupVideo(Video video) {
    GetFileResponse fileResponse = telegramBot.execute(new GetFile(video.getFileId()));

    File file = fileResponse.file();

    String filePath = file.filePath();

    String videoUrl = formFileDlUrl(filePath);

    log.info("Downloading {} : {}... ", video.getFileId(), videoUrl);

    FileUtils.copyURLToFile(
        new URL(videoUrl),
        new java.io.File(backupFolder, video.getFileUniqueId() + ".mp4"),
        10_000,
        10_000);
  }

  private String formFileDlUrl(String filePath) {
    return String.format("https://api.telegram.org/file/bot%s/%s", token, filePath);
  }
}
