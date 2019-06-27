package com.type2labs.nevernote.jpa.entity;

import javax.persistence.*;

/**
 *
 */
@Entity
public class Note {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    @Lob
    private String html;
    @ManyToOne
    @JoinColumn(name = "associatedNotebook_id")
    private Notebook associatedNotebook;

    private transient AccessLevel accessLevel;

    public Note() {

    }

    public Note(Long id, String title, String html, Notebook associatedNotebook) {
        this.id = id;
        this.title = title;
        this.html = html;
        this.associatedNotebook = associatedNotebook;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Notebook getAssociatedNotebook() {
        return associatedNotebook;
    }

    public void setAssociatedNotebook(Notebook associatedNotebook) {
        this.associatedNotebook = associatedNotebook;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
