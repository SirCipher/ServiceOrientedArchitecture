package com.type2labs.nevernote.jpa.respository;

import com.type2labs.nevernote.jpa.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for notes
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

}
