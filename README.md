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

`./gradlew ktlintFormat` - run ktlint auto-formatting

## Dependency Management

The project uses the following dependency management tools:

1. [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) - for managing version upgrades
2. [Dependency Check Plugin](https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html) - for
   checking for dependencies against OWASP vulnerabilities database

To run tasks:

`./gradlew dependencyUpdates` - checks for updates to current specified versions

`./gradlew dependencyCheckAnalyze` - checks dependencies for vulnerabilities

