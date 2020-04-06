package com.cmlteam.video_reply_telegram_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Video;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.InlineQueryResultCachedVideo;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class BotPollingJob {
  private final TelegramBotWrapper telegramBot;
  private final VideosListService videosListService;
  private final VideosBackupper videosBackupper;
  private final long adminUser;

  private final GetUpdates getUpdates = new GetUpdates();

  @Scheduled(fixedRate = 400)
  public void processUpdates() {
    GetUpdatesResponse updatesResponse = telegramBot.execute(getUpdates);

    List<Update> updates = updatesResponse.updates();

    for (Update update : updates) {
      log.info("Received:\n" + toPrettyString(update));

      Message message = update.message();
      //      telegramBot.execute(new SendMessage(message.chat().id(), "" + update.updateId()));

      if (message != null) {
        Long chatId = message.chat().id();

        if (isAdminUser(chatId)) {
          String text = message.text();
          if ("/backup".equals(text)) {
             videosBackupper.startBackup(adminUser);
          } else {
            Video video = message.video();
            if (video != null) {
              displayVideoFileIds(chatId, video);
            }
          }
        } else {
          forwardMessageToAdmin(message.messageId(), chatId);
        }
      }

      InlineQuery inlineQuery = update.inlineQuery();

      if (inlineQuery != null) {
        String query = inlineQuery.query();

        List<com.cmlteam.video_reply_telegram_bot.Video> fileIds =
            videosListService.searchVideo(query);

        telegramBot.execute(
            new AnswerInlineQuery(
                inlineQuery.id(),
                fileIds.stream()
                    .map(
                        v ->
                            new InlineQueryResultCachedVideo(
                                v.getFileUniqueId(), v.getFileId(), v.getKeywords().get(0)))
                    .toArray(InlineQueryResult[]::new)));
      }

      getUpdates.offset(update.updateId() + 1);
    }
  }

  private void forwardMessageToAdmin(Integer messageId, Long chatId) {
    telegramBot.execute(new ForwardMessage(adminUser, chatId, messageId));
  }

  private boolean isAdminUser(Long chatId) {
    return adminUser == chatId;
  }

  private void displayVideoFileIds(Long chatId, Video video) {
    String fileId = video.fileId();
    String fileUniqueId = video.fileUniqueId();
    //        telegramBot.execute(new SendVideo(message.chat().id(), fileId).caption(fileId));
    telegramBot.execute(
        new SendMessage(
            chatId, "file-id: \"" + fileId + "\"\nfile-unique-id: \"" + fileUniqueId + "\""));
  }

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private String toPrettyString(Object obj) {
    return gson.toJson(obj);
  }
}
