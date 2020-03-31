package com.cmlteam.video_reply_telegram_bot;

import com.cmlteam.video_reply_telegram_bot.controllers.MainController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private DataSource dataSource;

  @Before
  public void setup() {}

  @Test
  public void simpleGetTest() throws Exception {
    mockMvc
        .perform(get("/test"))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo("Hello CML!")));
  }

  @Test
  public void simplePostTest() throws Exception {
    mockMvc
        .perform(
            post("/testpost")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"payload\": {\"test\": \"test\"}}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"success\": true}"));
  }
}
