package com.example.demo.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;

@Controller
public class ChatWebSocketController {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public ChatWebSocketController(ChatMessageRepository chatMessageRepository,SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
        
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        // ✅ Save message into DB
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }
    @MessageMapping("/signal")
    public void handleSignal(Principal principal, Map<String, Object> msg) {
        String target = (String) msg.get("target");
       // messagingTemplate.convertAndSendToUser(target, "/topic/signal", msg);
        messagingTemplate.convertAndSend("/topic/signal/" + target, msg);

    }
}