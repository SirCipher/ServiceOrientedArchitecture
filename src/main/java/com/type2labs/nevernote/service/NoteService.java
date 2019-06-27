package com.type2labs.nevernote.service;

import com.type2labs.nevernote.controller.payload.NoteContentPayload;
import com.type2labs.nevernote.exception.ResourceNotFoundException;
import com.type2labs.nevernote.jpa.entity.AccessLevel;
import com.type2labs.nevernote.jpa.entity.Note;
import com.type2labs.nevernote.jpa.entity.Notebook;
import com.type2labs.nevernote.jpa.entity.User;
import com.type2labs.nevernote.jpa.respository.NoteRepository;
import com.type2labs.nevernote.jpa.respository.NotebookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for notes
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final NotebookRepository notebookRepository;
    private final CheckPrivilegeService checkPrivilegeService;
    // To avoid circular dependency
    @Autowired
    private NotebookService notebookService;

    @Autowired
    public NoteService(NoteRepository noteRepository, NotebookRepository notebookRepository, CheckPrivilegeService checkPrivilegeService) {
        this.noteRepository = noteRepository;
        this.notebookRepository = notebookRepository;
        this.checkPrivilegeService = checkPrivilegeService;
    }

    /**
     * Get a note by id
     *
     * @param id to get
     * @return the note
     */
    public Note getNote(Long id) {
        Optional<Note> note = noteRepository.findById(id);

        if (note.isPresent()) {
            Note matchedNote = note.get();
            AccessLevel accessLevel = checkPrivilegeService.note(matchedNote);
            matchedNote.setAccessLevel(accessLevel);
            return matchedNote;
        } else {
            throw new ResourceNotFoundException("Note", "id", id);
        }
    }

    /**
     * Initialise the user's notes and notebooks with the default
     *
     * @param user to initialise
     */
    public void initialiseForUser(User user) {
        Notebook notebook = new Notebook();
        notebook.setCreator(user);
        notebook.setTitle("Starred");

        Note note = new Note();
        note.setAssociatedNotebook(notebook);
        note.setTitle("My First Note");

        notebookRepository.save(notebook);
        noteRepository.save(note);
    }

    /**
     * Update a given notebook
     *
     * @param noteRequest to update
     */
    public void update(NoteContentPayload noteRequest) {
        Optional<Note> note = noteRepository.findById(noteRequest.getId());

        if (!note.isPresent()) {
            throw new ResourceNotFoundException("Note", "id", noteRequest.getId());
        }

        Note matchedNote = note.get();

        checkPrivilegeService.note(matchedNote);
        matchedNote.setHtml(noteRequest.getHtml());
        noteRepository.save(matchedNote);
    }

    /**
     * Create a note
     *
     * @param noteRequest to convert and create as an entity
     * @param parentId    notebook id
     * @return the created note
     */
    public Note createNote(NoteContentPayload noteRequest, Long parentId) {
        Note note = new Note();
        note.setTitle(noteRequest.getNotebookName());

        Optional<Notebook> match = notebookRepository.findById(parentId);

        if (!match.isPresent()) {
            throw new ResourceNotFoundException("Notebook", "id", parentId);
        }

        Notebook notebook = match.get();
        checkPrivilegeService.notebook(notebook);

        note.setAssociatedNotebook(notebook);
        noteRepository.save(note);

        return note;
    }

    /**
     * Delete a list of notes
     *
     * @param notes to delete
     */
    void deleteNotes(List<Note> notes) {
        for (Note n : notes) {
            deleteNote(n.getId());
        }
    }

    /**
     * Delete a single note
     *
     * @param id to delete
     */
    public void deleteNote(Long id) {
        Note note = getNote(id);
        noteRepository.delete(note);
    }

    /**
     * Star a given note by id and move it to the starred notebook
     *
     * @param id to star
     */
    public void star(Long id) {
        Note note = getNote(id);
        checkPrivilegeService.note(note);

        notebookService.moveToStarred(note);
        noteRepository.save(note);
    }
}
