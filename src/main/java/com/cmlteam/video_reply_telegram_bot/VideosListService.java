package com.cmlteam.video_reply_telegram_bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideosListService {
  private final VideosListProperties videosListProperties;

  /**
   * @param query user query string
   * @return list of file_ids of videos stored in telegram
   */
  List<Video> searchVideo(String query) {
    return videosListProperties.getList().stream()
        .filter(v -> v.matches(query))
        .collect(Collectors.toList());
  }

  void init() {}

  @PostConstruct
  public void postConstruct() {
    log.info("Videos: " + videosListProperties.getList().size());
    init();
  }
}
