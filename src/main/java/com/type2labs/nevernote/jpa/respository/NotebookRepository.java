package com.type2labs.nevernote.jpa.respository;

import com.type2labs.nevernote.jpa.entity.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for notebooks
 */
@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long> {

    List<Notebook> findAllByCreatorId(Long id);

    Notebook findByTitle(String s);

}
