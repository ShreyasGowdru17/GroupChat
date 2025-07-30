package com.chat.ChatRoom.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class FCMService {

    private final UserSessionService userSessionService;

    public FCMService(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }
    
    public void sendMessageNotificationToOfflineUsers(String senderName,String messageContent){

        Set<String> allUsers=userSessionService.getAllUsers();
        
        for(String username:allUsers){
            if(!username.equals(senderName) && !userSessionService.isOnline(username)){
                String token=userSessionService.getUserToken(username);
                if(token!=null){
                    sendMessageNotification(token,senderName,messageContent);
                }
            }
        }
    }

    public void sendUserJoinedNotificationToOfflineUsers(String joinedUser){
        Set<String> allUsers=userSessionService.getAllUsers();

        for(String username:allUsers){
            if(!username.equals(joinedUser) && !userSessionService.isOnline(username) ){
                String token=userSessionService.getUserToken(username);
                if(token!=null){
                    sendMessageNotification(token,joinedUser,"Joined");
                }
            }
        }
    }

    public void sendUserLeftNotificationToOfflineUsers(String leftUser){
        Set<String> allUsers=userSessionService.getAllUsers();

        for(String username:allUsers){
            if(!username.equals(leftUser) && !userSessionService.isOnline(username) ){
                String token=userSessionService.getUserToken(username);
                if(token!=null){
                    sendMessageNotification(token,leftUser,"Left");
                }
            }
        }
    }

    private void sendMessageNotification(String token, String senderName, String messageContent) {
        try{
            Map<String,String> data=new HashMap<>();
            data.put("type","message");
            data.put("sender",senderName);
            data.put("content",messageContent);

            Message message=Message.builder()
                    .setToken(token)
                    .setNotification(new Notification("New message from",senderName+" "+messageContent))
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                    .build())
                            .build())
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .setIcon("/icon-1192x192.png")
                                    .setBadge("/badge-72x72.png")
                                    .build())
                            .build())
                    .build();

            String response=FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message notification:{}",response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending message notification",e);
        }
    }

    private void sendUserStatusNotification(String token, String senderName, String messageContent) {
        try{
            Map<String,String> data=new HashMap<>();
            data.put("type","status");
            data.put("sender",senderName);
            data.put("content",messageContent);

            Message message=Message.builder()
                    .setToken(token)
                    .setNotification(new Notification("Chat Room Update",senderName+" "+messageContent))
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                    .build())
                            .build())
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .setIcon("/icon-192x192.png")
                                    .setBadge("/badge-72x72.png")
                                    .build())
                            .build())
                    .build();

            String response=FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent user status notification:{}",response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending set user status notification",e);
        }
    }
}
