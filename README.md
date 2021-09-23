# Simple Project

## What

A very simple Spring Boot application built using Gradle, Kotlin, Co-routines and Reactor, demonstrating basic configuration, setup and 
testing.

## Static Analysis

The project uses the following static analysis tools:

1. [Detekt](https://detekt.github.io/detekt/) - for code quality rule enforcement
2. [KtLint](https://ktlint.github.io/) - for code linting and auto-formatting

Both Detekt and KtLint will be run as part of the `check` build phase.

To run the tasks individually:

`./gradlew detekt` - runs detekt code analysis

`./gradlew ktlintCheck` - runs ktlint formatting checks

`./gradlew ktlintFormat` - run tklint auto-formatting

