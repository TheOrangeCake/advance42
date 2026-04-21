package swingy.utils;

import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidatorClient {
    public static Validator validator;

    private ValidatorClient() {}

    public static Validator getValidator() {
        if (validator == null) {
            java.util.logging.Logger.getLogger("org.hibernate.validator")
                    .setLevel(java.util.logging.Level.SEVERE);
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                validator = factory.getValidator();
            } catch (ValidationException e) {
                System.err.println("Error: Fail to set up validator. Program terminated");
                System.exit(-1);
            }
        }
        return validator;
    }
}
