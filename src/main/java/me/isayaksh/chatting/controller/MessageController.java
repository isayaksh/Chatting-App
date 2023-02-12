package me.isayaksh.chatting.controller;

import lombok.RequiredArgsConstructor;
import me.isayaksh.chatting.entity.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    /*
        /sub/channel/{channelId}    - 구독
        /pub/hello                  - 메시지 발행
    */

    @MessageMapping("/hello")
    public void message(Message message){
        simpMessageSendingOperations.convertAndSend("/sub/channel/" + message.getChannelId(), message);
    }
}
