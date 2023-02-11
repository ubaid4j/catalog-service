package dev.ubaid.catalogservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BookValidationTests {
    private static Validator validator;
    
    @BeforeAll
    static void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }
    
    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var book = 
            new Book("1234567890", "Title", "Author", 9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }
    
    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails() {
        var book = 
            new Book("a234567890", "Title", "Author", 10.1);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.stream().findFirst().orElseThrow().getMessage())
            .isEqualTo("The ISBN format must be valid.");
    }
}
