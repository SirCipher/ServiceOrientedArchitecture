package com.type2labs.nevernote.controller;

import com.type2labs.nevernote.controller.payload.ApiResponse;
import com.type2labs.nevernote.controller.payload.NoteContentPayload;
import com.type2labs.nevernote.jpa.entity.Note;
import com.type2labs.nevernote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles all note requests
 */
@RestController
@RequestMapping("/api/notes/")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Return a given note by id
     *
     * @param id to fetch
     * @return requested notebook and contents
     */
    @GetMapping("query")
    public NoteContentPayload getNote(@RequestParam(value = "id") Long id) {
        Note note = noteService.getNote(id);
        return new NoteContentPayload(note.getId(), note.getHtml(), note.getAssociatedNotebook().getTitle(), note.getAccessLevel().getValue());
    }

    /**
     * Create a note in a given notebook
     *
     * @param noteRequest to create
     * @param parentId    notebook id
     * @return the created note
     */
    @PutMapping("query")
    public NoteContentPayload createNote(@RequestBody NoteContentPayload noteRequest, @RequestParam(value = "parentId") Long parentId) {
        if (noteRequest == null) {
            throw new IllegalArgumentException("Supplied note cannot be null");
        } else if (parentId == null) {
            throw new IllegalArgumentException("Parent id cannot be null");
        }

        Note note = noteService.createNote(noteRequest, parentId);
        noteRequest.setId(note.getId());

        return noteRequest;
    }

    /**
     * Save a given note
     *
     * @param noteRequest to save
     * @return success state
     */
    @PostMapping("query")
    public ResponseEntity<?> saveNote(@RequestBody NoteContentPayload noteRequest) {
        if (noteRequest == null || noteRequest.getId() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        noteService.update(noteRequest);

        return ResponseEntity.ok(new ApiResponse(true, "Saved"));
    }

    /**
     * Move a note to the starred notebook
     *
     * @param id the note id
     * @return success
     */
    @PostMapping("star")
    public ResponseEntity<?> starNote(@RequestParam(value = "id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        noteService.star(id);

        return ResponseEntity.ok(new ApiResponse(true, "Starred"));
    }

    /**
     * Delete a given note by id
     *
     * @param id to delete
     * @return success
     */
    @DeleteMapping("query")
    public ResponseEntity<?> deleteNote(@RequestParam(value = "id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        noteService.deleteNote(id);

        return ResponseEntity.ok(new ApiResponse(true, "Deleted"));
    }

}
