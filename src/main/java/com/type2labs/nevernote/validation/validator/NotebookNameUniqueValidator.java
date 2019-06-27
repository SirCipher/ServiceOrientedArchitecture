package com.type2labs.nevernote.validation.validator;

import com.type2labs.nevernote.jpa.entity.Notebook;
import com.type2labs.nevernote.jpa.entity.User;
import com.type2labs.nevernote.validation.annotation.NotebookNameUnique;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validates that a {@link com.type2labs.nevernote.jpa.entity.Notebook}'s name is unique across the all the user's {@link com.type2labs.nevernote.jpa.entity.Notebook}s
 */
public class NotebookNameUniqueValidator implements ConstraintValidator<NotebookNameUnique, Notebook> {

    @Override
    public boolean isValid(Notebook notebook, ConstraintValidatorContext context) {

        /*
            Entered name is empty and the annotation's default message does not explain this to the user. Create a custom message
         */
        if (StringUtils.isEmpty(notebook.getTitle())) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate("Notebook name cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        User creator = notebook.getCreator();

        if (creator.getNotebooks() == null) {
            return true;
        }

        List<Notebook> userNotebooks = creator.getNotebooks();

        // This is the first entry
        if (userNotebooks.size() == 1) {
            return true;
        }

        List<String> names = userNotebooks.stream()
                // Notebook not equal to the target
                .filter(n -> n != notebook)
                // Notebook name not equal to target name
                .map(Notebook::getTitle).filter(e -> e.equalsIgnoreCase(notebook.getTitle()))
                .collect(Collectors.toList());

        return !(names.size() > 0);
    }
}
