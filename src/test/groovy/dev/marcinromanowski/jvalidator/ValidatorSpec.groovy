package dev.marcinromanowski.jvalidator

import groovy.transform.TupleConstructor
import spock.lang.Specification

class ValidatorSpec extends Specification {

    def "Should throw exception when we tries to get value for not valid object"() {
        given: "validator for `ValidableObject`"
            BigDecimal fieldToValidate = 0.0
            Validator<ValidableObject> validableObjectValidator = Validator.<ValidableObject> empty()
                    .with(ValidationPolicy.requireGreaterThanZero(fieldToValidate, "value"))
                    .create(() -> new ValidableObject(value: fieldToValidate))

        when: "we validate our $validableObjectValidator"
            Validator.ValidationResult<ValidableObject> result = validableObjectValidator.validate()
        and: "we tries get not present value"
            result.get()
        then: "exception should be thrown"
            thrown NoSuchElementException
    }

    def "Should not throw any exception when object is validated successfully"() {
        given: "validator for `ValidableObject`"
            BigDecimal fieldToValidate = 1.0
            Validator<ValidableObject> validableObjectValidator = Validator.<ValidableObject> empty()
                    .with(ValidationPolicy.requireGreaterThanZero(fieldToValidate, "value"))
                    .create(() -> new ValidableObject(value: fieldToValidate))

        when: "we validate $validableObjectValidator"
            Validator.ValidationResult<ValidableObject> result = validableObjectValidator.validate()
        and: "we tries get present value"
            ValidableObject validableObject = result.get()
        then: "no exception is thrown and value is present"
            notThrown NoSuchElementException
            validableObject.value == fieldToValidate
    }

    def "Should validate every dependencies on fields"() {
        given: "field to validate"
            BigDecimal fieldToValidate = 1.0
        and: "validator for `ValidableObject`"
            Validator.Builder<ValidableObject> builder = Validator.<ValidableObject> empty()
        and: "`ValidableObject` policy definition"
            builder.with(ValidationPolicy.requireGreaterThanZero(fieldToValidate, "value"))
        and: "validator for `ValidableObject` depends on another `BigDecimal` validator"
            Validator<BigDecimal> dependsOnValueValidator = Validator.<BigDecimal> empty()
                    .with(ValidationPolicy.requireGreaterThanZero(dependsOnValue as BigDecimal, "dependsOnValue"))
                    .create(() -> dependsOnValue)

            Validator<ValidableObject> validableObjectValidator = builder
                    .dependsOn(dependsOnValueValidator)
                    .create(() -> new ValidableObject(fieldToValidate))

        when: "we tries to validate whole `ValidableObject`"
            Validator.ValidationResult<ValidableObject> result = validableObjectValidator.validate()
        then: "validator with dependencies should be validated"
            result.getOptional().isPresent() == isPresent
            result.getValidationFailureReasons().failureReasons.size() == failureReasons

        where:
            dependsOnValue | failureReasons || isPresent
            -1.0           | 2              || false
            0.0            | 2              || false
            1.0            | 0              || true
    }

    @TupleConstructor
    private static class ValidableObject {
        BigDecimal value
    }
}
