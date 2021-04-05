package com.ebarapp.ebar.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.ebarapp.ebar.model.type.RoleType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

class UserValidatorTests {

    public static Set<RoleType> roles;

    private Validator createValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        return localValidatorFactoryBean;
    }

    @BeforeAll
    static void instantiateRoles() {
        roles = new HashSet<>();
        roles.add(RoleType.ROLE_CLIENT);
    }

    @Test
    void shouldNotValidateWhenUsernameNull() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername(null);
        user.setFirstName("Jorge");
        user.setLastName("Diaz");
        user.setEmail("example@mail.com");
        user.setDni("34235645X");
        user.setPhoneNumber("+34 722345123");
        user.setPassword("random");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("username");
        assertThat(violation.getMessage()).isEqualTo("must not be null");
    }

    @Test
    void shouldNotValidateWhenFirstnameEmpty() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("");
        user.setLastName("Diaz");
        user.setEmail("example@mail.com");
        user.setDni("34235645X");
        user.setPhoneNumber("+34 722345123");
        user.setPassword("random");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("firstName");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenLastnameEmpty() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("Jorge");
        user.setLastName("");
        user.setEmail("example@mail.com");
        user.setDni("34235645X");
        user.setPhoneNumber("+34 722345123");
        user.setPassword("random");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("lastName");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenEmailEmpty() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("Jorge");
        user.setLastName("Diaz");
        user.setEmail("");
        user.setDni("34235645X");
        user.setPhoneNumber("+34 722345123");
        user.setPassword("random");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("email");
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void shouldNotValidateWhenEmailWrong() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("Jorge");
        user.setLastName("Diaz");
        user.setEmail("thisisnotamail");
        user.setDni("34235645X");
        user.setPhoneNumber("+34 722345123");
        user.setPassword("random");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("email");
        assertThat(violation.getMessage()).isEqualTo("must be a well-formed email address");
    }

    @Test
    void shouldNotValidateWhenPhoneNumberEmpty() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("Jorge");
        user.setLastName("Diaz");
        user.setEmail("example@mail.com");
        user.setDni("34235645X");
        user.setPhoneNumber("");
        user.setPassword("random");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("phoneNumber");
        assertThat(violation.getMessage()).isEqualTo("Must be a valid phone number");
    }

    @Test
    void shouldNotValidateWhenPhoneNumberWrong() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("Jorge");
        user.setLastName("Diaz");
        user.setEmail("example@mail.com");
        user.setDni("34235645X");
        user.setPhoneNumber("6237+233+345");
        user.setPassword("random");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("phoneNumber");
        assertThat(violation.getMessage()).isEqualTo("Must be a valid phone number");
    }

    @Test
    void shouldNotValidateWhenRolesNull() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("Jorge");
        user.setLastName("Diaz");
        user.setEmail("example@mail.com");
        user.setDni("34235645X");
        user.setPhoneNumber("+34 722345123");
        user.setPassword("random");
        user.setRoles(null);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isEqualTo(1);
        ConstraintViolation<User> violation = constraintViolations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("roles");
        assertThat(violation.getMessage()).isEqualTo("must not be null");
    }

    @ParameterizedTest
    @CsvSource({
            "sample@mail.com, +34 722 567 345",
            "sample122@mail.us.es, 722567345"
    })
    void shouldValidate(String email, String phone) {
        User user = new User();
        user.setUsername("jorgedz");
        user.setFirstName("Jorge");
        user.setLastName("Diaz");
        user.setEmail(email);
        user.setDni("34235645X");
        user.setPhoneNumber(phone);
        user.setPassword("sample");
        user.setRoles(roles);

        Validator validator = createValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

        assertThat(constraintViolations.size()).isZero();
    }

}
