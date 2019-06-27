package com.type2labs.nevernote.controller;

import com.type2labs.nevernote.controller.payload.ApiResponse;
import com.type2labs.nevernote.controller.payload.NotebookResponse;
import com.type2labs.nevernote.security.UserPrincipal;
import com.type2labs.nevernote.service.NotebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles all notebook requests
 */
@RestController
@RequestMapping("/api/notebooks")
public class NotebookController {

    private final NotebookService notebookService;

    @Autowired
    public NotebookController(NotebookService notebookService) {
        this.notebookService = notebookService;
    }

    /**
     * Create the requested notebook using the provided name
     *
     * @param name to create
     * @return success state
     */
    @PutMapping("query")
    public ResponseEntity<ApiResponse> createNotebook(@RequestParam(value = "name") String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        notebookService.createNotebook(name);

        return ResponseEntity.ok(new ApiResponse(true, "Notebook created"));
    }

    /**
     * Delete the requested notebook
     *
     * @param id to delete
     * @return success state
     */
    @DeleteMapping("query")
    public ResponseEntity<?> deleteNotebook(@RequestParam(value = "id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        notebookService.delete(id);

        return ResponseEntity.ok(new ApiResponse(true, "Deleted"));
    }

    /**
     * Returns the user's notebooks and notebooks that have been shared with them
     *
     * @return the list of notebooks
     */
    @GetMapping("/")
    public List<NotebookResponse> getAllNotebooks() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notebookService.getAllNotebooks(user.getId());
    }

    /**
     * Shares a notebook with a given user
     *
     * @param email       recipient's email
     * @param access      level for the recipient
     * @param notebook_id to share
     * @return success state
     */
    @PostMapping("share")
    public ResponseEntity<?> shareNotebook(@RequestParam(value = "email") String email, @RequestParam(value = "access") String access, @RequestParam(value = "notebook_id") Long notebook_id) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(access) || notebook_id == null) {
            throw new IllegalArgumentException("Email, access level and notebook id must be supplied");
        }

        notebookService.share(email, access, notebook_id);

        return ResponseEntity.ok(new ApiResponse(true, "Shared"));
    }

    /**
     * Unshares a notebook completely
     *
     * @param notebook_id to unshare
     * @return success
     */
    @DeleteMapping("share")
    public ResponseEntity<?> unShare(@RequestParam(value = "notebook_id") Long notebook_id) {
        if (notebook_id == null) {
            throw new IllegalArgumentException("Notebook id must be supplied");
        }

        notebookService.unshare(notebook_id);

        return ResponseEntity.ok(new ApiResponse(true, "Unshared"));
    }


}
