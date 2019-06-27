package com.type2labs.nevernote.controller;

import com.type2labs.nevernote.controller.payload.UserSummary;
import com.type2labs.nevernote.security.CurrentUser;
import com.type2labs.nevernote.security.UserPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles all user requests
 */
@RestController
@RequestMapping("/api")
public class UserController {

    /**
     * Return the authenticated user
     *
     * @param currentUser to return
     * @return the user
     */
    @GetMapping("/user/me")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName() + " " + currentUser.getLastName());
    }
}