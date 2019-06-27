package com.type2labs.nevernote.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User entity
 */
@Entity
@Table(indexes = {@Index(name = "unique_email", columnList = "emailAddress", unique = true)})
public class User {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    @Pattern(regexp = "^([a-zA-Z0-9]){1,16}$", message = "Username can only contain letters, numbers and must be less than 16 characters or less")
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Email
    private String emailAddress;
    @Lob
    private String password;

    @OneToMany(mappedBy = "creator", orphanRemoval = true)
    private List<Notebook> notebooks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> availableRoles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<SharedNotebook> notebooksSharedWith;

    public User() {

    }

    public User(String username, String firstName, String lastName, String emailAddress, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public User(Long id, String username, String firstName, String lastName, String emailAddress, String password, List<Notebook> notebooks) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.notebooks = notebooks;
    }

    public List<SharedNotebook> getNotebooksSharedWith() {
        return notebooksSharedWith;
    }

    public void setNotebooksSharedWith(List<SharedNotebook> notebooksSharedWith) {
        this.notebooksSharedWith = notebooksSharedWith;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getAvailableRoles() {
        return availableRoles;
    }

    public void setAvailableRoles(Set<Role> availableRoles) {
        this.availableRoles = availableRoles;
    }

    public void addNotebook(Notebook notebook) {
        if (this.notebooks == null) {
            this.notebooks = new ArrayList<>();
        }

        this.notebooks.add(notebook);
    }

    public List<Notebook> getNotebooks() {
        return notebooks;
    }

    public void setNotebooks(List<Notebook> notebooks) {
        this.notebooks = notebooks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}
