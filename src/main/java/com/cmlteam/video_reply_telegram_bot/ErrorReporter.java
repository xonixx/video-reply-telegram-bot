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
  public static final int ERROR_REPORT_INTERVAL = 5000; // test
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

          StringBuilder errSb = new StringBuilder();
          renderError(errSb, error);

          if (msg.length() + errSb.length() > MAX_MSG_LEN) {
            break;
          } else {
            msg.append(errSb);
          }
        }

        SendResponse response =
            telegramBot.execute(
                new SendMessage(adminUser, msg.toString()).parseMode(ParseMode.HTML));

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
      msg.append("<b>Code:</b> ").append(errorCode).append("\n");
    }

    String description = error.getDescription();
    if (StringUtils.isNotBlank(description)) {
      msg.append("<b>Description:</b> ").append(Util.trim(description, 200)).append("\n");
    }

    String request = error.getRequest();
    if (request != null) {
      msg.append("<b>Request:</b> \n")
          .append("<pre>")
          .append(Util.trim(request, 200))
          .append("</pre>\n");
    }

    Exception ex = error.getException();
    if (ex != null) {
      msg.append("<b>Exc:</b> ")
          .append(ex.toString())
          .append("\n<pre>")
          .append(Util.trim(ExceptionUtils.getStackTrace(ex), 200))
          .append("</pre>");
    }
  }
}
