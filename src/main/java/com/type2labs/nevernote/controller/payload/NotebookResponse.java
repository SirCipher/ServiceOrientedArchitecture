package com.type2labs.nevernote.controller.payload;


import java.util.ArrayList;
import java.util.List;

public class NotebookResponse {

    private Long id;
    private String title;
    private String accessLevel;
    private List<NoteResponse> notes = new ArrayList<>();

    public NotebookResponse(Long id, String title, String accessLevel) {
        this.id = id;
        this.title = title;
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<NoteResponse> getNotes() {
        return notes;
    }

    public void addNote(NoteResponse noteResponse) {
        this.notes.add(noteResponse);
    }
}
