package com.chat.ChatRoom.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class UserSessionService {

    private final ConcurrentHashMap<String,String> userTokens=new ConcurrentHashMap<>();

    private final Set<String> online=new CopyOnWriteArraySet<>();

    public void addUser(String username,String fcmToken){
        if(fcmToken!=null && !fcmToken.isEmpty()){
            userTokens.put(username,fcmToken);
        }
        online.add(username);
    }

    public void removeUser(String username){
        online.remove(username);
    }

    public boolean isOnline(String username){
        return online.contains(username);
    }

    public String getUserToken(String username){
        return userTokens.get(username);
    }

    public Set<String> getOnline(){
        return new CopyOnWriteArraySet<>(online);
    }

    public Set<String> getAllUsers(){
        return userTokens.keySet();
    }

    public void updateUserTokens(String username,String fcmToken){
        if(fcmToken!=null && !fcmToken.isEmpty()){
            userTokens.put(username,fcmToken);
        }
    }
}
