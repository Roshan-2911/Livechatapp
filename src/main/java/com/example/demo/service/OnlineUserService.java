package com.example.demo.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserService {
    private final Set<String> onlineUsers = new HashSet<>();
    private final SimpMessagingTemplate messagingTemplate;

    public OnlineUserService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void userLogin(String username) {
        onlineUsers.add(username);
        broadcastOnlineUsers();

        // send current list only to the new user also
        messagingTemplate.convertAndSendToUser(username, "/queue/users", onlineUsers);
    }

    public void userLogout(String username) {
        onlineUsers.remove(username);
        broadcastOnlineUsers();
    }

    private void broadcastOnlineUsers() {
        messagingTemplate.convertAndSend("/topic/onlineUsers", onlineUsers);
    }
}
