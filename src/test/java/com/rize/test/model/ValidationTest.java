package com.rize.test.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
public class ValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    public void testValidationFailedOnEmpty() {
        Artist artist = Artist.builder().firstName("").build();
        Set<ConstraintViolation<Artist>> violations = validator.validate(artist);
        log.info("violations [{}]", violations);
        log.info("[{}] violations detected", violations.size());
        assertThat(violations.size()).isGreaterThan(0);
    }

    @Test
    public void testValidationPassedOnNotEmpty() {
        Artist artist = Artist.builder()
                .firstName("name")
                .category(ArtistCategory.ACTOR)
                .lastName("name")
                .build();
        Set<ConstraintViolation<Artist>> violations = validator.validate(artist);
        log.info("violations [{}]", violations);
        log.info("[{}] violations detected", violations.size());
        assertThat(violations.size()).isZero();
    }

}
