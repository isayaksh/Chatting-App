package me.isayaksh.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.isayaksh.chatting.entity.ChatMessage;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Utils() {
    }

    public static ChatMessage getObject(final String message) throws Exception {
        return objectMapper.readValue(message, ChatMessage.class);
    }

    public static String getString(final ChatMessage chatMessage) throws Exception {
        return objectMapper.writeValueAsString(chatMessage);
    }
}
