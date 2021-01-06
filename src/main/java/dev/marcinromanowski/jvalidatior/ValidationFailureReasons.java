package dev.marcinromanowski.jvalidatior;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationFailureReasons {

    @Getter
    private final List<ValidationFailureReason> failureReasons;

    public static ValidationFailureReasons empty() {
        return new ValidationFailureReasons(new ArrayList<>());
    }

    public static ValidationFailureReasons of(ValidationFailureReason... validationFailureReasons) {
        return new ValidationFailureReasons(List.of(validationFailureReasons));
    }

    public void add(ValidationFailureReason validationFailureReason) {
        failureReasons.add(validationFailureReason);
    }

    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ValidationFailureReason {

        private static final ValidationFailureReason EMPTY = new ValidationFailureReason(null, null);

        @Getter
        private final String property;

        @Getter
        private final String reason;

        boolean isEmpty() {
            return this == EMPTY;
        }

        public static ValidationFailureReason of(String property, String reason) {
            requireNonNull(property);
            requireNonNull(reason);
            return new ValidationFailureReason(property, reason);
        }

        public static ValidationFailureReason empty() {
            return EMPTY;
        }
    }
}
