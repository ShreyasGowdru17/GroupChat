package com.chat.ChatRoom.Config;

import com.chat.ChatRoom.model.ChatMessage;
import com.chat.ChatRoom.model.MessageType;
import com.chat.ChatRoom.service.FCMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;
    private final FCMService fcmService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
            StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
            String username=(String)headerAccessor.getSessionAttributes().get("username");
            if(username!=null){
                log.info("User disconnected:{}",username);

                fcmService.sendUserLeftNotificationToOfflineUsers(username);

                var chatMessage= ChatMessage.builder()
                        .type(MessageType.LEAVE)
                        .sender(username)
                        .build();
                messageTemplate.convertAndSend("/topic/public",chatMessage);
            }
    }
}
