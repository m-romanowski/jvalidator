# JValidator
Lightweight java validation API

## Build and test

You can build project with gradle
```
gradlew build
```

and also run tests
```
gradlew test
```

## Usage

#### Built-in validation policies
- requireNonNull
- requireGreaterThanZero
- requireNonEmpty
- requireMatches

#### Built-in validation failure reasons
- IS_NULL
- IS_LESS_OR_EQUAL_ZERO
- IS_EMPTY
- DOESNT_MATCH

#### Create own validation policy

```java
ValidationPolicy requireGreaterThanOne(BigDecimal property, String propertyName) {
    if (Objects.isNull(property)) {
        return ValidationPolicy.invalid(ValidationFailureReason.of(propertyName, ValidationPolicy.IS_NULL));
    }

    return property.compareTo(BigDecimal.ONE) <= 0
        ? ValidationPolicy.invalid(ValidationFailureReason.of(propertyName, "is less or equal one"))
        : ValidationPolicy.valid();
}
```

## Example
```java
class ValidableObject {
    
    private final BigDecimal value;

    ValidableObject(BigDecimal value) {
        this.value = value;
    }
}

...

BigDecimal fieldToValidate = BigDecimal.ZERO;

ValidationResult<ValidableObject> validationResult = Validator.<ValidableObject>empty()
    .with(ValidationPolicy.requireGreaterThanZero(fieldToValidate, "value"))
    .create(() -> new ValidableObject(fieldToValidate))
    .validate();

validationResult.ifPresentOrElse(
    result -> System.out.println("SUCCESS with: " + result),
    failureReasons -> System.out.println("FAIL with: " + failureReasons)
);
```

## Built With
* [Gradle](https://gradle.org)
* [Lombok](https://projectlombok.org)
* [Spock](http://spockframework.org)

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
