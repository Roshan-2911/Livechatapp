package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Message;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.service.OnlineUserTracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserTracker tracker;
    @Autowired
    private ChatMessageRepository chatMessageRepository;


    public ChatController(SimpMessagingTemplate messagingTemplate, OnlineUserTracker tracker) {
        this.messagingTemplate = messagingTemplate;
        this.tracker = tracker;
    }

    // Receive chat messages from frontend
	/*
	 * @MessageMapping("/chat") public void chatMessage(Map<String, String> msg) {
	 * messagingTemplate.convertAndSend("/topic/messages", msg); }
	 */
    
    @MessageMapping("/chat")
    public void chatMessage(Map<String, String> msg) {
        // Save message to DB
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(msg.get("sender"));
        chatMessage.setReceiver(msg.get("receiver"));
        chatMessage.setContent(msg.get("content"));
        chatMessage.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(chatMessage);

        // Broadcast to subscribers
        messagingTemplate.convertAndSend("/topic/messages", msg);
    }

    

    // User logs in → add to online usersh
    @MessageMapping("/online")
    public void userOnline(Map<String, String> msg, StompHeaderAccessor accessor) {
        String user = msg.get("user");
        if (user != null) {
            tracker.addUser(user);
            accessor.getSessionAttributes().put("username", user);
            broadcastOnlineUsers();
        }
    }

    // Broadcast online users to all clients
    private void broadcastOnlineUsers() {
        Set<String> users = tracker.getOnlineUsers();
        messagingTemplate.convertAndSend("/topic/online", users);
    }

    // Handle disconnect → remove user from online list
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) accessor.getSessionAttributes().get("username");
        if (username != null) {
            tracker.removeUser(username);
            broadcastOnlineUsers();
        }
    }

    // Optional: store username in session when connect
    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        // Can store headers here if needed
    }
    
    
    
 // Load chat history between two users
    @GetMapping("/api/history/{user1}/{user2}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable String user1,
                                            @PathVariable String user2) {
        return chatMessageRepository.findHistory(user1, user2);
    }

    
}