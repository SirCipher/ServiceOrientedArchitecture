package com.type2labs.nevernote.validation.annotation;

import com.type2labs.nevernote.validation.validator.NotebookNameUniqueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to validate a {@link com.type2labs.nevernote.jpa.entity.User}'s notebook name is unique across the {@link com.type2labs.nevernote.jpa.entity.Notebook} collection
 */
@Documented
@Constraint(validatedBy = NotebookNameUniqueValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotebookNameUnique {

    String message() default "A notebook's name must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}