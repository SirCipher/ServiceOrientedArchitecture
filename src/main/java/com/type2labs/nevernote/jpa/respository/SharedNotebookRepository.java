package com.type2labs.nevernote.jpa.respository;

import com.type2labs.nevernote.jpa.entity.SharedNotebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for shared notebooks
 */
@Repository
public interface SharedNotebookRepository extends JpaRepository<SharedNotebook, Long> {

    List<SharedNotebook> findByUserId(Long id);

    List<SharedNotebook> findByNotebookId(Long id);
}
