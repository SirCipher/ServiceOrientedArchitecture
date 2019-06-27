package com.type2labs.nevernote.jpa.entity;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests all validation on a {@link User}
 */
public class UserTest {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    /**
     * Tests that {@link NotBlank} is the only violation returned
     */
    @Test
    public void testNull() {
        User user = new User();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        for (ConstraintViolation<User> cv : constraintViolations) {
            assertThat(cv.getConstraintDescriptor().getAnnotation(), instanceOf(NotBlank.class));
        }
    }

    /**
     * Tests that validation passes when all fields are populated correctly
     */
    @Test
    public void testPopulated() {
        User user = new User();
        user.setUsername("un");
        user.setFirstName("fn");
        user.setLastName("ln");
        user.setEmailAddress("a@a.com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertEquals(0, constraintViolations.size());
    }

    /**
     * Tests that validation fails when all fields are populated but a malformed email address is provided
     */
    @Test
    public void testEmailInvalid() {
        User user = new User();
        user.setUsername("un");
        user.setFirstName("fn");
        user.setLastName("ln");
        user.setEmailAddress("a@@@@a....com");
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertEquals(1, constraintViolations.size());
    }

}