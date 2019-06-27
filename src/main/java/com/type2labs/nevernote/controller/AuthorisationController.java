package com.type2labs.nevernote.controller;

import com.type2labs.nevernote.controller.payload.ApiResponse;
import com.type2labs.nevernote.controller.payload.LoginRequest;
import com.type2labs.nevernote.controller.payload.RegisterRequest;
import com.type2labs.nevernote.jpa.entity.User;
import com.type2labs.nevernote.service.AuthorisationService;
import com.type2labs.nevernote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

/**
 * Handles all authorisation requests
 */
@RestController
@RequestMapping("/api/authorisation")
public class AuthorisationController {

    private final AuthorisationService authorisationService;
    private final NoteService noteService;

    @Autowired
    public AuthorisationController(AuthorisationService authorisationService, NoteService noteService) {
        this.authorisationService = authorisationService;
        this.noteService = noteService;
    }

    /**
     * Returns whether or not a given username is available. The username is lowercased upon processing
     *
     * @param username to check
     * @return if the username is available
     */
    @GetMapping("/usernameAvailable")
    public ResponseEntity<?> usernameAvailable(@RequestParam(value = "username") String username) {
        boolean available = authorisationService.usernameAvailable(username);
        return ResponseEntity.ok(new ApiResponse(available));
    }

    /**
     * Checks whether or not a given email address is available. The email address is lowercased upon processing
     *
     * @param email to check
     * @return if the email is available
     */
    @GetMapping("/emailAvailable")
    public ResponseEntity<?> emailAvailable(@RequestParam(value = "email") String email) {
        boolean available = authorisationService.emailAvailable(email);
        return ResponseEntity.ok(new ApiResponse(available));
    }

    /**
     * Registers a given user
     *
     * @param request to register
     * @return the user's notes location URI
     */
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User(request.getUserName(), request.getFirstName(), request.getLastName(), request.getEmailAddress(), request.getPassword());
        user = authorisationService.createUser(user);

        // Give the user a default NoteBook and Note
        noteService.initialiseForUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/notes/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Account created"));
    }

    /**
     * Login the requested user and generate their JWT
     *
     * @param request to process
     * @return their JWT in the response headers
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String jwt = authorisationService.login(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", "Bearer " + jwt);
        responseHeaders.set("Access-Control-Expose-Headers", "Authorization");

        return ResponseEntity.ok().headers(responseHeaders).body(new ApiResponse(true));
    }


}
