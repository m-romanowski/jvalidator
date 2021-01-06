package dev.marcinromanowski.jvalidatior

import spock.lang.Specification

import java.util.regex.Pattern

class ValidationPolicySpec extends Specification {

    def "Should validate properties with validator"() {
        expect:
            def validationPolicy = ValidationPolicy."$validator"(value, "test_property")
            validationPolicy.isValid() == isValid
        where:
            validator                | value         || isValid
            "requireNonNull"         | "valid_value" || true
            "requireNonNull"         | ""            || true
            "requireNonNull"         | null          || false
            "requireGreaterThanZero" | 1.0           || true
            "requireGreaterThanZero" | 0.0           || false
            "requireGreaterThanZero" | -1.0          || false
            "requireNonEmpty"        | "valid_value" || true
            "requireNonEmpty"        | ""            || false
            "requireNonEmpty"        | null          || false
    }

    def "Should validate pattern matches"() {
        expect:
            def validationPolicy = ValidationPolicy.requireMatches(pattern, value, "test_property")
            validationPolicy.isValid() == isValid
        where:
            pattern                   | value           || isValid
            Pattern.compile("[1-9]+") | null            || false
            Pattern.compile("[1-9]+") | ""              || false
            Pattern.compile("[1-9]+") | "1"             || true
            Pattern.compile("[1-9]+") | "invalid_value" || false
    }
}
