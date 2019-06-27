package com.type2labs.nevernote.service;

import com.type2labs.nevernote.jpa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    void notifyUser(User user, String message) {
        messagingTemplate.convertAndSendToUser(user.getUsername(), "/topics/all", message);
    }

}
