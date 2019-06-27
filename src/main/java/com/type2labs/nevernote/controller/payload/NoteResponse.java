package com.type2labs.nevernote.controller.payload;

public class NoteResponse {

    private Long id;
    private String title;
    private String notebookName;

    public NoteResponse(Long id, String title, String notebookName) {
        this.id = id;
        this.title = title;
        this.notebookName = notebookName;
    }

    public String getNotebookName() {
        return notebookName;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
