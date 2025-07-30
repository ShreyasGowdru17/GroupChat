package com.chat.ChatRoom.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ChatMessage {

    private String sender;
    private String content;
    private MessageType type;
    private String fcmToken;
    private long timeStamp;

    public ChatMessage(String sender, String content, MessageType type, String fcmToken, long timeStamp) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timeStamp = System.currentTimeMillis();
    }
}
