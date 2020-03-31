package com.cmlteam.video_reply_telegram_bot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram-bot")
@Getter
@Setter
public class BotProperties {
  private String adminUser;
  private String token;
}
