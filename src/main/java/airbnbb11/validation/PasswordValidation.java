package airbnbb11.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface PasswordValidation {
    String message() default "Password must contain at least 1 uppercase letter (A-Z) at least one number and minimum 6 characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
