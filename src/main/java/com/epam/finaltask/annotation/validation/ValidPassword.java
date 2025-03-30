package com.epam.finaltask.annotation.validation;

import com.epam.finaltask.validation.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters long, contain uppercase and lowercase letters, " +
            "a number, and a special character.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
