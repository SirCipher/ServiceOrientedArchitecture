package com.type2labs.nevernote.controller.payload;

public class NoteContentPayload {

    private Long id;
    private String html;
    private String notebookName;
    private String accessLevel;

    public NoteContentPayload(Long id, String html, String notebookName, String accessLevel) {
        this.id = id;
        this.html = html;
        this.notebookName = notebookName;
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public String getNotebookName() {
        return notebookName;
    }
}
