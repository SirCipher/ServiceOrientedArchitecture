package com.type2labs.nevernote.jpa.entity;

import javax.persistence.*;

@Entity
public class SharedNotebook {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private User user;
    @OneToOne
    private Notebook notebook;
    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    public SharedNotebook() {

    }

    public SharedNotebook(User user, Notebook notebook, AccessLevel accessLevel) {
        this.user = user;
        this.notebook = notebook;
        this.accessLevel = accessLevel;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
