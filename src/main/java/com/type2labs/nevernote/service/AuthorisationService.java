package com.type2labs.nevernote.service;

import com.type2labs.nevernote.controller.payload.LoginRequest;
import com.type2labs.nevernote.exception.ApplicationException;
import com.type2labs.nevernote.jpa.entity.Role;
import com.type2labs.nevernote.jpa.entity.RoleName;
import com.type2labs.nevernote.jpa.entity.User;
import com.type2labs.nevernote.jpa.respository.RoleRepository;
import com.type2labs.nevernote.jpa.respository.UserRepository;
import com.type2labs.nevernote.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthorisationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider provider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorisationService(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtTokenProvider provider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.provider = provider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns whether or not an username is available
     *
     * @param username to check
     * @return if it is available
     */
    public boolean usernameAvailable(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        } else {
            return !userRepository.existsByUsername(username.toLowerCase());
        }
    }

    /**
     * Returns whether or not an email address is available
     *
     * @param email to check
     * @return if it is available
     */
    public boolean emailAvailable(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        } else {
            return !userRepository.existsByEmailAddress(email.toLowerCase());
        }
    }

    /**
     * Performs a login request and returns the generated token
     *
     * @param request to process
     * @return a generated JWT
     */
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return provider.generateToken(authentication);
    }

    /**
     * Checks if the username's email address or username is already taken
     *
     * @param user to validate
     */
    private void validateUser(User user) {
        if (userRepository.existsByEmailAddress(user.getEmailAddress())) {
            throw new ApplicationException("Email address: " + user.getEmailAddress() + " is taken");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ApplicationException("Username: " + user.getUsername() + " is taken");
        }
    }

    /**
     * Validates and saves the current user
     *
     * @param user to save
     * @return the saved user
     */
    public User createUser(User user) {
        validateUser(user);

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new ApplicationException("User Role not set."));
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmailAddress(user.getEmailAddress().toLowerCase());
        user.setAvailableRoles(Collections.singleton(userRole));

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

}
