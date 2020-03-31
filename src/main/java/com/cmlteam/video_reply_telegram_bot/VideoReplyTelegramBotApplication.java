package com.cmlteam.video_reply_telegram_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = "com.cmlteam")
public class VideoReplyTelegramBotApplication {

  public static void main(String[] args) {
    SpringApplication.run(VideoReplyTelegramBotApplication.class, args);
  }

  @PostConstruct
  public void doAtStartup() {
    // Insert here post-start action if need be
  }
}
