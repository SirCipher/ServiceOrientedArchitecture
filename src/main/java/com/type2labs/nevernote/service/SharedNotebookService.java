package com.type2labs.nevernote.service;

import com.type2labs.nevernote.jpa.entity.Notebook;
import com.type2labs.nevernote.jpa.entity.SharedNotebook;
import com.type2labs.nevernote.jpa.respository.SharedNotebookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for shared notebooks
 */
@Service
public class SharedNotebookService {

    private final SharedNotebookRepository sharedNotebookRepository;
    private final NotificationService notificationService;

    @Autowired
    public SharedNotebookService(SharedNotebookRepository sharedNotebookRepository, NotificationService notificationService) {
        this.sharedNotebookRepository = sharedNotebookRepository;
        this.notificationService = notificationService;
    }

    /**
     * Finds all notebooks that have been shared with a given user ID
     *
     * @param id the user ID
     * @return a list of all shared notebooks
     */
    List<SharedNotebook> findSharedWithUserId(Long id) {
        return sharedNotebookRepository.findByUserId(id);
    }

    /**
     * Saves a given shared notebook and sends the recipient a notification
     *
     * @param sharedNotebook
     */
    void save(SharedNotebook sharedNotebook) {
        sharedNotebookRepository.save(sharedNotebook);
        notificationService.notifyUser(sharedNotebook.getUser(), "New notebook shared with you: "
                + sharedNotebook.getNotebook().getTitle() + " by "
                + sharedNotebook.getNotebook().getCreator().getUsername());
    }

    /**
     * Revokes all sharing for a given notebook
     *
     * @param notebook the notebook to unshare
     */
    void unshare(Notebook notebook) {
        List<SharedNotebook> matches = sharedNotebookRepository.findByNotebookId(notebook.getId());
        sharedNotebookRepository.deleteAll(matches);
    }
}
