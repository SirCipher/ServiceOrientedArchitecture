package com.type2labs.nevernote.service;

import com.type2labs.nevernote.jpa.entity.*;
import com.type2labs.nevernote.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Handles all note and notebook access levels
 */
@Service
public class CheckPrivilegeService {

    private final AuthorisationService authorisationService;

    @Autowired
    public CheckPrivilegeService(AuthorisationService authorisationService) {
        this.authorisationService = authorisationService;
    }

    /**
     * If the target notebook is registered to the user principal
     *
     * @param notebook to check
     */
    void isMyNotebook(Notebook notebook) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!notebook.getCreator().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorised access of notebook");
        }
    }

    /**
     * If the user principal owns the notebook or it is shared with them
     *
     * @param notebook to check
     * @return the access level they have
     */
    public AccessLevel notebook(Notebook notebook) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = authorisationService.getUser(user.getId());

        if (!optionalUser.isPresent()) {
            throw new AccessDeniedException("Unauthorised access of notebook");
        }

        User userEntity = optionalUser.get();
        List<SharedNotebook> sharedNotebookList = userEntity.getNotebooksSharedWith();
        User notebookCreator = notebook.getCreator();

        for (SharedNotebook shared : sharedNotebookList) {
            if (shared.getUser().getId().equals(user.getId())) {
                return shared.getAccessLevel();
            }
        }

        if (!notebookCreator.getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorised access of notebook");
        }

        return AccessLevel.READ_WRITE;
    }

    /**
     * Convenience method for checking note access level
     *
     * @param note to check
     * @return the access level they have
     */
    public AccessLevel note(Note note) {
        return notebook(note.getAssociatedNotebook());
    }

}
