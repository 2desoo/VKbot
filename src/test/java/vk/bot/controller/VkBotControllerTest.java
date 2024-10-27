package vk.bot.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VkBotController.class)
public class VkBotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleCallback_Confirmation() throws Exception {
        String confirmationCode = "b43df98c";
        String requestBody = "{\"type\":\"confirmation\"}";

        mockMvc.perform(post("/callback")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(confirmationCode));
    }

    @Test
    public void testHandleNewMessage() throws Exception {
        String requestBody = "{\"type\":\"message_new\",\"object\":{\"message\":{\"from_id\":481874267,\"text\":\"Привет\"}}}";

        mockMvc.perform(post("/callback")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    public void testHandleCallback_InvalidJson() throws Exception {
        String requestBody = "{invalidJson}";

        mockMvc.perform(post("/callback")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid JSON format"));
    }

    @Test
    public void testHandleCallback_UnknownType() throws Exception {
        String requestBody = "{\"type\":\"unknown_type\"}";

        mockMvc.perform(post("/callback")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Неправильный запрос"));
    }

    @Test
    public void testHandleCallback_InvalidMessageStructure() throws Exception {
        String requestBody = "{\"type\":\"message_new\",\"object\":{}}";

        mockMvc.perform(post("/callback")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}