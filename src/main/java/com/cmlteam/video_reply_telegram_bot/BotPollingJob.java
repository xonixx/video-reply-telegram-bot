package com.cmlteam.video_reply_telegram_bot;

import com.cmlteam.telegram_bot_common.JsonHelper;
import com.cmlteam.telegram_bot_common.LogHelper;
import com.cmlteam.telegram_bot_common.TelegramBotWrapper;
import com.pengrad.telegrambot.model.Video;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.InlineQueryResultCachedVideo;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class BotPollingJob {
  private final TelegramBotWrapper telegramBot;
  private final VideosListService videosListService;
  private final VideosBackupper videosBackupper;
  private final JsonHelper jsonHelper;
  private final LogHelper logHelper;
  private final long adminUser;

  private final GetUpdates getUpdates = new GetUpdates();

  @Scheduled(fixedRate = 400)
  public void processUpdates() {
    GetUpdatesResponse updatesResponse = telegramBot.execute(getUpdates);

    if (!updatesResponse.isOk()) {
      return;
    }

    List<Update> updates = updatesResponse.updates();

    for (Update update : updates) {
      logHelper.captureLogParams(update);

      log.info("Received:\n" + jsonHelper.toPrettyString(update));

      Message message = update.message();
      //      telegramBot.execute(new SendMessage(message.chat().id(), "" + update.updateId()));

      if (message != null) {
        Long chatId = message.chat().id();
        Integer messageId = message.messageId();

        String text = message.text();

        boolean handled = false;

        if (isAdminUser(message.from())) {
          if ("/backup".equals(text)) {
            handled = true;
            videosBackupper.startBackup(adminUser);
          } else {
            Video video = message.video();
            if (video != null) {
              displayVideoFileIds(chatId, video, messageId);
            }
          }
        } else {
          forwardMessageToAdmin(messageId, chatId);
        }

        if (!handled && StringUtils.isNoneEmpty(text)) {
          telegramBot.sendText(
              chatId,
              "This is inline bot to allow reply with video-meme!\n"
                  + "More instructions: https://github.com/xonixx/video-reply-telegram-bot/blob/master/README.md");
        }
      }

      InlineQuery inlineQuery = update.inlineQuery();

      if (inlineQuery != null) {
        String query = inlineQuery.query();
        String offset = inlineQuery.offset();

        VideosPage videosPage = videosListService.searchVideo(query, offset);

        //        log.info("offset: {}, nextOffset: {}", offset, videosPage.getNextOffset());

        List<InlineQueryResultCachedVideo> results = new ArrayList<>(videosPage.getVideos().size());
        for (com.cmlteam.video_reply_telegram_bot.Video v : videosPage.getVideos()) {
          results.add(
              new InlineQueryResultCachedVideo(
                  v.getFileUniqueId(), v.getFileId(), v.getKeywords().get(0)));
        }

        telegramBot.execute(
            update,
            new AnswerInlineQuery(inlineQuery.id(), results.toArray(new InlineQueryResult[0]))
                .nextOffset(videosPage.getNextOffset()));
      }

      getUpdates.offset(update.updateId() + 1);
    }
  }

  private void forwardMessageToAdmin(Integer messageId, Long chatId) {
    telegramBot.execute(new ForwardMessage(adminUser, chatId, messageId));
  }

  private boolean isAdminUser(User user) {
    return adminUser == user.id().longValue();
  }

  private void displayVideoFileIds(Long chatId, Video video, Integer messageId) {
    String fileId = video.fileId();
    String fileUniqueId = video.fileUniqueId();
    //        telegramBot.execute(new SendVideo(message.chat().id(), fileId).caption(fileId));
    telegramBot.sendText(
        chatId,
        "file-id: \""
            + fileId
            + "\"\nfile-unique-id: \""
            + fileUniqueId
            + "\"\nmessage-id: \""
            + messageId
            + "\"");
  }
}
