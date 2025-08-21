package com.example.demo.Controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class PresenceController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();

        return userRepository.findByUsernameAndPassword(username, password).map(user -> {
            onlineUsers.add(username);
            messagingTemplate.convertAndSend("/topic/onlineUsers", onlineUsers);
            response.put("status", "success");
            response.put("username", username);
            return response;
        }).orElseGet(() -> {
            response.put("status", "fail");
            return response;
        });
    }

    @PostMapping("/logout")
    public void logout(@RequestParam String username) {
        onlineUsers.remove(username);
        messagingTemplate.convertAndSend("/topic/onlineUsers", onlineUsers);
    }
}