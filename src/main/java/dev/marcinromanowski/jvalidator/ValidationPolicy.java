package dev.marcinromanowski.jvalidator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static dev.marcinromanowski.jvalidator.ValidationFailureReasons.ValidationFailureReason;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationPolicy {

    public static final String IS_NULL = "Is null";
    public static final String IS_LESS_OR_EQUAL_ZERO = "Is less or equal zero";
    public static final String IS_EMPTY = "Is empty";
    public static final String DOESNT_MATCH = "Not matches validation policy";

    @Getter
    private final ValidationFailureReason failureReason;

    public boolean isValid() {
        return failureReason.isEmpty();
    }

    public static ValidationPolicy valid() {
        return new ValidationPolicy(ValidationFailureReason.empty());
    }

    public static ValidationPolicy invalid(ValidationFailureReason failureReason) {
        return new ValidationPolicy(failureReason);
    }

    public static <T> ValidationPolicy requireNonNull(T object, String propertyName) {
        return isNull(object)
            ? invalid(ValidationFailureReason.of(propertyName, IS_NULL))
            : valid();
    }

    public static ValidationPolicy requireGreaterThanZero(BigDecimal property, String propertyName) {
        if (isNull(property)) {
            return ValidationPolicy.invalid(ValidationFailureReason.of(propertyName, IS_NULL));
        }

        return property.compareTo(BigDecimal.ZERO) <= 0
            ? invalid(ValidationFailureReason.of(propertyName, IS_LESS_OR_EQUAL_ZERO))
            : valid();
    }

    public static ValidationPolicy requireNonEmpty(String property, String propertyName) {
        return nonNull(property) && !property.isEmpty()
            ? valid()
            : invalid(ValidationFailureReason.of(propertyName, IS_EMPTY));
    }

    public static ValidationPolicy requireMatches(Pattern pattern, CharSequence property, String propertyName) {
        if (isNull(property)) {
            return ValidationPolicy.invalid(ValidationFailureReason.of(propertyName, IS_NULL));
        }

        return !pattern.matcher(property).matches()
            ? invalid(ValidationFailureReason.of(propertyName, DOESNT_MATCH))
            : valid();
    }
}
