package com.cmlteam.video_reply_telegram_bot;

import com.cmlteam.util.Util;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
class ErrorReporter {
  private final TelegramBot telegramBot;
  private final long adminUser;
  private final List<ErrorData> errors = Collections.synchronizedList(new ArrayList<>());

  //  public static final int ERROR_REPORT_INTERVAL = 5 * 60 * 1000; // 5 min
  public static final int ERROR_REPORT_INTERVAL = 200; // test
  private static final int MAX_MSG_LEN = 4096;

  void reportError(ErrorData error) {
    errors.add(error);
  }

  @Scheduled(fixedRate = ERROR_REPORT_INTERVAL)
  void reportJob() {
    if (!errors.isEmpty()) {
      try {
        StringBuilder msg = new StringBuilder();

        msg.append("Received ")
            .append(errors.size())
            .append(" errors during last ")
            .append(Util.renderDuration(ERROR_REPORT_INTERVAL))
            .append(":");

        for (ErrorData error : errors) {
          msg.append("\n");
          renderError(msg, error);

          if (msg.length() > MAX_MSG_LEN) {
            break;
          }
        }

        SendResponse response =
            telegramBot.execute(
                new SendMessage(adminUser, Util.trim(msg.toString(), MAX_MSG_LEN - 3))
                    .parseMode(ParseMode.MarkdownV2));

        if (!response.isOk()) {
          log.error(
              "Error sending logs to admin: {}: {}", response.errorCode(), response.description());
        }

      } finally {
        errors.clear();
      }
    }
  }

  private void renderError(StringBuilder msg, ErrorData error) {
    int errorCode = error.getErrorCode();
    if (errorCode != 0) {
      msg.append("*Code:* ").append(errorCode).append("\n");
    }

    String description = error.getDescription();
    if (StringUtils.isNotBlank(description)) {
      msg.append("*Description:* ").append(description).append("\n");
    }

    String request = error.getRequest();
    if (request != null) {
      msg.append("*Request:* \n").append("```\n").append(request).append("\n```\n");
    }

    Exception ex = error.getException();
    if (ex != null) {
      msg.append("*Exc:* ")
          .append(ex.toString())
          .append("\n```\n")
          .append(ExceptionUtils.getStackTrace(ex))
          .append("\n```");
    }
  }
}
