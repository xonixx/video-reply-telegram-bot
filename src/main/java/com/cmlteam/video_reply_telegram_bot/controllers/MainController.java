package com.cmlteam.video_reply_telegram_bot.controllers;

import com.cmlteam.video_reply_telegram_bot.model.ServerStatus;
import com.cmlteam.video_reply_telegram_bot.services.SampleService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MainController {

  private final SampleService sampleService;

  @ApiOperation(value = "Test GET endpoint")
  @GetMapping(value = "test")
  public String test() {
    return "Hello CML!";
  }

  @GetMapping(value = "testws")
  public String testws() {
    RestTemplate template = new RestTemplate();
    ServerStatus status =
        Objects.requireNonNull(
            template.getForObject(
                "https://l2c1x1.com/services/misc/server-stats", ServerStatus.class));
    return "" + status.getTotalAccounts();
  }

  @ApiOperation(value = "Show the DB version")
  @GetMapping(value = "testdb")
  public String testdb() {
    return sampleService.getDbVersion();
  }

  @ApiOperation(value = "Test POST endpoint")
  @PostMapping(value = "/testpost")
  public Map testPost(@RequestBody Map payload) {
    log.info("Test POST: {}", payload);
    HashMap res = new HashMap();
    res.put("success", true);
    return res;
  }
}
