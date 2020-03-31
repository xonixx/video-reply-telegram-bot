package com.cmlteam.video_reply_telegram_bot.services;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "s3")
@Getter
@Setter
@ToString(exclude = {"accessKey", "secretKey"})
public class AwsS3Props {
  private String bucket;
  private String accessKey;
  private String secretKey;
  private String region;
  private String endpoint;
}
