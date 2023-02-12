package me.isayaksh.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.isayaksh.chatting.entity.Message;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Utils() {
    }

    public static Message getObject(final String message) throws Exception {
        return objectMapper.readValue(message, Message.class);
    }

    public static String getString(final Message message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}
