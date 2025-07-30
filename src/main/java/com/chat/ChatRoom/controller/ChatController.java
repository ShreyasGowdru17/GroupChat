package com.chat.ChatRoom.controller;

import com.chat.ChatRoom.model.ChatMessage;
import com.chat.ChatRoom.service.FCMService;
import com.chat.ChatRoom.service.UserSessionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private  final FCMService fcmService;
    private final UserSessionService userSessionService;

    public ChatController(FCMService fcmService, UserSessionService userSessionService) {
        this.fcmService = fcmService;
        this.userSessionService = userSessionService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
        fcmService.sendMessageNotificationToOfflineUsers(chatMessage.getSender(), chatMessage.getContent());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        //add username in websocket session
        headerAccessor.getSessionAttributes().put("username",chatMessage.getSender());

        String fcmToken= chatMessage.getContent();
        userSessionService.addUser(chatMessage.getSender(), fcmToken);
        fcmService.sendUserJoinedNotificationToOfflineUsers(chatMessage.getSender());

        chatMessage.setContent(null);
        return chatMessage;
    }

    @MessageMapping("/chat.updateToken")
    public void updateFCMToken(@Payload ChatMessage chatMessage){
        userSessionService.updateUserTokens(chatMessage.getSender(), chatMessage.getContent());
    }
}
