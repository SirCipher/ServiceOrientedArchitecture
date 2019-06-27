package com.type2labs.nevernote.validation.validator;

import com.type2labs.nevernote.jpa.entity.Notebook;
import com.type2labs.nevernote.jpa.entity.User;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Tests the notebook name validator and ensures that only unique names are allowed
 */
public class NotebookNameUniqueValidatorTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    /**
     * Tests that validation fails when the notebook name is null
     */
    @Test
    public void testNull() {
        Notebook notebook = new Notebook();
        User user = new User();

        notebook.setCreator(user);
        user.addNotebook(notebook);

        Set<ConstraintViolation<Notebook>> constraintViolations = validator.validate(notebook);

        assertEquals(1, constraintViolations.size());
    }

    /**
     * Tests that validation passes when the first notebook name is created
     */
    @Test
    public void testSingleName() {
        Notebook notebook = new Notebook();
        User user = new User();

        notebook.setCreator(user);
        notebook.setTitle("SomeUniqueName");
        user.addNotebook(notebook);

        Set<ConstraintViolation<Notebook>> constraintViolations = validator.validate(notebook);

        assertEquals(0, constraintViolations.size());
    }

    /**
     * Tests that validation fails when two notebooks have the same name
     */
    @Test
    public void testDuplicateName() {
        Notebook notebook = new Notebook();
        User user = new User();

        notebook.setCreator(user);
        notebook.setTitle("SomeUniqueName");
        user.addNotebook(notebook);

        notebook = new Notebook();
        notebook.setCreator(user);
        notebook.setTitle("SomeUniqueName");
        user.addNotebook(notebook);

        Set<ConstraintViolation<Notebook>> constraintViolations = validator.validate(notebook);

        assertEquals(1, constraintViolations.size());
    }

    /**
     * Tests that validation fails when when multiple notebooks exist and a notebook with a null name is added
     */
    @Test
    public void testPopulatedAndNullEntry() {
        Notebook notebook = new Notebook();
        User user = new User();

        notebook.setCreator(user);
        notebook.setTitle("SomeUniqueName");
        user.addNotebook(notebook);

        notebook = new Notebook();
        notebook.setCreator(user);
        notebook.setTitle("AnotherUniqueName");
        user.addNotebook(notebook);

        notebook = new Notebook();
        notebook.setCreator(user);
        user.addNotebook(notebook);

        Set<ConstraintViolation<Notebook>> constraintViolations = validator.validate(notebook);

        assertEquals(1, constraintViolations.size());
    }

}