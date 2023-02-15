package me.isayaksh.chatting.handler;

import lombok.extern.slf4j.Slf4j;
import me.isayaksh.chatting.Utils;
import me.isayaksh.chatting.entity.ChatMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.isayaksh.chatting.entity.MessageType.JOIN;
import static me.isayaksh.chatting.entity.MessageType.LEAVE;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(); // key : 세션 ID, value : 세션

    /** 웹소켓 연결 **/
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocketHandler.afterConnectionEstablished");
        /** 세션 저장 **/
        String sessionId = session.getId(); // UUID 로 생성
        sessions.put(sessionId, session);

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sessionId)
                .channelId("all").build();
        chatMessage.setType(JOIN);

        /** 모든 세션에 입장을 알림 **/
       sessions.values().forEach(s -> {
            try {
                if(!s.getId().equals(sessionId)){
                    s.sendMessage(new TextMessage(Utils.getString(chatMessage)));
                }
            } catch (Exception e){
                //TODO : throw
            }
        });
    }

    /** 데이터 통신 **/
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        log.info("WebSocketHandler.handleTextMessage");
        ChatMessage chatMessage = Utils.getObject(textMessage.getPayload()); // textMessage.payload → Message
        chatMessage.setSender(session.getId()); // 송신자 정보 갱신
        
        /** receiver 가 존재하고, 현재 연결된 상태라 메시지 전송 **/
        WebSocketSession receiver = sessions.get(chatMessage.getChannelId()); // 수신자 정보 추출
        if (receiver != null && receiver.isOpen()){
            receiver.sendMessage(new TextMessage(Utils.getString(chatMessage)));
        }

        super.handleTextMessage(session, textMessage);
    }
    
    /** 웹소켓 연결 종료 **/
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocketHandler.afterConnectionClosed");
        String sessionId = session.getId();
        sessions.remove(sessionId); // 세션 저장소에서 연결이 끊긴 사용자를 삭제한다.

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(LEAVE);
        chatMessage.setSender(sessionId);

        sessions.values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(Utils.getString(chatMessage)));
            } catch (Exception e){
                //TODO: throw
            }
        });
    }

    /** 웹소켓 통신 에러 **/
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }
}
