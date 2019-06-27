package com.type2labs.nevernote.service;

import com.type2labs.nevernote.controller.payload.NoteResponse;
import com.type2labs.nevernote.controller.payload.NotebookResponse;
import com.type2labs.nevernote.exception.BadRequestException;
import com.type2labs.nevernote.exception.ResourceNotFoundException;
import com.type2labs.nevernote.exception.ValidationException;
import com.type2labs.nevernote.jpa.entity.*;
import com.type2labs.nevernote.jpa.respository.NotebookRepository;
import com.type2labs.nevernote.jpa.respository.UserRepository;
import com.type2labs.nevernote.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer for notebooks
 */
@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;
    private final UserRepository userRepository;
    private final NoteService noteService;
    private final SharedNotebookService sharedNotebookService;
    private final CheckPrivilegeService checkPrivilegeService;

    @Autowired
    public NotebookService(NotebookRepository notebookRepository, UserRepository userRepository, NoteService noteService, SharedNotebookService sharedNotebookService, CheckPrivilegeService checkPrivilegeService) {
        this.notebookRepository = notebookRepository;
        this.userRepository = userRepository;
        this.noteService = noteService;
        this.sharedNotebookService = sharedNotebookService;
        this.checkPrivilegeService = checkPrivilegeService;
    }

    /**
     * Converts from a notebook entity to a DTO
     *
     * @param notebooks to convert
     * @return converted list
     */
    private List<NotebookResponse> mapToDto(List<Notebook> notebooks) {
        List<NotebookResponse> responses = new ArrayList<>();

        for (Notebook nb : notebooks) {
            NotebookResponse notebookResponse = new NotebookResponse(nb.getId(), nb.getTitle(), AccessLevel.READ_WRITE.getValue());

            for (Note note : nb.getNotes()) {
                notebookResponse.addNote(new NoteResponse(note.getId(), note.getTitle(), nb.getTitle()));
            }

            responses.add(notebookResponse);
        }

        return responses;
    }

    /**
     * Get all notebooks for a given user
     *
     * @param userId to fetch notebook sfor
     * @return a list of DTO notebooks
     */
    public List<NotebookResponse> getAllNotebooks(Long userId) {
        List<Notebook> myNotebooks = notebookRepository.findAllByCreatorId(userId);
        List<SharedNotebook> sharedWith = sharedNotebookService.findSharedWithUserId(userId);
        List<NotebookResponse> results = mapToDto(myNotebooks);

        for (SharedNotebook sharedNotebook : sharedWith) {
            Notebook nb = sharedNotebook.getNotebook();
            NotebookResponse notebookResponse = new NotebookResponse(nb.getId(), nb.getTitle(), sharedNotebook.getAccessLevel().getValue());

            for (Note note : nb.getNotes()) {
                notebookResponse.addNote(new NoteResponse(note.getId(), note.getTitle(), nb.getTitle()));
            }

            results.add(notebookResponse);
        }

        return results;
    }

    /**
     * Create a notebook for a given name
     *
     * @param name
     */
    public void createNotebook(String name) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("Registered user not found");
        }

        Notebook notebook = new Notebook();
        notebook.setCreator(userOptional.get());
        notebook.setTitle(name);
        userOptional.get().addNotebook(notebook);

        save(notebook);
    }

    /**
     * The requested notebook
     *
     * @param notebook to save
     */
    private void save(Notebook notebook) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Notebook>> constraintViolations = validator.validate(notebook);

        if (constraintViolations.size() != 0) {
            List<String> messages = new ArrayList<>();

            for (ConstraintViolation<Notebook> cv : constraintViolations) {
                String m = cv.getMessage();
                messages.add(m);
            }

            throw new ValidationException(messages);
        }

        notebookRepository.save(notebook);
    }

    /**
     * Deletes a given notebook
     *
     * @param id to delete
     */
    @SuppressWarnings("Duplicates")
    public void delete(Long id) {
        Optional<Notebook> notebook = notebookRepository.findById(id);
        if (!notebook.isPresent()) {
            throw new ResourceNotFoundException("Notebook", "id", id);
        }

        Notebook match = notebook.get();
        checkPrivilegeService.notebook(match);

        if (match.getTitle().equalsIgnoreCase("Starred")) {
            throw new BadRequestException("Cannot delete the starred notebook");
        }

        noteService.deleteNotes(match.getNotes());
        notebookRepository.deleteById(match.getId());
    }

    /**
     * Move the given note to the starred notebook
     *
     * @param note
     */
    void moveToStarred(Note note) {
        Notebook notebook = notebookRepository.findByTitle("Starred");
        note.setAssociatedNotebook(notebook);
    }

    /**
     * Get a notebook by id
     *
     * @param id to get
     * @return the requested notebook
     */
    private Notebook getNotebook(Long id) {
        Optional<Notebook> notebook = notebookRepository.findById(id);

        if (notebook.isPresent()) {
            Notebook matchedNote = notebook.get();
            checkPrivilegeService.notebook(matchedNote);
            return matchedNote;
        } else {
            throw new ResourceNotFoundException("Notebook", "id", id);
        }
    }

    /**
     * Shares a notebook with the requested email address
     *
     * @param email       to share the notebook with
     * @param access      the access level they will have
     * @param notebook_id to share
     */
    public void share(String email, String access, Long notebook_id) {
        AccessLevel accessLevel = AccessLevel.from(access);
        Notebook notebook = getNotebook(notebook_id);

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Can't share a notebook with yourself
        if (userPrincipal.getEmailAddress().equalsIgnoreCase(email)) {
            return;
        }

        if (!notebook.getCreator().getId().equals(userPrincipal.getId())) {
            throw new BadRequestException("You cannot share somebody elses notebook");
        }

        if ("Starred".equals(notebook.getTitle())) {
            throw new BadRequestException("Cannot shared starred notebook");
        }

        Optional<User> recipientOpt = userRepository.findByEmailAddress(email.toLowerCase());

        if (!recipientOpt.isPresent()) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        User user = recipientOpt.get();

        List<SharedNotebook> existing = sharedNotebookService.findSharedWithUserId(user.getId());

        long result = existing.stream().filter(e -> e.getUser().getId().equals(user.getId()) && e.getNotebook().getId().equals(notebook_id)).count();

        if (result != 0) {
            throw new BadRequestException("Notebook already shared with that user");
        }

        SharedNotebook sharedNotebook = new SharedNotebook(user, notebook, accessLevel);
        sharedNotebookService.save(sharedNotebook);
    }

    /**
     * Unshare a particular notebook
     *
     * @param notebook_id to unshare
     */
    @SuppressWarnings("Duplicates")
    public void unshare(Long notebook_id) {
        Optional<Notebook> optionalNotebook = notebookRepository.findById(notebook_id);

        if (!optionalNotebook.isPresent()) {
            throw new ResourceNotFoundException("Notebook", "id", notebook_id);
        }

        Notebook notebook = optionalNotebook.get();
        checkPrivilegeService.isMyNotebook(notebook);

        sharedNotebookService.unshare(notebook);
    }
}
