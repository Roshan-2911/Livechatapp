package com.example.demo.repository;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSender(String sender, String receiver, String r1, String r2);

    @Query("""
            SELECT m FROM ChatMessage m
            WHERE (m.sender = :u1 AND m.receiver = :u2)
               OR (m.sender = :u2 AND m.receiver = :u1)
            ORDER BY m.timestamp
        """)
        List<ChatMessage> findHistory(@Param("u1") String user1,
                                      @Param("u2") String user2);
    
}
