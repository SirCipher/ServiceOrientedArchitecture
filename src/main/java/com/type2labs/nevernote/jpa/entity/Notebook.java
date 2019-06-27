package com.type2labs.nevernote.jpa.entity;


import com.type2labs.nevernote.validation.annotation.NotebookNameUnique;

import javax.persistence.*;
import java.util.List;

/**
 *
 */
@Entity
@NotebookNameUnique
@Table(indexes = {@Index(name = "unique_notebook_user", columnList = "title, creator_id", unique = true)})
public class Notebook {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    @ManyToOne
    private User creator;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "associatedNotebook", orphanRemoval = true)
    private List<Note> notes;

    public Notebook() {

    }

    public Notebook(Long id, String title, User creator) {
        this.id = id;
        this.title = title;
        this.creator = creator;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "Notebook{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creator=" + creator.toString() +
                '}';
    }

}
