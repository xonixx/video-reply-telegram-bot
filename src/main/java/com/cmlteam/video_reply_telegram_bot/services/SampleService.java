package com.cmlteam.video_reply_telegram_bot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SampleService {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public SampleService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public String getDbVersion() {
    return jdbcTemplate.queryForObject("select version();", String.class);
  }
}
