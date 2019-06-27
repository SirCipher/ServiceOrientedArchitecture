package com.type2labs.nevernote.controller.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * User sign up request payload
 */
public class RegisterRequest {

    @NotBlank
    @Size(min = 1, max = 30)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 30)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^([a-zA-Z0-9]){1,16}$", message = "Username can only contain letters, numbers and must be less than 16 characters or less")
    private String userName;

    @NotBlank
    @Email
    private String emailAddress;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
