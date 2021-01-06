package dev.marcinromanowski.jvalidator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Validator<T> {

    private final ValidationResult<T> validationResult;
    private final List<ValidationPolicy> validationPolicies;

    public static <T> Builder<T> empty() {
        return new Builder<>(new ArrayList<>());
    }

    public ValidationResult<T> validate() {
        if (isInvalid()) {
            ValidationFailureReasons failureReasons = ValidationFailureReasons.empty();
            validationPolicies.stream()
                .map(ValidationPolicy::getFailureReason)
                .forEach(failureReasons::add);
            return ValidationResult.withFailure(failureReasons);
        }

        return validationResult;
    }

    private boolean isInvalid() {
        return !validationPolicies.stream()
            .allMatch(ValidationPolicy::isValid);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder<T> {

        private final List<ValidationPolicy> validationPolicies;

        public Builder<T> with(ValidationPolicy validationPolicy) {
            requireNonNull(validationPolicy);
            validationPolicies.add(validationPolicy);
            return this;
        }

        public Builder<T> dependsOn(Validator<?> other) {
            requireNonNull(other);
            validationPolicies.addAll(other.validationPolicies);
            return this;
        }

        public Validator<T> create(Supplier<T> payload) {
            requireNonNull(payload);
            return new Validator<>(ValidationResult.of(payload), validationPolicies);
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ValidationResult<T> {

        private final Supplier<T> payload;
        private final ValidationFailureReasons failureReasons;

        public static <T> ValidationResult<T> of(Supplier<T> payload) {
            return new ValidationResult<>(payload, ValidationFailureReasons.empty());
        }

        public static <T> ValidationResult<T> withFailure(ValidationFailureReasons validationFailureReasons) {
            return new ValidationResult<>(null, validationFailureReasons);
        }

        public T get() {
            if (isNull(payload)) {
                throw new NoSuchElementException("No value present");
            }

            return payload.get();
        }

        public Optional<T> getOptional() {
            if (isNull(payload)) {
                return Optional.empty();
            }

            return Optional.of(payload.get());
        }

        public ValidationFailureReasons getValidationFailureReasons() {
            return failureReasons;
        }

        public void ifPresentOrElse(Consumer<T> validationSuccessConsumer, Consumer<ValidationFailureReasons> validationFailureConsumer) {
            if (nonNull(payload)) {
                validationSuccessConsumer.accept(payload.get());
            } else {
                validationFailureConsumer.accept(failureReasons);
            }
        }
    }
}
