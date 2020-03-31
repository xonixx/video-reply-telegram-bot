package com.cmlteam.video_reply_telegram_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Video;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendVideo;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotPollingJob {
  private final TelegramBot telegramBot;
  private final VideosListProperties videosListProperties;

  private final GetUpdates getUpdates = new GetUpdates();

  @Scheduled(fixedRate = 400)
  public void processUpdates() {
    GetUpdatesResponse updatesResponse = telegramBot.execute(getUpdates);

    List<Update> updates = updatesResponse.updates();

    for (Update update : updates) {
      log.info("Received:\n" + toPrettyString(update));

      Message message = update.message();
      //      telegramBot.execute(new SendMessage(message.chat().id(), "" + update.updateId()));

      Video video = message.video();
      if (video != null) {
        String fileId = video.fileId();
        //        telegramBot.execute(new SendVideo(message.chat().id(), fileId).caption(fileId));
        telegramBot.execute(new SendMessage(message.chat().id(), fileId));
      }

      getUpdates.offset(update.updateId() + 1);
    }
  }

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private String toPrettyString(Object obj) {
    return gson.toJson(obj);
  }

  @PostConstruct
  public void postConstruct() {
    log.info("Videos: " + videosListProperties.getList().size());
  }
}
